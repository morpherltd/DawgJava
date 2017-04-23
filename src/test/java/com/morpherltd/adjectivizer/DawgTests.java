package com.morpherltd.adjectivizer;

import com.google.common.collect.Lists;
import com.morpherltd.adjectivizer.StringExtensions;
import com.morpherltd.adjectivizer.Dawg;
import com.morpherltd.adjectivizer.DawgBuilder;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import static com.morpherltd.adjectivizer.StringExtensions.join;

public class DawgTests extends TestCase {

    public void testNodeCount () {
        DawgBuilder<Integer> dawgBuilder = new DawgBuilder<Integer>(Integer.class);

        dawgBuilder.insert(Lists.charactersOf("tip"), 3);
        dawgBuilder.insert(Lists.charactersOf("tap"), 3);

        DawgTestsHelper<Integer> helper = new DawgTestsHelper<Integer>();
        Dawg<Integer> rehydrated = helper.getDawg(dawgBuilder);

        assertEquals(4, rehydrated.getNodeCount());
    }

    public void testPresistance() {
        DawgBuilder<Integer> dawgBuilder = new DawgBuilder<Integer>(Integer.class);

        dawgBuilder.insert(Lists.charactersOf("cone"), 10);
        dawgBuilder.insert(Lists.charactersOf("bone"), 10);
        dawgBuilder.insert(Lists.charactersOf("gone"), 9);
        dawgBuilder.insert(Lists.charactersOf("go"), 5);
        dawgBuilder.insert(Lists.charactersOf("tip"), 3);
        dawgBuilder.insert(Lists.charactersOf("tap"), 3);

        DawgTestsHelper<Integer> helper = new DawgTestsHelper<Integer>();
        Dawg<Integer> rehydrated = helper.getDawg(dawgBuilder);

        assertEquals(10, (int) rehydrated.get(Lists.charactersOf("cone")));
        assertEquals(10, (int) rehydrated.get(Lists.charactersOf("bone")));
        assertEquals(9, (int) rehydrated.get(Lists.charactersOf("gone")));
        assertEquals(5, (int) rehydrated.get(Lists.charactersOf("go")));
        assertEquals(3, (int) rehydrated.get(Lists.charactersOf("tip")));
        assertEquals(3, (int) rehydrated.get(Lists.charactersOf("tap")));

        assertNull(rehydrated.get(Lists.charactersOf("cones")));
        assertNull(rehydrated.get(Lists.charactersOf("g")));
        assertNull(rehydrated.get(Lists.charactersOf("god")));
        assertNull(rehydrated.get(Lists.charactersOf("")));

//        assertEquals(0, (int) rehydrated.get(Lists.charactersOf("cones")));
//        assertEquals(0, (int) rehydrated.get(Lists.charactersOf("g")));
//        assertEquals(0, (int) rehydrated.get(Lists.charactersOf("god")));
//        assertEquals(0, (int) rehydrated.get(Lists.charactersOf("")));

    }

    public void testEmptyNode() {
        DawgBuilder<Integer> dawgBuilder = new DawgBuilder<Integer>(Integer.class);

        dawgBuilder.insert(Lists.charactersOf("tip"), 0);

        DawgTestsHelper<Integer> helper = new DawgTestsHelper<Integer>();
        Dawg<Integer> rehydrated = helper.getDawg (dawgBuilder);

        assertEquals(0, (int) rehydrated.get(Lists.charactersOf("tip")));
    }

    public void testTipTap() {
        DawgBuilder<Integer> dawgBuilder = new DawgBuilder<Integer>(Integer.class);

        dawgBuilder.insert(Lists.charactersOf("tip"), 3);
        dawgBuilder.insert(Lists.charactersOf("tap"), 3);

        DawgTestsHelper<Integer> helper = new DawgTestsHelper<Integer>();
        Dawg<Integer> rehydrated = helper.getDawg(dawgBuilder);

        assertEquals(3, (int) rehydrated.get(Lists.charactersOf("tap")));
        assertEquals(3, (int) rehydrated.get(Lists.charactersOf("tip")));
    }

    public void testEmptyKey() {
        DawgBuilder<Integer> dawgBuilder = new DawgBuilder<Integer>(Integer.class);

        dawgBuilder.insert(Lists.charactersOf(""), 5);

        DawgTestsHelper<Integer> helper = new DawgTestsHelper<Integer>();
        Dawg<Integer> rehydrated = helper.getDawg(dawgBuilder);

        assertEquals(5, (int) rehydrated.get(Lists.charactersOf("")));
    }

    public void testLongString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200 * 1000; i++) {
            sb.append('a');
        }
        String longString = sb.toString();

        DawgBuilder<Boolean> dawgBuilder = new DawgBuilder<Boolean>(Boolean.class);

        dawgBuilder.insert(Lists.charactersOf(longString), true);

        DawgTestsHelper<Boolean> helper = new DawgTestsHelper<Boolean>();
        Dawg<Boolean> rehydrated = helper.getDawg(dawgBuilder);

        assertTrue(rehydrated.get(Lists.charactersOf(longString)));
    }

    public void testEnekoWordList() {
//        long tStart = System.currentTimeMillis();

        String fileName = "eneko-words.txt";
        ArrayList<String> words = readLinesFromFile(fileName, 5000);

        DawgBuilder<Boolean> dawgBuilder = new DawgBuilder<Boolean>(Boolean.class);

//        System.out.println("Eneko inserting");
        for (String word : words) {
            dawgBuilder.insert(Lists.charactersOf(word), true);
        }

//        System.out.println("Eneko getDawg");
        DawgTestsHelper<Boolean> helper = new DawgTestsHelper<Boolean>();
        Dawg<Boolean> rehydrated = helper.getDawg(dawgBuilder);

//        System.out.println("Eneko get");
        for (String word : words) {
            assertTrue(rehydrated.get(Lists.charactersOf(word)));
        }

//        long tEnd = System.currentTimeMillis();
//        long tDelta = tEnd - tStart;
//        double elapsedSeconds = tDelta / 1000.0;
//        System.out.println("Eneko lapsed seconds: " + elapsedSeconds);
    }

    public void testMatchPrefix() {
        DawgBuilder<Boolean> dawgBuilder = new DawgBuilder<Boolean>(Boolean.class);

        dawgBuilder.insert(Lists.charactersOf("cat"), true);
        dawgBuilder.insert(Lists.charactersOf("caterpillar"), true);
        dawgBuilder.insert(Lists.charactersOf("dog"), true);

        DawgTestsHelper<Boolean> helper = new DawgTestsHelper<Boolean>();
        Dawg<Boolean> dawg = helper.getDawg(dawgBuilder);

        assertEquals("cat,caterpillar", helper.matchJoin(dawg, Lists.charactersOf("cat")));
        assertEquals("cat,caterpillar", helper.matchJoin(dawg, Lists.charactersOf("ca")));
        assertEquals("cat,caterpillar,dog", helper.matchJoin(dawg, Lists.charactersOf("")));
        assertEquals("", helper.matchJoin(dawg, Lists.charactersOf("boot")));
        assertEquals("", helper.matchJoin(dawg, Lists.charactersOf("cats")));

    }

    public void testEmptyDictionary() {
        DawgBuilder<Boolean> dawgBuilder = new DawgBuilder<Boolean>(Boolean.class);

        DawgTestsHelper<Boolean> helper = new DawgTestsHelper<Boolean>();
        Dawg<Boolean> dawg = helper.getDawg(dawgBuilder);

        int count1 = 0;
        for (Map.Entry<String, Boolean> p: dawg.matchPrefix(Lists.charactersOf("boot"))) {
            count1++;
        }
        assertTrue(count1 == 0);
        int count2 = 0;
        for (Map.Entry<String, Boolean> p: dawg.matchPrefix(Lists.charactersOf(""))) {
            count2++;
        }
        assertTrue(count1 == 0);
        assertTrue(count2 == 0);
        assertNull(dawg.get(Lists.charactersOf("")));
        assertNull(dawg.get(Lists.charactersOf("boot")));
    }

    public void testSuffixMatch() {
        DawgBuilder<Boolean> dawgBuilder = new DawgBuilder<Boolean>(Boolean.class);

        dawgBuilder.insert(Lists.charactersOf(StringExtensions.reverse("visibility")), true);
        dawgBuilder.insert(Lists.charactersOf(StringExtensions.reverse("possibility")), true);
        dawgBuilder.insert(Lists.charactersOf(StringExtensions.reverse("dexterity")), true);

        DawgTestsHelper<Boolean> helper = new DawgTestsHelper<Boolean>();
        Dawg<Boolean> dawg = helper.getDawg(dawgBuilder);

        int count1 = 0;
        for (Map.Entry<String, Boolean> p: dawg.matchPrefix(Lists.charactersOf(StringExtensions.reverse("ility")))) {
            count1++;
        }
        assertTrue(count1 == 2);
    }

    private ArrayList<String> readLinesFromFile(final String fileName, int numLines) {
        URL resource = getClass().getClassLoader().getResource(fileName);
        File file = new File(resource.getPath());
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> words = new ArrayList<String>();
        String line = null;
        int i = 0;

        try {
            while ((line = bufferedReader.readLine()) != null) {
                words.add(line);
                i++;
                if (i >= numLines) {
                    break;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return words;
    }


    private class DawgTestsHelper<TPayload> {
        private String matchJoin(Dawg<Boolean> dawg, Iterable<Character> prefix) {
            ArrayList<String> keys = new ArrayList<String>();
            for (Map.Entry<String, Boolean> pair: dawg.matchPrefix(prefix)){
                keys.add(pair.getKey());
            }
            return join(",", keys);
        }

        public Dawg<TPayload> getDawg(DawgBuilder<TPayload> dawgBuilder) {
            return dawgBuilder.buildDawg();
        }
    }
}
