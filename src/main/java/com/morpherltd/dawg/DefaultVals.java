package com.morpherltd.dawg;

public class DefaultVals {
    public static Object get(Class tp) {
        if (tp.equals(Integer.class)) {
            return 0;
        }

        throw new RuntimeException("Unknown class");
    }
}
