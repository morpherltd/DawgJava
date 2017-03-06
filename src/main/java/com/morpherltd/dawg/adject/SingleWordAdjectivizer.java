package com.morpherltd.dawg.adject;

import com.morpherltd.dawg.Dawg;

public class SingleWordAdjectivizer {
    private final Dawg <DictionaryPayloadCollection> dictionary;
    private final Dawg<Boolean> reverseDictionary;
    private final Dictionary <DictionaryPayload, int> payloadRanks;

    public SingleWordAdjectivizer () : this (Dawg<DictionaryPayloadCollection>.Load (new MemoryStream (Resources.Dictionary), DictionaryPayloadCollection.Read))
    {
    }

    public SingleWordAdjectivizer (Dawg <DictionaryPayloadCollection> dictionary)
    {
        this.dictionary = dictionary;

        reverseDictionary = dictionary.ToDawg (e => e.Key.Reverse (), e => true);

        payloadRanks = dictionary
            .SelectMany (e => e.Value)
                .GroupBy (p => p)
                .ToDictionary (g => g.Key, g => g.Count ());
    }

    public IEnumerable <string> GetAdjectives (string noun)
    {
        var lcNoun = noun.ToLowerInvariant ();

        IEnumerable<DictionaryPayload> payloads = dictionary [lcNoun];

        if (payloads == null)
        {
            int suffixLength = reverseDictionary.GetLongestCommonPrefixLength (lcNoun.Reverse ());

            do
            {
                int currentSuffixLength = suffixLength;

                payloads = reverseDictionary
                    .MatchPrefix (lcNoun.Reverse ().Take (suffixLength)) // select all dictionary words with given suffix
                    .Select (re => new {Noun = new string (re.Key.Reverse ().ToArray ()), Payloads = dictionary [re.Key.Reverse ()]})
                        .SelectMany (r => r.Payloads.Select (p => new {r.Noun, Payload = p}))
                        .Where (r => CanBeApplied (r.Noun, lcNoun, currentSuffixLength))
                        .Select (r => r.Payload)
                        .Distinct ()
                .Where (p => p.NounSuffix.Length <= currentSuffixLength)
                //.Where (p => payloadRanks [p] > 1)
                        .OrderByDescending (p => payloadRanks [p])
                        .ToArray ();

                if (payloads.Any())
                    break;
            }
            while (--suffixLength >= 0);
        }

        return payloads.Select (p => GetAdjective (lcNoun, p).Replace(' ', '-'));
    }

    static bool CanBeApplied(string dictionaryNoun, string noun, int suffixLength)
    {
        var canBeApplied = noun.Length > suffixLength && dictionaryNoun.Length > suffixLength
            && Category (suffixLength, noun).Equals (Category (suffixLength, dictionaryNoun));

        return canBeApplied;
    }

    private static object Category (int suffixLength, string noun)
    {
        var category = Category (noun.Reverse ().Skip (suffixLength).First ());

        return category;
    }

    private static object Category(char c)
    {
            const string s = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
            const string p = "аббкбаашбабббббаббббабкбшшшбабааа";

        int i = s.IndexOf (c);

        return i == -1 ? ' ' : p [i];
    }

    private static string GetAdjective (string lcNoun, DictionaryPayload p)
    {
        return lcNoun.Substring (0, lcNoun.Length - p.NounSuffix.Length) + p.AdjvSuffix;
    }

}
