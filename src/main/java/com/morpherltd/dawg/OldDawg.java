package com.morpherltd.dawg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class OldDawg<TPayload> implements IDawg<TPayload> {
    final Node<TPayload> root;
    final Class<TPayload> cls;

    @Override
    public TPayload get(Iterable<Character> word) {
        Node<TPayload> node = findNode(word);

        if (node == null) {
            try {
                return cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return node.getPayload();
        }
    }

    private Node<TPayload> findNode(Iterable<Character> word) {
        Node<TPayload> node = root;

        for (Character c : word) {
            node = node.getChild(c);

            if (node == null) return null;
        }

        return node;
    }

    @Override
    public int getLongestCommonPrefixLength(Iterable<Character> word)
    {
        Node<TPayload> node = root;
        int len = 0;

        for (char c : word)
        {
            node = node.getChild(c);

            if (node == null) break;

            ++len;
        }

        return len;
    }

    @Override
    public Iterable<Map.Entry<String, TPayload>> matchPrefix(Iterable<Character> prefix) {
        Node<TPayload> node = findNode(prefix);

//        if (node == null) return Enumerable.Empty <KeyValuePair <string, TPayload>> ();

        StringBuilder sb = new StringBuilder ();

        sb.append(prefix.toString());

        return new PrefixMatcher<TPayload>(sb).matchPrefix(node);
    }

    public class NodeByPayloadComparer implements Comparator<Node<TPayload>> {
        @Override
        public int compare(Node<TPayload> x, Node<TPayload> y) {
            boolean xb = x.hasPayload();
            boolean yb = y.hasPayload();
            return - (xb == yb ? 0 : (xb ? 1 : -1));
        }
    }

    public void SaveAsYaleDawg (DataOutputStream writer,
                                BiConsumer<DataOutputStream, TPayload>
                                    writePayload) throws IOException {
        final int version = 2;
        writer.writeInt(version);

        ArrayList<Node<TPayload>> allNodes = root.getAllDistinctNodes();


        Collections.sort(allNodes, new NodeByPayloadComparer());
        int totalChildCount = 0;
        for (Node<TPayload> n: allNodes){
            totalChildCount += n.children().size();
        }

        writer.writeInt(allNodes.size());

        HashMap<Node<TPayload>, Integer> nodeIndex = new HashMap<>();
        for (int i = 0; i < allNodes.size(); i++) {
            nodeIndex.put(allNodes.get(i), i);
        }

        int rootNodeIndex = -1;
        if (nodeIndex.containsKey(root)) {
            rootNodeIndex = nodeIndex.get(root);
        }

        writer.writeInt(rootNodeIndex);

        ArrayList<Node<TPayload>> nodesWithPayloads = new ArrayList<>();
        for (Node<TPayload> n : allNodes) {
            if (n.hasPayload()) {
                nodesWithPayloads.add(n);
            } else {
                break;
            }
        }

        writer.writeInt(nodesWithPayloads.size());

        for (Node<TPayload> node : nodesWithPayloads) {
            writePayload.accept(writer, node.getPayload());
        }

        Set<Character> allChars = new HashSet<>();
        for (Node<TPayload> node : allNodes) {
            allChars.addAll(node.children().keySet());
        }
        writer.writeInt(allChars.size());

        ArrayList<Character> sortedAllChars = new ArrayList<>(allChars);
        Collections.sort(sortedAllChars);
        for (char c : sortedAllChars) {
            writer.writeChar(c);
        }

        writer.writeInt(totalChildCount);

        char[] allCharsArr = sortedAllChars.toString().toCharArray();

        writeChildrenNoLength(writer, allNodes, nodeIndex, allCharsArr);
    }

    public void saveAsMatrixDawg(DataOutputStream writer,
                                 BiConsumer<DataOutputStream, TPayload>
                                     writePayload) throws IOException {
        final int version = 1;
        writer.writeInt(version);

        ArrayList<Node<TPayload>> allNodes = root.getAllDistinctNodes();

        writer.writeInt(allNodes.size());

        Node<TPayload>[][] nodes = (Node<TPayload>[][]) new Object[2][2];

        ArrayList<Node<TPayload>> hasBoth = new ArrayList<>();
        ArrayList<Node<TPayload>> hasPayload = new ArrayList<>();
        ArrayList<Node<TPayload>> hasChildren = new ArrayList<>();
        ArrayList<Node<TPayload>> hasNothing = new ArrayList<>();

        for (Node<TPayload> n : allNodes) {
            if (n.hasPayload() && n.hasChildren()) {
                hasBoth.add(n);
            } else if (n.hasPayload()) {
                hasPayload.add(n);
            } else if (n.hasChildren()) {
                hasChildren.add(n);
            } else {
                hasNothing.add(n);
            }
        }

        ArrayList<Node<TPayload>> nodesWithPayloads = new ArrayList<>();
        nodesWithPayloads.addAll(hasBoth);
        nodesWithPayloads.addAll(hasPayload);

        ArrayList<Node<TPayload>> all = new ArrayList<>();
        all.addAll(hasBoth);
        all.addAll(hasPayload);
        all.addAll(hasChildren);
        all.addAll(hasNothing);
        HashMap<Node<TPayload>, Integer> nodeIndex = new HashMap<>();
        for (int i = 0; i < all.size(); i++) {
            nodeIndex.put(all.get(i), i);
        }

        int rootNodeIndex = nodeIndex.get(root);

        writer.writeInt(rootNodeIndex);

        writer.writeInt(nodesWithPayloads.size());

        for (Node<TPayload> node : nodesWithPayloads)
        {
            writePayload.accept(writer, node.getPayload());
        }

        Set<Character> allChars = new HashSet<>();
        for (Node<TPayload> node : allNodes) {
            allChars.addAll(node.children().keySet());
        }
        writer.writeInt(allChars.size());

        ArrayList<Character> sortedAllChars = new ArrayList<>(allChars);
        Collections.sort(sortedAllChars);
        for (char c : sortedAllChars) {
            writer.writeChar(c);
        }

        char[] allCharsArr = sortedAllChars.toString().toCharArray();

        writeChildren(writer, nodeIndex, hasBoth, allCharsArr);
        writeChildren(writer, nodeIndex, hasChildren, allCharsArr);
    }

    private static <TPayload> void writeChildren(
            DataOutputStream writer,
            HashMap<Node<TPayload>, Integer> nodeIndex,
            ArrayList<Node<TPayload>> nodes,
            char[] allChars) throws IOException {
        writer.writeInt(nodes.size());

        writeChildrenNoLength(writer, nodes, nodeIndex, allChars);
    }

    private static <TPayload> void writeChildrenNoLength(
            DataOutputStream writer,
            Iterable<Node<TPayload>> nodes,
            HashMap<Node<TPayload>, Integer> nodeIndex,
            char[] allChars) throws IOException {
        short[] charToIndexPlusOne = MatrixDawg.getCharToIndexPlusOneMap(
            allChars
        );

        char firstChar = allChars[0];

        for (Node<TPayload> node : nodes)
        {
            writeInt (writer, node.children().size(), allChars.length + 1);

            for (Map.Entry<Character, Node<TPayload>> child
                : node.sortedChildren()) // todo ok?
            {
                int charIndex = charToIndexPlusOne [child.getKey() - firstChar] - 1;

                writeInt (writer, charIndex, allChars.length);

                writer.write(nodeIndex.get(child.getValue()));
            }
        }
    }

    private static void writeInt(DataOutputStream writer,
                                 int charIndex, int countOfPossibleValues)
            throws IOException {
        if (countOfPossibleValues > 256)
        {
            writer.write((short) charIndex);
        }
        else
        {
            writer.write((byte) charIndex);
        }
    }

    public OldDawg(Node<TPayload> root, Class<TPayload> cls) {
        this.root = root;
        this.cls = cls;
    }

    @Override
    public int getNodeCount() {
        return root.getRecursiveChildNodeCount ();
    }
}
