package com.morpherltd.dawg.adject;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.morpherltd.dawg.Dawg;
import com.morpherltd.dawg.DawgBuilder;
import com.morpherltd.dawg.DawgExtensions;
import com.morpherltd.dawg.DawgStatic;
import com.morpherltd.dawg.helpers.MReader;
import com.morpherltd.dawg.helpers.StrHelper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class SingleWordAdjectivizer {
    private Dawg <DictionaryPayloadCollection> dictionary;
    private Dawg<Boolean> reverseDictionary;
    private HashMap<DictionaryPayload, Integer> payloadRanks;

    public SingleWordAdjectivizer () throws IOException {
        MReader byteArrayInputStream = new MReader("Dictionary.dawg");
        init(new DawgStatic<DictionaryPayloadCollection>(DictionaryPayloadCollection.class).load(byteArrayInputStream, r -> {
                try {
                    return DictionaryPayloadCollection.read(r);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }));
    }

    public SingleWordAdjectivizer (Dawg<DictionaryPayloadCollection> dictionary) {
        init(dictionary);
    }

    public void init(Dawg<DictionaryPayloadCollection> dictionary) {
        this.dictionary = dictionary;
//        ArrayList<String> asd = new ArrayList<>();
//        while (it.hasNext()) {
//            Map.Entry<String, DictionaryPayloadCollection> next = it.next();
//            System.out.println("dict key: " + next.getKey());
//            asd.add(next.getKey());
////            System.out.println("dict key: " + next.getKey() + " " + next.getValue().GetEnumerator().iterator().next().NounSuffix);
//        }
//
//        for (int i = 0; i < asd.size(); i++) {
//            System.out.println(i + ": " + asd.get(i));
//        }

        System.out.println("CORRECT UP TO HERE");

        reverseDictionary = DawgExtensions.toDawg(dictionary, e -> Lists.charactersOf(new StringBuilder(e.getKey()).reverse().toString()), e -> true, Boolean.class);
//        DawgBuilder<Boolean> dawgBuilder = new DawgBuilder(Boolean.class);
//        for (Map.Entry<String, DictionaryPayloadCollection> elem : dictionary) {
//            System.out.println("key: " + elem.getKey());
//            dawgBuilder.insert(
//                Lists.charactersOf(new StringBuilder(elem.getKey()).reverse().toString()),
//                true
//            );
//        }

//        System.out.println("buildDawg");
//        reverseDictionary = dawgBuilder.buildDawg();
//        System.out.println("finishedBuildDawg");

        Iterator<Map.Entry<String, DictionaryPayloadCollection>> rit = reverseDictionary.iterator();
        ArrayList<String> asd = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<String, DictionaryPayloadCollection> next = it.next();
            System.out.println("dict key: " + next.getKey());
            asd.add(next.getKey());
//            System.out.println("dict key: " + next.getKey() + " " + next.getValue().GetEnumerator().iterator().next().NounSuffix);
        }
        for (int i = 0; i < asd.size(); i++) {
            System.out.println(i + ": " + asd.get(i));
        }

        Iterator<Map.Entry<String, DictionaryPayloadCollection>> it = dictionary.iterator();
        payloadRanks = new HashMap<>();
        while (it.hasNext()) {
//            System.out.println("dict key: " + dictionary.iterator().next().getKey());
            Map.Entry<String, DictionaryPayloadCollection> next = it.next();
            DictionaryPayloadCollection c = next.getValue();
            for (DictionaryPayload payload: c.GetEnumerator()) {
                System.out.println(payload.NounSuffix);
                System.out.println(payload.AdjvSuffix);
                if (!payloadRanks.containsKey(payload))
                    payloadRanks.put(payload, 1);
                else
                    payloadRanks.put(payload, payloadRanks.get(payload) + 1);
            }
        }
    }

    public Iterable<String> getAdjectives(String noun)
            throws IllegalAccessException, InstantiationException {
        String lcNoun = noun.toLowerCase(Locale.ROOT);

        Iterable<DictionaryPayload> payloads =
            dictionary.get(Lists.charactersOf(lcNoun)).GetEnumerator();

        ArrayList<String> result = new ArrayList<>();
        for (DictionaryPayload p : payloads) {
            result.add(getAdjective(lcNoun, p).replace(' ', '-'));
        }

        if (result.size() == 0)
//        if (payloads == null)
        {
            String lcNounReversed = new StringBuilder(lcNoun).reverse().toString();
            int suffixLength = reverseDictionary.getLongestCommonPrefixLength(
                Lists.charactersOf(lcNounReversed)
            );

            do
            {
                int currentSuffixLength = suffixLength;

                HashSet<DictionaryPayload> distinct = new HashSet<>();

                String suf = lcNounReversed.substring(0, suffixLength);
                Iterable<Map.Entry<String, Boolean>> matched =
                    reverseDictionary.matchPrefix(Lists.charactersOf(suf));
                for (Map.Entry<String, Boolean> tpl: matched) {
                    String n = StrHelper.reverse(tpl.getKey());
                    DictionaryPayloadCollection pc = dictionary.get(Lists.charactersOf(n));
                    for (DictionaryPayload p: pc.GetEnumerator()) {
                        if (canBeApplied(n, lcNoun, currentSuffixLength)) {
                            if (p.NounSuffix.length() <= currentSuffixLength) {
                                distinct.add(p);
                            }
                        }
                    }
                }

                DictionaryPayload[] payloadsDescending = (DictionaryPayload[])
                    distinct.stream().sorted(
                        (p1, p2) -> -(payloadRanks.get(p2) - payloadRanks.get(p1))
                    ).toArray(size -> new Object[size]);

                if (payloadsDescending.length > 0)
                    break;
            }
            while (--suffixLength >= 0);
        }
        return result;
    }

    static boolean canBeApplied(String dictionaryNoun, String noun, int suffixLength)
    {
        boolean canBeApplied = noun.length() > suffixLength && dictionaryNoun.length() > suffixLength
            && category (suffixLength, noun).equals(category (suffixLength, dictionaryNoun));

        return canBeApplied;
    }

    private static Object category (int suffixLength, String noun) {
        Object category = category(StrHelper.reverse(noun).substring(suffixLength, 1).charAt(0));

        return category;
    }

    private static Object category(char c) {
        String s = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        String p = "аббкбаашбабббббаббббабкбшшшбабааа";

        int i = s.indexOf (c);

        return i == -1 ? ' ' : p.charAt(i);
    }

    private static String getAdjective(String lcNoun, DictionaryPayload p) {
        return lcNoun.substring (0, lcNoun.length() - p.NounSuffix.length()) + p.AdjvSuffix;
    }

}
