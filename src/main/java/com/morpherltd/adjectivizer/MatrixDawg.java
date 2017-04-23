package com.morpherltd.adjectivizer;

import com.google.common.base.Joiner;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

class MatrixDawg<TPayload> implements IDawg <TPayload> {
    final Class<TPayload> cls;

    @Override
    public TPayload get(final Iterable<Character> word) {
        int node_i = rootNodeIndex;

        for (char c : word)
        {
            int childIndexPlusOne = getChildIndexPlusOne(node_i, c);

            if (childIndexPlusOne == 0) return NewInstance.make(cls);

            node_i = childIndexPlusOne - 1;
        }

        if (node_i == -1) return NewInstance.make(cls);

        if (node_i < payloads.length) {
            return payloads[node_i];
        } else {
            return NewInstance.make(cls);
        }
    }

    ArrayList<Integer> getPath(Iterable<Character> word) {
        ArrayList<Integer> result = new ArrayList<Integer>();

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
            Iterable<Character> prefix) {
        ArrayList<Map.Entry<String, TPayload>> result = new ArrayList<Map.Entry<String, TPayload>>();

        String prefixStr = Joiner.on("").join(prefix);

        ArrayList<Integer> path = getPath(prefix);
        int node_i = prefixStr.length() == 0 ? rootNodeIndex : path.get(path.size() - 1);

        Deque<StackItem> stack = new ArrayDeque<StackItem>();

        if (node_i != -1)
        {
            if (node_i < payloads.length)
            {
                TPayload payload = payloads[node_i];

                if (!NewInstance.make(cls).equals(payload))
                {
                    result.add(new AbstractMap.SimpleEntry<String, TPayload>(
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

                            if (!NewInstance.make(cls).equals(payload))
                            {
                                result.add(new AbstractMap.SimpleEntry<String, TPayload>(
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
    public void SaveAsOldDawg (MReader stream, BiConsumer<DataOutputStream, TPayload> writePayload) {
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

    public MatrixDawg (final MReader reader,
                       ExecutionStrategy<MReader, TPayload> readPayload,
                       Class<TPayload> c)
            throws IOException {
        cls = c;

        // The nodes are grouped by (has payload, has children).
        nodeCount = reader.readInt();

        rootNodeIndex = reader.readInt();

        payloads = readArray(reader, readPayload);

        Character[] tmpChars = readArray(reader, new ExecutionStrategy<MReader, Character>() {
            @Override
            public Character apply(MReader r) {
                try {
                    return reader.readChar();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        StringBuilder sb = new StringBuilder();
        for (char aa : tmpChars) {
            sb.append(aa);
        }
        indexToChar = sb.toString().toCharArray();

        charToIndexPlusOne = getCharToIndexPlusOneMap(indexToChar);

        children1 = readChildren(reader, indexToChar);
        children0 = readChildren(reader, indexToChar);

        firstChar = indexToChar[0];
        lastChar = indexToChar[indexToChar.length - 1];
    }

    public static short[] getCharToIndexPlusOneMap(char [] uniqueChars) {
        if (uniqueChars.length == 0) return null;

        short to = (short) uniqueChars[uniqueChars.length - 1];
        short from = (short) uniqueChars[0];
        int toInt = to >= 0 ? to : 0x10000 + to;
        int fromInt = from >= 0 ? from : 0x10000 + from;

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
            short childCount = YaleDawg.readInt(
                reader, indexToChar.length + 1
            );

            for (short child_i = 0; child_i < childCount; ++child_i)
            {
                short charIndex = YaleDawg.readInt(
                    reader, indexToChar.length
                );
                int childNodeIndex = reader.readInt();

                children [node_i][charIndex] = childNodeIndex + 1;
            }
        }

        return children;
    }


    public static <T> T[] readArray(MReader reader,
                                    ExecutionStrategy<MReader, T> read)
            throws IOException {
        int len = reader.readInt();

        T[] result = (T[]) new Object[len];
        int i = 0;
        for (T t: readSequence(reader, read)) {
            result[i] = t;
            i++;
            if (i == len) break;
        }

        return result;
    }

    public static Character[] readCharArray(MReader reader,
                                            ExecutionStrategy<MReader, Character> read)
        throws IOException {
        int len = reader.readInt();

        Character[] result = new Character[len];
        int i = 0;
        for (Character t: readSequence(reader, read)) {
            result[i] = t;
            i++;
            if (i == len) break;
        }

        return result;
    }

    static <T> Iterable<T> readSequence(final MReader reader,
                                        final ExecutionStrategy<MReader, T> read)
    {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
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
        };
    }

}
