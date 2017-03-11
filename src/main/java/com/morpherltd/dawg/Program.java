package com.morpherltd.dawg;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by rok on 10-Mar-17.
 */
class Program {
    public static void main(String[] args) throws IOException {
        new Program().run();
    }

    private void run() throws IOException {
        long tStart = System.currentTimeMillis();

        String fileName = "eneko-words.txt";
        ArrayList<String> words = readLinesFromFile(fileName, 20000);

        DawgBuilder<Boolean> dawgBuilder = new DawgBuilder<>(Boolean.class);

        System.out.println("Eneko inserting");
        for (String word : words) {
            dawgBuilder.insert(Lists.charactersOf(word), true);
        }

        System.out.println("Eneko getDawg");
        DawgTestsHelper<Boolean> helper = new DawgTestsHelper<>();
        Dawg<Boolean> rehydrated = helper.getDawg(dawgBuilder);

        System.out.println("Eneko get");
        for (String word : words) {
            boolean a = rehydrated.get(Lists.charactersOf(word));
        }

        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Eneko lapsed seconds: " + elapsedSeconds);

    }


    private ArrayList<String> readLinesFromFile(final String fileName, int numLines) throws IOException {
//        URL resource = getClass().getClassLoader().getResource(fileName);
        File file = new File("D:\\dev\\MorpherDawg\\src\\test\\resources\\eneko-words.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> words = new ArrayList<>();
        String line = null;
        int i = 0;
        while ((line = bufferedReader.readLine()) != null) {
            words.add(line);
            i++;
            if (i >= numLines) {
                break;
            }
        }
        bufferedReader.close();
        return words;
    }


    private class DawgTestsHelper<TPayload> {
        private String matchJoin(Dawg<Boolean> dawg, Iterable<Character> prefix) {
            ArrayList<String> keys = new ArrayList<>();
            for (Map.Entry<String, Boolean> pair: dawg.matchPrefix(prefix)){
                keys.add(pair.getKey());
            }
            return String.join(",", keys);
        }
        public Dawg<TPayload> getDawg(DawgBuilder<TPayload> dawgBuilder) {
            return dawgBuilder.buildDawg();
        }
    }
}
