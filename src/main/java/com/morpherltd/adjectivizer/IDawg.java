package com.morpherltd.adjectivizer;

import java.util.Map;

interface IDawg<TPayload> {
    TPayload get(Iterable<Character> word);

    int getLongestCommonPrefixLength(Iterable<Character> word);

    Iterable<Map.Entry<String, TPayload>> matchPrefix(
        Iterable<Character> prefix
    );

    int getNodeCount();
}
