package com.morpherltd.dawg;

import java.util.Iterator;
import java.util.Map;

public interface IDawg<TPayload> {
    TPayload get(Iterable<Character> word) throws IllegalAccessException, InstantiationException;

    int getLongestCommonPrefixLength(Iterable<Character> word);

    Iterable<Map.Entry<String, TPayload>> matchPrefix(
        Iterable<Character> prefix
    ) throws IllegalAccessException, InstantiationException;

    int getNodeCount();
}
