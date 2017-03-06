package com.morpherltd.dawg;


import com.morpherltd.dawg.adject.Adjectivizer;
import junit.framework.TestCase;

public class Sandbox extends TestCase {
    private final Adjectivizer adjectivizer = new Adjectivizer();

    public void testEmptyString() {
        assertEquals(0, adjectivizer.getAdjectives("").Count());
    }
}
