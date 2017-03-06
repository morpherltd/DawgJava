package com.morpherltd.dawg;


import java.util.*;

public class Node<TPayload> {
    private final HashMap<Character, Node<TPayload>> _children
        = new HashMap<>();

    private TPayload _payload;
    private boolean _payloadSet;
    private Class<TPayload> cls;

    public Node(Class<TPayload> cls) {
        this.cls = cls;
        _payload = (TPayload) NewInstance.get(this.cls);
    }

    public TPayload getPayload() {
        return _payload;
    }

    public void setPayload(final TPayload tPayload) {
        _payload = tPayload;
        _payloadSet = true;
    }

    public Node<TPayload> getOrAddEdge (char c) {
        Node<TPayload> newNode;
        if (!_children.containsKey(c)) {
            newNode = new Node<>(cls);
            _children.put(c, newNode);
        } else {
            newNode = _children.get(c);
        }
        return newNode;
    }

    public Node<TPayload> getChild(char c)
    {
        if (_children.containsKey(c))
            return _children.get(c);
        else
            return null;
    }

    public boolean hasChildren() {
        return _children.size() > 0;
    }

    public HashMap<Character, Node<TPayload>> children() {
        return _children;
    }

    public Set<Map.Entry<Character, Node<TPayload>>> sortedChildren() {
        return new TreeMap<>(children()).entrySet();
    }

    public int getRecursiveChildNodeCount() {
        return getAllDistinctNodes().size();
    }

    public ArrayList<Node<TPayload>> getAllDistinctNodes() {
        HashSet<Node<TPayload>> visitedNodes = new HashSet<>();
        visitedNodes.add(this);

        ArrayList<Node<TPayload>> result = new ArrayList<>();
        result.add(this);

        Deque<Iterator<Map.Entry<Character, Node<TPayload>>>> deque =
            new ArrayDeque<>();
        Iterator<Map.Entry<Character, Node<TPayload>>> iterator =
            this.children().entrySet().iterator();
        deque.push(iterator);

        int nivo = 0;
        for (;;) {
            Iterator<Map.Entry<Character, Node<TPayload>>> curIterator =
                deque.peek();

            if (curIterator.hasNext()) {
                Map.Entry<Character, Node<TPayload>> curPair = curIterator.next();
                System.out.println("Nivo: " + nivo + ", key: " + curPair.getKey());

                Node<TPayload> node = curPair.getValue();
                if (visitedNodes.contains(node)) {
                    continue;
                }
                visitedNodes.add(node);
                result.add(this);

                deque.push(node.children().entrySet().iterator());

            } else {
                nivo ++;
                deque.pop();
                if (deque.size() == 0) break;
            }
        }

        for (Node<TPayload> a : result) {
            System.out.println(a.getPayload());
        }
        System.out.println("visited:");
        for (Node<TPayload> a : visitedNodes) {
            System.out.println(a.getPayload());
        }

        return result;
    }

    public boolean hasPayload() {
        return _payloadSet;
    }
}


