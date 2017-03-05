package com.morpherltd.dawg;

import java.util.Iterator;
import java.util.Map;

public interface IDawg<TPayload> {
    TPayload get(Iterable<Character> word);

    int getLongestCommonPrefixLength(Iterable<Character> word);

    Iterable<Map.Entry<String, TPayload>> matchPrefix(
        Iterable<Character> prefix
    );

    int getNodeCount();
}
