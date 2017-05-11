package ru.morpher.adjectivizer;


class DawgExtensions {
    static <T, TPayload> Dawg<TPayload> toDawg(Iterable<T> keys,
                                               ExecutionStrategy<T, Iterable<Character>> key,
                                               ExecutionStrategy<T, TPayload> payload,
                                               Class<TPayload> cls) {
        DawgBuilder<TPayload> dawgBuilder = new DawgBuilder(cls);

        for (T elem : keys) {
            dawgBuilder.insert(key.apply(elem), payload.apply(elem));
        }

        return dawgBuilder.buildDawg();
    }
}

