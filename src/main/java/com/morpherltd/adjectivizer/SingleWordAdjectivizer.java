package com.morpherltd.adjectivizer;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class SingleWordAdjectivizer {
    private Dawg<DictionaryPayloadCollection> dictionary;
    private Dawg<Boolean> reverseDictionary;
    private HashMap<DictionaryPayload, Integer> payloadRanks;

    public SingleWordAdjectivizer () {
        try {
            MReader dictionaryInputStream = new MReader("Dictionary.dawg");
            ExecutionStrategy<MReader, DictionaryPayloadCollection> dictionaryStrategy = new ExecutionStrategy<MReader, DictionaryPayloadCollection>() {
                @Override
                public DictionaryPayloadCollection apply(MReader r) {
                    try {
                        return DictionaryPayloadCollection.read(r);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            DawgStatic<DictionaryPayloadCollection> dawgStatic = new DawgStatic<DictionaryPayloadCollection>(DictionaryPayloadCollection.class);
            Dawg<DictionaryPayloadCollection> dictionary = dawgStatic.load(dictionaryInputStream, dictionaryStrategy);

            init(dictionary);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SingleWordAdjectivizer (Dawg<DictionaryPayloadCollection> dictionary) {
        init(dictionary);
    }

    public void init(Dawg<DictionaryPayloadCollection> dictionary) {
        this.dictionary = dictionary;
        reverseDictionary = DawgExtensions.toDawg(dictionary,  new ExecutionStrategy<Map.Entry<String, DictionaryPayloadCollection>, Iterable<Character>>() {
            @Override
            public Iterable<Character> apply(Map.Entry<String, DictionaryPayloadCollection> e) {
                return Lists.charactersOf(new StringBuilder(e.getKey()).reverse().toString());
            }
        }, new ExecutionStrategy<Map.Entry<String, DictionaryPayloadCollection>, Boolean>() {
            @Override
            public Boolean apply(Map.Entry<String, DictionaryPayloadCollection> e) {
                return true;
            }
        }, Boolean.class);

        Iterator<Map.Entry<String, DictionaryPayloadCollection>> it = dictionary.iterator();
        payloadRanks = new HashMap<DictionaryPayload, Integer>();
        while (it.hasNext()) {
            Map.Entry<String, DictionaryPayloadCollection> next = it.next();
            DictionaryPayloadCollection c = next.getValue();
            for (DictionaryPayload payload: c.GetEnumerator()) {
                if (!payloadRanks.containsKey(payload))
                    payloadRanks.put(payload, 1);
                else
                    payloadRanks.put(payload, payloadRanks.get(payload) + 1);
            }
        }
    }

    public Iterable<String> getAdjectives(String noun) {
        String lcNoun = noun.toLowerCase(Locale.ROOT);

        DictionaryPayloadCollection prePayloads = dictionary.get(Lists.charactersOf(lcNoun));
        Iterable<DictionaryPayload> payloads = null;
        if (prePayloads != null) {
            payloads = prePayloads.GetEnumerator();
        }

        List<DictionaryPayload> payloadsDescending = new ArrayList<DictionaryPayload>();

        if (prePayloads == null) {
            String lcNounReversed = new StringBuilder(lcNoun).reverse().toString();
            int suffixLength = reverseDictionary.getLongestCommonPrefixLength(
                Lists.charactersOf(lcNounReversed)
            );

            do {
                int currentSuffixLength = suffixLength;

                HashSet<DictionaryPayload> distinct = new HashSet<DictionaryPayload>();

                String suf = lcNounReversed.substring(0, suffixLength);
                Iterable<Map.Entry<String, Boolean>> matched = reverseDictionary.matchPrefix(Lists.charactersOf(suf));
                for (Map.Entry<String, Boolean> tpl : matched) {
                    String n = StringExtensions.reverse(tpl.getKey());
                    DictionaryPayloadCollection pc = dictionary.get(Lists.charactersOf(n));
                    for (DictionaryPayload p : pc.GetEnumerator()) {
                        if (canBeApplied(n, lcNoun, currentSuffixLength)) {
                            if (p.NounSuffix.length() <= currentSuffixLength) {
                                distinct.add(p);
                            }
                        }
                    }
                }

                if (distinct.size() != 0) {
                    List<DictionaryPayload> sortedDistinct = new ArrayList<DictionaryPayload>(distinct);
                    Collections.sort(sortedDistinct, new Comparator<DictionaryPayload>() {
                        @Override
                        public int compare(DictionaryPayload p1, DictionaryPayload p2) {
                            return -(payloadRanks.get(p2) - payloadRanks.get(p1));
                        }
                    });
                    payloadsDescending = sortedDistinct;
                    break;
                }
            }
            while (--suffixLength >= 0);
        }

        ArrayList<String> result = new ArrayList<String>();
        if (prePayloads == null) {
            Iterator<DictionaryPayload> iter = payloadsDescending.iterator();
            while (iter.hasNext()) {
                result.add(getAdjective(lcNoun, iter.next()).replace(' ', '-'));
            }
            return result;
        }
        if (payloads != null) {
                for (DictionaryPayload p : payloads) {
                    result.add(getAdjective(lcNoun, p).replace(' ', '-'));
                }
            return result;
        }

        return new ArrayList<String>();
    }

    static boolean canBeApplied(String dictionaryNoun, String noun, int suffixLength)
    {
        boolean canBeApplied = noun.length() > suffixLength && dictionaryNoun.length() > suffixLength
            && category (suffixLength, noun).equals(category (suffixLength, dictionaryNoun));

        return canBeApplied;
    }

    private static Object category (int suffixLength, String noun) {
        String reversedNoun = StringExtensions.reverse(noun);
        String substr = reversedNoun.substring(suffixLength, suffixLength + 1);
        char first = substr.charAt(0);
        Object category = category(first);

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
