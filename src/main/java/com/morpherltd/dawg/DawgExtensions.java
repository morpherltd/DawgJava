package com.morpherltd.dawg;

import java.util.function.Function;

class DawgExtensions<T, TPayload>
{
    public static <T, TPayload> Dawg toDawg(Iterable<T> keys, Function<T, Iterable<Character>> key, Function<T, TPayload> payload, Class<TPayload> cls)
    {
        DawgBuilder<TPayload> dawgBuilder = new DawgBuilder(cls);

        for (T elem : keys) {
            dawgBuilder.insert(key.apply(elem), payload.apply(elem));
        }

        return dawgBuilder.buildDawg();
    }
}

