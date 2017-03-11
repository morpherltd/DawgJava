package com.morpherltd.dawg;

class StringExtensions
{
    public static String removeLast (String s, int count)
    {
        return s.substring(0, s.length() - count);
    }

    public static String last (String s, int count)
    {
        return s.substring(s.length() - count);
    }

    public static Boolean endsWithAny(String s, String [] endings) {
        for (String end : endings) {
            if (s.endsWith(end)) {
                return true;
            }
        }
        return false;
    }
}

