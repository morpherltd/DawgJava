package com.morpherltd.dawg;

import java.util.*;

public class LevelBuilder<TPayload> {
    public static <TPayload> void buildLevelsExcludingRoot(Node<TPayload> root) {
        ArrayList<HashMap<NodeWrapper<TPayload>, NodeWrapper<TPayload>>> levels
            = new ArrayList<>();

        Deque<StackNode<TPayload>> stack = new ArrayDeque<>();

        push(stack, root);

        Map.Entry<Character, Node<TPayload>> cur = null;
        while (stack.size() > 0) {
            if (stack.peek().ChildIterator.hasNext()) {
                cur = stack.peek().ChildIterator.next();
                push(stack, cur.getValue());
            } else {
                StackNode<TPayload> current = stack.pop();
                if (stack.size() > 0) {
                    StackNode<TPayload> parent = stack.peek();

                    int level = current.Level;

                    if (levels.size() <= level) {
                        levels.add(new HashMap<>());
                    }

                    HashMap<NodeWrapper<TPayload>, NodeWrapper<TPayload>>
                        dictionary = levels.get(level);

                    NodeWrapper<TPayload> nodeWrapper = new NodeWrapper<>(
                        current.Node, parent.Node, cur.getKey()
                    );

                    if (dictionary.containsKey(nodeWrapper)) {
                        NodeWrapper<TPayload> existing =
                            dictionary.get(nodeWrapper);
                        parent.Node.children().put(
                            cur.getKey(), existing.Node
                        );

                    } else {
                        dictionary.put(nodeWrapper, nodeWrapper);
                    }

                    int parentLevel = current.Level + 1;

                    if (parent.Level < parentLevel) {
                        parent.Level = parentLevel;
                    }
                }
            }
        }
    }

    private static <TPayload> void push(Deque<StackNode<TPayload>> stack,
                                        Node<TPayload> node) {
        StackNode<TPayload> sn = new StackNode<>();
        sn.Node = node;
        sn.ChildIterator = node.children().entrySet().iterator();

        stack.push(sn);
    }

    private static class StackNode<TPayload> {
        public Node <TPayload> Node;
        public Iterator<Map.Entry<Character, Node <TPayload>>> ChildIterator;
        public int Level;
    }
}
