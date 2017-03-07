package com.morpherltd.dawg.adject;

/**
 * Created by rok on 06-Mar-17.
 */
public class Adjectivizer {
    final SingleWordAdjectivizer singleWordAdjectivizer = new SingleWordAdjectivizer ();

    public Iterable<String> getAdjectives(String phrase) {
        String noun = WordJoiner.toLowerAndJoinWords(phrase);

        return singleWordAdjectivizer.getAdjectives(noun);
    }

}
