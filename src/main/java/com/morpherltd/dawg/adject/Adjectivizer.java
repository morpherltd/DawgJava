package com.morpherltd.dawg.adject;

import java.io.IOException;

public class Adjectivizer {
    final SingleWordAdjectivizer singleWordAdjectivizer = new SingleWordAdjectivizer();

    public Adjectivizer() throws IOException {
    }

    public Iterable<String> getAdjectives(String phrase) {
        String noun = WordJoiner.toLowerAndJoinWords(phrase);

        return singleWordAdjectivizer.getAdjectives(noun);
    }

}
