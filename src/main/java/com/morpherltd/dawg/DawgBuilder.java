package com.morpherltd.dawg;

import com.google.common.base.Defaults;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DawgBuilder<TPayload> {
    final Node<TPayload> root;

    final ArrayList<Node<TPayload>> lastPath = new ArrayList<>();
    String lastKey = "";

    Class<TPayload> cls;

    public DawgBuilder(Class<TPayload> cls) {
        this.cls = cls;
        root = new Node<>(cls);
    }

    public void insert(String key, TPayload value) {
        if (key != null) { // TODO: same in Java?
            insertLastPath(key, value);
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
                                            String key,
                                            TPayload value) {
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            node = node.getOrAddEdge(c);
        }
        node.setPayload(value);
    }

    public boolean tryGetValue(String key, _<TPayload> value) {
        try {
            value.s(cls.newInstance());
        } catch (InstantiationException e) {
//            TPayload def = Defaults.defaultValue(cls);
            try {
                Constructor<TPayload> ctor = cls.getConstructor(String.class);
                TPayload tp = ctor.newInstance(DefaultVals.get(cls).toString());
                value.s(tp);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Node<TPayload> node = this.root;

        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            node = node.getChild(c);

            if (node == null) return false;
        }

        value.s(node.getPayload());

        return true;
    }

    public Dawg<TPayload> buildDawg() {
        LevelBuilder.<TPayload>buildLevelsExcludingRoot(root);
        return new Dawg<TPayload>(new OldDawg<TPayload>(root, cls), cls);
    }
}
