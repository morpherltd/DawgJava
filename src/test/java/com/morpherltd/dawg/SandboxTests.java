package com.morpherltd.dawg;


import com.morpherltd.dawg.adject.Adjectivizer;
import junit.framework.TestCase;

import java.io.IOException;

public class SandboxTests extends TestCase {
    private final Adjectivizer adjectivizer = new Adjectivizer();

    public SandboxTests() throws IOException {
    }

    public void testEmptyString()
            throws IllegalAccessException, InstantiationException {
        assertEquals(
            0,
            adjectivizer.getAdjectives("").spliterator().getExactSizeIfKnown()
        );
    }
}
