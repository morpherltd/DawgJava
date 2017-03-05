package com.morpherltd.dawg;


import java.util.*;

public class Node<TPayload> {
    private final HashMap<Character, Node<TPayload>> _children
        = new HashMap<>();

    private TPayload _payload;
    private boolean _payloadSet;

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
            newNode = new Node<>();
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
        ArrayList<Node<TPayload>> result = new ArrayList<>();

        HashSet<Node<TPayload>> visitedNodes = new HashSet<>();
        visitedNodes.add(this);
        result.add(this);

        Deque<Iterator<Map.Entry<Character, Node<TPayload>>>> deque =
            new ArrayDeque<>();
        Iterator<Map.Entry<Character, Node<TPayload>>> iterator =
            this.children().entrySet().iterator();
        deque.push(iterator);

        for (;;) {
            Iterator<Map.Entry<Character, Node<TPayload>>> cur =
                deque.peek();
            if (cur.hasNext()) {
                Node<TPayload> node = cur.next().getValue();
                if (visitedNodes.contains(node)) {
                    continue;
                }
                visitedNodes.add(node);
                result.add(this);

                deque.push(node.children().entrySet().iterator());

            } else {
                deque.pop();
                if (deque.size() == 0) break;
            }
        }

        return result;
    }

    public boolean hasPayload() {
        return _payloadSet;
    }
}


