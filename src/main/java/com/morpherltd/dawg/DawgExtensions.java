package com.morpherltd.dawg;

import java.util.function.Function;

public class DawgExtensions<T, TPayload>
{
    public static <TPayload> Dawg toDawg (String[] keys, Function<String, Iterable<Character>> key, Function<String, TPayload> payload, Class<TPayload> cls)
    {
        DawgBuilder<TPayload> dawgBuilder = new DawgBuilder(cls);

        for (int i = 0; i < keys.length; i++) {  // TODO: changed to String
            String elem = keys[i];
            dawgBuilder.insert(key.apply(elem), payload.apply(elem));
        }

        return dawgBuilder.buildDawg();
    }
}

