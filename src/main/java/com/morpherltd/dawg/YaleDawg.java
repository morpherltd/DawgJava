package com.morpherltd.dawg;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static com.morpherltd.dawg.MatrixDawg.getCharToIndexPlusOneMap;
import static com.morpherltd.dawg.MatrixDawg.readArray;

public class YaleDawg<TPayload> implements IDawg<TPayload> {
    private final Class<TPayload> cls;

    class Child
    {
        public final int Index;
        public final short CharIndex;

        public Child(int index, short charIndex)
        {
            Index = index;
            CharIndex = charIndex;
        }
    }

    private final TPayload[] payloads;
    private final char[] indexToChar;
    private final short[] charToIndexPlusOne;
    private final int nodeCount, rootNodeIndex;
    private final char firstChar;
    private final char lastChar;
    private final int [] firstChildForNode;
    private final Child [] children; // size = NNZ

    public YaleDawg (DataInputStream reader,
                     Function<DataInputStream, TPayload> readPayload,
                     Class<TPayload> c)
            throws IOException {
        cls = c;

        // The nodes are grouped by (has payload, has children).
        nodeCount = reader.readInt();

        rootNodeIndex = reader.readInt();

        payloads = MatrixDawg.<TPayload>readArray(reader, readPayload);

        Character[] tmpChars = MatrixDawg.<Character>readArray(reader, (DataInputStream r) -> {
            try {
                return r.readChar();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        indexToChar = tmpChars.toString().toCharArray();

        charToIndexPlusOne = MatrixDawg.<TPayload>getCharToIndexPlusOneMap(indexToChar);

        firstChildForNode = new int[nodeCount+1];

        int firstChildForNode_i = 0;

        int totalChildCount = reader.readInt();

        children = (Child[]) new Object[totalChildCount];

        firstChildForNode [nodeCount] = totalChildCount;

        int globalChild_i = 0;

        for (int child1_i = 0; child1_i < nodeCount; ++child1_i)
        {
            firstChildForNode [firstChildForNode_i++] = globalChild_i;

            short childCount = readInt(reader, indexToChar.length + 1);

            for (short child_i = 0; child_i < childCount; ++child_i)
            {
                short charIndex = readInt(reader, indexToChar.length);
                int childNodeIndex = reader.readInt();

                children [globalChild_i++] = new Child(childNodeIndex, charIndex);
            }
        }

        firstChar = indexToChar[0];
        lastChar = indexToChar[indexToChar.length - 1];
    }

    static short readInt(DataInputStream reader, int countOfPossibleValues)
            throws IOException {
        return countOfPossibleValues > 256 ? reader.readShort() : reader.readByte();
    }

    public class ChildComparer implements Comparator<Child> {
        @Override
        public int compare(Child x, Child y) {
            return x.CharIndex - y.CharIndex; // TODO correct?
        }
    }

    final ChildComparer childComparer = new ChildComparer();

    @Override
    public TPayload get(final Iterable<Character> word)
        throws IllegalAccessException, InstantiationException {
        ArrayList<Integer> p = getPath(word);
        int node_i = p.get(p.size() - 1);

        if (node_i == -1) return cls.newInstance();

        return getPayload(node_i);
    }

    private TPayload getPayload(int node_i)
        throws IllegalAccessException, InstantiationException {
        return node_i < payloads.length ? payloads [node_i] : cls.newInstance();
    }

    ArrayList<Integer> getPath (Iterable<Character> word) {
        ArrayList<Integer> result = new ArrayList<>();

        int node_i = rootNodeIndex;

        if (node_i == -1) {
            result.add(-1);
            return result;
        }

        result.add(node_i);

        for (char c : word)
        {
            if (c < firstChar || c > lastChar) {
                result.add(-1);
                return result;
            }

            int firstChild_i = firstChildForNode[node_i];

            int lastChild_i = firstChildForNode[node_i + 1];

            short charIndexPlusOne = charToIndexPlusOne[c - firstChar];

            if (charIndexPlusOne == 0) {
                result.add(-1);
                return result;
            }

            int nChildren = lastChild_i - firstChild_i;

            short charIndex = (short) (charIndexPlusOne - 1);

            int child_i;
            if (nChildren == 1) {
                child_i = children[firstChild_i].CharIndex == charIndex ? firstChild_i : -1;
            } else {
                Child searchValue = new Child(-1, charIndex);

                child_i = Arrays.binarySearch(children, firstChild_i, firstChild_i + nChildren, searchValue, childComparer);
            }

            if (child_i < 0) {
                result.add(-1);
                return result;
            }

            node_i = children[child_i].Index;

            result.add(node_i);
        }
        return result;
    }

    @Override
    public int getLongestCommonPrefixLength(final Iterable<Character> word) {
        ArrayList<Integer> a = getPath(word);
        int count = 0;
        for (int i : a) {
            if (i != -1) {
                count++;
            }
        }
        return count - 1;
    }

    @Override
    public Iterable<Map.Entry<String, TPayload>> matchPrefix(final Iterable<Character> prefix) throws IllegalAccessException, InstantiationException {
        String prefixStr = prefix.toString();

        ArrayList<Integer> p = getPath(prefix);
        int node_i = p.get(p.size() - 1);

        StringBuilder sb = new StringBuilder(prefixStr);

        return matchPrefix(sb, node_i);
    }


    private ArrayList<Map.Entry<String, TPayload>> matchPrefix (
            StringBuilder sb, int node_i)
            throws IllegalAccessException, InstantiationException {
        ArrayList<Map.Entry<String, TPayload>> result = new ArrayList<>();

        if (node_i != -1)
        {
            TPayload payload = getPayload(node_i);

            if (!payload.equals(cls.newInstance()))
            {
                result.add(new AbstractMap.SimpleEntry<>(sb.toString(), payload));
            }

            int firstChild_i = firstChildForNode [node_i];

            int lastChild_i = node_i + 1 < nodeCount
                ? firstChildForNode[node_i + 1]
                : children.length;

            for (int i = firstChild_i; i < lastChild_i; ++i)
            {
                Child child = children [i];

                sb.append(indexToChar [child.CharIndex]);

                for (Map.Entry<String, TPayload> pair
                    : matchPrefix (sb, child.Index))
                {
                    result.add(pair);
                }

                sb.setLength(sb.length() - 1);
            }
        }
        return result;
    }


    @Override
    public int getNodeCount() {
        return nodeCount;
    }
}
