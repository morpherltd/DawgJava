package ru.morpher.adjectivizer;

public class Adjectivizer {
    final SingleWordAdjectivizer singleWordAdjectivizer = new SingleWordAdjectivizer();

    public Adjectivizer() {
    }

    public Iterable<String> getAdjectives(String phrase) {
        String noun = WordJoiner.toLowerAndJoinWords(phrase);

        return singleWordAdjectivizer.getAdjectives(noun);
    }

}
