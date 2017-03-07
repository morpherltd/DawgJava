package com.morpherltd.dawg.adject;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.morpherltd.dawg.Dawg;
import com.morpherltd.dawg.DawgExtensions;
import com.morpherltd.dawg.DawgStatic;
import com.morpherltd.dawg.helpers.StrHelper;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SingleWordAdjectivizer {
    private Dawg <DictionaryPayloadCollection> dictionary;
    private Dawg<Boolean> reverseDictionary;
    private HashMap<DictionaryPayload, Integer> payloadRanks;

    public SingleWordAdjectivizer () throws IOException {
        InputStream stream = getClass().getClassLoader()
            .getResourceAsStream("Dictionary.dawg");
        init(new DawgStatic<DictionaryPayloadCollection>().load(new DataInputStream(stream), r -> {
            try {
                return DictionaryPayloadCollection.read(r);
            } catch (IOException e) { throw new RuntimeException(e); }
        }));
    }

    public SingleWordAdjectivizer (Dawg<DictionaryPayloadCollection> dictionary) {
        init(dictionary);
    }

    public void init(Dawg<DictionaryPayloadCollection> dictionary) {
        this.dictionary = dictionary;
        reverseDictionary = DawgExtensions.toDawg(dictionary, e -> Lists.charactersOf(new StringBuilder(e.getKey()).reverse().toString()), e -> true, Boolean.class);

        payloadRanks = new HashMap<>();
        while (dictionary.iterator().hasNext()) {
            DictionaryPayloadCollection c = dictionary.iterator().next().getValue();
            for (DictionaryPayload payload: c.GetEnumerator()) {
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

        if (payloads == null)
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
                    ).<DictionaryPayload>toArray();

                if (payloadsDescending.length > 0)
                    break;
            }
            while (--suffixLength >= 0);
        }

        ArrayList<String> result = new ArrayList<>();
        for (DictionaryPayload p : payloads) {
            result.add(getAdjective(lcNoun, p).replace(' ', '-'));
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
