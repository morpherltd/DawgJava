package com.morpherltd.dawg.adject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WordJoiner {
    static final String[] common =
    {
        "республика", "острова", "федерация"
    };

    public static String toLowerAndJoinWords (String phrase)
    {
        String tmp = phrase.toLowerCase(Locale.ROOT);
        String[] tempSpl = tmp.split(" ");
        List<String> commonList = Arrays.asList(common);

        ArrayList<String> split = new ArrayList<>();
        for (String s : tempSpl) {
            if (!s.equals("") && !commonList.contains(s)) {
                split.add(s);
            }
        }
        switch (split.size())
        {
            case 1: return split.get(0);
            case 2:
                char c = 'о';
                int n = 2;

                if (StringExtensions.endsWithAny(split.get(0), new String[]{"ый", "ой", "ая", "ое", "ые"})) {
                } else if (StringExtensions.endsWithAny(split.get(0), new String[]{"ий", "яя", "ее", "ие"})) {
                    c = 'е';
                } else if (StringExtensions.endsWithAny(split.get(0), new String[]{"ев", "ёв", "ов", "ин", "цын"})) {
                    n = 0; // Сергиев Посад - сергиевопосадский
                } else if (StringExtensions.endsWithAny(split.get(0), new String[]{"ева", "ёва", "ова", "ина", "цына"})) {
                    n = 1; // Марьина Роща - марьинорощинский
                } else if (StringExtensions.endsWithAny(split.get(0), new String[]{"ево", "ёво", "ово", "ино", "цыно"})) {
                    n = 1;
                } else if (StringExtensions.endsWithAny(split.get(0), new String[]{"евы", "ёвы", "овы", "ины", "цыны"})) {
                    n = 1;
                } else break;

                String first = StringExtensions.removeLast(split.get(0), n).replace('ё', 'е');

                if (first.equals("северн")) first = "север"; // Северная Африка - североафриканский (-н- исчезает)

                char last = StringExtensions.last(first, 1).charAt(0);

                if ("гкх".indexOf(last) != -1)
                {
                    c = 'о';
                }

                if ("шщжчц".indexOf(last) != -1)
                {
                    c = 'е';
                }

                return first + c + split.get(1);
        }

        return String.join("-", split);
    }
}
