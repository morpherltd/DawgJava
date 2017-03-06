package com.morpherltd.dawg;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class PrefixMatcher<TPayload> {
    private final StringBuilder sb;
    private final Class<TPayload> cls;

    public PrefixMatcher(StringBuilder sb, Class<TPayload> cls) {
        this.sb = sb;
        this.cls = cls;
    }

    public Iterable<Map.Entry<String, TPayload>> matchPrefix(
            Node<TPayload> node)
            throws IllegalAccessException, InstantiationException {
        ArrayList<Map.Entry<String, TPayload>> result = new ArrayList<>();

        if (!node.getPayload().equals(NewInstance.make(cls)))
        {
            result.add(new AbstractMap.SimpleEntry<>(
                sb.toString(), node.getPayload())
            );
        }

        for (Map.Entry<Character, Node<TPayload>> child
            : node.children().entrySet())
        {
            sb.append(child.getKey());

            for (Map.Entry<String, TPayload> kvp
                : matchPrefix(child.getValue())) {
                result.add(kvp);
            }

            sb.setLength(sb.length() - 1);
        }

        return result;
    }
}
