package com.morpherltd.dawg;

import com.morpherltd.dawg.helpers.MReader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MatrixDawg<TPayload> implements IDawg <TPayload> {
    final Class<TPayload> cls;

    @Override
    public TPayload get(final Iterable<Character> word)
            throws IllegalAccessException, InstantiationException {
        int node_i = rootNodeIndex;

        for (char c : word)
        {
            int childIndexPlusOne = getChildIndexPlusOne(node_i, c);

            if (childIndexPlusOne == 0) return cls.newInstance();

            node_i = childIndexPlusOne - 1;
        }

        if (node_i == -1) return cls.newInstance();

        return node_i < payloads.length ?
            payloads[node_i] : cls.newInstance();
    }

    ArrayList<Integer> getPath(Iterable<Character> word) {
        ArrayList<Integer> result = new ArrayList<>();

        int node_i = rootNodeIndex;
        result.add(node_i);

        for (char c : word)
        {
            int childIndexPlusOne = getChildIndexPlusOne(node_i, c);

            if (childIndexPlusOne == 0)
            {
                result.add(-1);
                return result;
            }

            node_i = childIndexPlusOne - 1;

            result.add(node_i);
        }
        return result;
    }

    int getChildIndexPlusOne (int node_i, char c) {
        int[][] children = node_i < payloads.length ? children1 : children0;

        if (node_i >= payloads.length) node_i -= payloads.length;

        if (node_i >= children.length) return 0; // node has no children

        if (c < firstChar) return 0;
        if (c > lastChar) return 0;

        short charIndexPlusOne = charToIndexPlusOne [c - firstChar];

        if (charIndexPlusOne == 0) return 0;

        return children [node_i][charIndexPlusOne - 1];
    }

    @Override
    public int getLongestCommonPrefixLength(final Iterable<Character> word) {
        int count = 0;
        for (int i : getPath(word)) {
            if (i != -1) {
                count++;
            }
        }
        return count - 1;
    }

    class StackItem
    {
        public int node_i, child_i;
    }


    @Override
    public ArrayList<Map.Entry<String, TPayload>> matchPrefix(
            Iterable<Character> prefix)
            throws IllegalAccessException, InstantiationException {
        ArrayList<Map.Entry<String, TPayload>> result = new ArrayList<>();

        String prefixStr = prefix.toString();

        ArrayList<Integer> path = getPath(prefix);
        int node_i = prefixStr.length() == 0 ? rootNodeIndex : path.get(path.size() - 1);

        Deque<StackItem> stack = new ArrayDeque<>();

        if (node_i != -1)
        {
            if (node_i < payloads.length)
            {
                TPayload payload = payloads[node_i];

                if (!cls.newInstance().equals(payload))
                {
                    result.add(new AbstractMap.SimpleEntry<>(
                        prefixStr, payload
                    ));
                }
            }

            StringBuilder sb = new StringBuilder(prefixStr);

            int child_i = -1;

            for (;;)
            {
                int[][] children = node_i < payloads.length ? children1 : children0;

                int adj_node_i = (node_i >= payloads.length)
                    ? node_i - payloads.length
                    : node_i;

                if (adj_node_i < children.length)
                {
                    int next_child_i = child_i + 1;

                    for (; next_child_i < indexToChar.length; ++next_child_i)
                    {
                        if (children [adj_node_i][next_child_i] != 0)
                        {
                            break;
                        }
                    }

                    if (next_child_i < indexToChar.length)
                    {
                        StackItem si = new StackItem();
                        si.node_i = node_i;
                        si.child_i = next_child_i;
                        stack.push(si);
                        sb.append(indexToChar [next_child_i]);
                        node_i = children [adj_node_i][next_child_i] - 1;

                        if (node_i < payloads.length)
                        {
                            TPayload payload = payloads [node_i];

                            if (!cls.newInstance().equals(payload))
                            {
                                result.add(new AbstractMap.SimpleEntry<>(
                                    sb.toString(), payload
                                ));
                            }
                        }

                        continue;
                    }
                }

                // No (more) children.

                if (stack.size() == 0) break;

                sb.setLength(sb.length() - 1);
                StackItem item = stack.pop();

                node_i = item.node_i;
                child_i = item.child_i;
            }
        }

        return result;
    }

    // Todo: first is DataInputStream?
    public void SaveAsOldDawg (MReader stream,
                               BiConsumer<DataOutputStream, TPayload>
                                   writePayload) {
        throw new NotImplementedException();
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    private final TPayload[] payloads;
    private final int[][] children1;
    private final int[][] children0;
    private final char[] indexToChar;
    private final short[] charToIndexPlusOne;
    private final int nodeCount, rootNodeIndex;
    private final char firstChar;
    private final char lastChar;

    public MatrixDawg (MReader reader,
                       Function<MReader, TPayload> readPayload,
                       Class<TPayload> c)
            throws IOException {
        cls = c;

        // The nodes are grouped by (has payload, has children).
        nodeCount = reader.readInt();

        rootNodeIndex = reader.readInt();

        payloads = readArray(reader, readPayload);

        Character[] tmpChars = readArray(reader, (MReader r) -> {
            try {
                return reader.readChar();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        indexToChar = tmpChars.toString().toCharArray();

        charToIndexPlusOne = getCharToIndexPlusOneMap(indexToChar);

        children1 = readChildren(reader, indexToChar);
        children0 = readChildren(reader, indexToChar);

        firstChar = indexToChar[0];
        lastChar = indexToChar[indexToChar.length - 1];
    }

    public static short[] getCharToIndexPlusOneMap(char [] uniqueChars) {
        if (uniqueChars.length == 0) return null;

        System.out.println("unique len: " + uniqueChars.length);

        System.out.println("binaries:");
        for (int i = 0; i < uniqueChars.length; ++i)
            System.out.println(Integer.toBinaryString(0x100 + uniqueChars[i]).substring(2));
        System.out.println("--------");

        short to = (short) uniqueChars[uniqueChars.length - 2];
        short from = (short) uniqueChars[0];
        int toInt = to >= 0 ? to : 0x10000 + to;
        int fromInt = from >= 0 ? from : 0x10000 + from;

        System.out.println("to: " + toInt);
        System.out.println("from: " + fromInt);

        System.out.println("len: " + (uniqueChars[uniqueChars.length - 1] - uniqueChars[0] + 1));

        short[] charToIndex = new short[
            toInt - fromInt + 1
        ];

        for (int i = 0; i < uniqueChars.length; ++i)
        {
            short curcur = (short) uniqueChars[i];
            int cur = curcur >= 0 ? curcur : 0x10000 + curcur;
            charToIndex[cur - fromInt] = (short) (i + 1);
        }

        return charToIndex;
    }

    private static <TPayload> int[][] readChildren(MReader reader,
                                        char[] indexToChar) throws IOException {
        int nodeCount = reader.readInt();

        int[][] children = new int[nodeCount][indexToChar.length];

        for (int node_i = 0; node_i < nodeCount; ++node_i)
        {
            short childCount = YaleDawg.<TPayload>readInt(
                reader, indexToChar.length + 1
            );

            for (short child_i = 0; child_i < childCount; ++child_i)
            {
                short charIndex = YaleDawg.<TPayload>readInt(
                    reader, indexToChar.length
                );
                int childNodeIndex = reader.readInt();

                children [node_i][charIndex] = childNodeIndex + 1;
            }
        }

        return children;
    }


    public static <T> T[] readArray(MReader reader,
                                    Function<MReader, T> read)
            throws IOException {
        int len = reader.readInt();

        System.out.println("len: " + len);

        T[] result = (T[]) new Object[len];
        int i = 0;
        for (T t: readSequence(reader, read)) {
            result[i] = t;
            i++;
            System.out.println("read array payload num: " + i);
            if (i == len) break;
        }

        return result;
    }

    public static Character[] readCharArray(MReader reader,
                                    Function<MReader, Character> read)
        throws IOException {
        int len = reader.readInt();

        System.out.println("len: " + len);

        Character[] result = new Character[len];
        int i = 0;
        for (Character t: readSequence(reader, read)) {
            result[i] = t;
            i++;
            System.out.println("read char array payload num: " + i);
            if (i == len) break;
        }

        return result;
    }

    static <T> Iterable<T> readSequence(MReader reader,
                                        Function<MReader, T> read)
    {
        return () -> new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return true;
            }
            @Override
            public T next() {
                return read.apply(reader);
            }
            @Override
            public void remove() {
                throw new RuntimeException("Not supported");
            }
        };
    }

}
