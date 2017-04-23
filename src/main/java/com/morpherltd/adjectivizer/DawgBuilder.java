package com.morpherltd.adjectivizer;

import com.google.common.base.Joiner;

import java.util.ArrayList;

class DawgBuilder<TPayload> {
    private final Node<TPayload> root;
    private final ArrayList<Node<TPayload>> lastPath = new ArrayList<Node<TPayload>>();
    private String lastKey = "";
    private Class<TPayload> cls;

    DawgBuilder(Class<TPayload> cls) {
        this.cls = cls;
        root = new Node<TPayload>(cls);
    }

    void insert(Iterable<Character> key, TPayload value) {
        String strKey = Joiner.on("").join(key);

        if (key != null) { // TODO: same in Java?
            insertLastPath(strKey, value);
        } else {
            doInsert(root, key, value);
        }
    }

    private void insertLastPath(String strKey, TPayload value) {
        int i = 0;

        while (i < strKey.length() && i < lastKey.length()) {
            if (strKey.charAt(i) != lastKey.charAt(i)) break;
            ++i;
        }

        lastPath.subList(i, lastPath.size()).clear();


        lastKey = strKey;

        Node<TPayload> node = i == 0 ? root : lastPath.get(i - 1);

        while (i < strKey.length()) {
            node = node.getOrAddEdge(strKey.charAt(i));
            lastPath.add(node);
            ++i;
        }

        node.setPayload(value);
    }

    private static <TPayload> void doInsert(Node<TPayload> node,
                                            Iterable<Character> key,
                                            TPayload value) {
        for (char c : key) {
            node = node.getOrAddEdge(c);
        }
        node.setPayload(value);
    }

    boolean tryGetValue(Iterable<Character> key, _<TPayload> value) {
        value.s(NewInstance.<TPayload>make(cls));

        Node<TPayload> node = this.root;

        for (char c : key) {
            node = node.getChild(c);

            if (node == null) return false;
        }

        value.s(node.getPayload());

        return true;
    }

    Dawg<TPayload> buildDawg() {
        LevelBuilder.<TPayload>buildLevelsExcludingRoot(root);
        return new Dawg<TPayload>(new OldDawg<TPayload>(root, cls), cls);
    }

    /**
     * Used as a replacement of the "out" parameters in C#.
     *
     * From Stackoverflow:
     * http://stackoverflow.com/questions/430479/how-do-i-use-an-equivalent-to-c-reference-parameters-in-java/431152#431152
     */
    static class _<E> {
        E ref;
        _(E e){
            ref = e;
        }
        E g() { return ref; }
        void s(E e){ this.ref = e; }

        public String toString() {
            return ref.toString();
        }
    }
}
