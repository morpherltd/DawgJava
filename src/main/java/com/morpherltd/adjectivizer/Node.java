package com.morpherltd.adjectivizer;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class Node<TPayload> {
    private final HashMap<Character, Node<TPayload>> _children = new LinkedHashMap<Character, Node<TPayload>>();

    private TPayload _payload;
    private boolean _payloadSet;
    private Class<TPayload> cls;

    public Node(Class<TPayload> cls) {
        this.cls = cls;
        _payload = NewInstance.make(this.cls);
    }

    public TPayload getPayload() {
        return _payload;
    }

    public void setPayload(final TPayload tPayload) {
        _payload = tPayload;
        _payloadSet = true;
    }

    public Node<TPayload> getOrAddEdge(char c) {
        if (_children.containsKey(c)) {
            return _children.get(c);
        }

        Node<TPayload> newNode = new Node<TPayload>(cls);
        _children.put(c, newNode);

        return newNode;
    }

    public Node<TPayload> getChild(char c) {
        if (_children.containsKey(c)) {
            return _children.get(c);
        }

        return null;
    }

    public boolean hasChildren() {
        return _children.size() > 0;
    }

    public HashMap<Character, Node<TPayload>> children() {
        return _children;
    }

    public Set<Map.Entry<Character, Node<TPayload>>> sortedChildren() {
        return new TreeMap<Character, Node<TPayload>>(children()).entrySet();
    }

    public int getRecursiveChildNodeCount() {
        return getAllDistinctNodes().size();
    }

    public ArrayList<Node<TPayload>> getAllDistinctNodes() {
        HashSet<Node<TPayload>> visitedNodes = new HashSet<Node<TPayload>>();
        visitedNodes.add(Node.this);

        Iterator<Map.Entry<Character, Node<TPayload>>> iterator = Node.this.children().entrySet().iterator();

        Deque<Iterator<Map.Entry<Character, Node<TPayload>>>> deque = new ArrayDeque<Iterator<Map.Entry<Character, Node<TPayload>>>>();
        deque.push(iterator);

        for (; ; ) {
            Iterator<Map.Entry<Character, Node<TPayload>>> curIterator = deque.peek();

            if (curIterator.hasNext()) {
                Map.Entry<Character, Node<TPayload>> curPair = curIterator.next();

                Node<TPayload> node = curPair.getValue();
                if (visitedNodes.contains(node)) {
                    continue;
                }

                visitedNodes.add(node);
                deque.push(node.children().entrySet().iterator());

            } else {
                deque.pop();

                if (deque.size() == 0) {
                    break;
                }
            }
        }


        return new ArrayList<Node<TPayload>>(visitedNodes);
    }

    @Override
    public boolean equals(Object that) {
        Node n = (Node) that;
        if (this.children().size() != n.children().size()) {
            return false;
        }
        for (Character key : this.children().keySet()) {
            if (!n.children().containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (Character key : this.children().keySet()) {
            hashCode = 31 * hashCode + key.hashCode();
        }
        return hashCode;
    }

    public ArrayList<Node<TPayload>> getAllDistinctNodes2() {
        HashSet<Node<TPayload>> visitedNodes = new HashSet<Node<TPayload>>();
        visitedNodes.add(this);

        ArrayList<Node<TPayload>> result = new ArrayList<Node<TPayload>>();
        result.add(this);

        Deque<Iterator<Map.Entry<Character, Node<TPayload>>>> deque =
                new ArrayDeque<Iterator<Map.Entry<Character, Node<TPayload>>>>();
        Iterator<Map.Entry<Character, Node<TPayload>>> iterator =
                this.children().entrySet().iterator();
        deque.push(iterator);

        int nivo = 0;
        for (; ; ) {
            Iterator<Map.Entry<Character, Node<TPayload>>> curIterator =
                    deque.peek();

            if (curIterator.hasNext()) {
                Map.Entry<Character, Node<TPayload>> curPair = curIterator.next();

                Node<TPayload> node = curPair.getValue();
                if (visitedNodes.contains(node)) {
                    continue;
                }
                visitedNodes.add(node);
                result.add(this);

                deque.push(node.children().entrySet().iterator());

            } else {
                nivo++;
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


