package ru.morpher.adjectivizer;

import java.util.*;

class NodeWrapper<TPayload> {

    public NodeWrapper(Node<TPayload> node, Node<TPayload> sup, char ch) {
        Node = node;
        Super = sup;
        Char = ch;
    }

    public final Node<TPayload> Node;
    public final Node<TPayload> Super;
    public final char Char;

    // NodeWrapperEqualityComparer functionality from C#.

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NodeWrapper))
            return false;
        if (obj == this)
            return true;

        NodeWrapper<TPayload> other = (NodeWrapper<TPayload>) obj;
        return true && sequenceEqual(
            this.Node.sortedChildren(), other.Node.sortedChildren()
        );
    }

    private static <TPayload> boolean sequenceEqual(
        Set<Map.Entry<Character, ru.morpher.adjectivizer.Node<TPayload>>> x,
        Set<Map.Entry<Character, ru.morpher.adjectivizer.Node<TPayload>>> y) {

        Iterator<Map.Entry<Character, Node<TPayload>>> xe = x.iterator();
        Iterator<Map.Entry<Character, Node<TPayload>>> ye = y.iterator();
        while (xe.hasNext()) {
            if (ye.hasNext()) return false;

            Map.Entry<Character, Node<TPayload>> xcurrent = xe.next();
            Map.Entry<Character, Node<TPayload>> ycurrent = ye.next();

            if (!xcurrent.getKey().equals(ycurrent.getKey())) return false;
            if (!xcurrent.getValue().equals(ycurrent.getValue())) return false;
        }

        return !ye.hasNext();
    }

    private int getHashCode(Node<TPayload> node) {
        TPayload payload = node.getPayload();
        if (payload == null) return 0;

        int hashCode = payload.hashCode();
//        int hashCode = 0;

        for (Map.Entry<Character, Node<TPayload>> c:
                node.children().entrySet()){
            hashCode ^= c.getKey() ^ c.getValue().hashCode();
        }

        return hashCode;
//        return 0;
    }

    @Override
    public int hashCode() {
        return getHashCode(this.Node);
    }
}
