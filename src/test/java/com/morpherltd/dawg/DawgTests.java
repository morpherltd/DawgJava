package com.morpherltd.dawg;

import com.google.common.collect.Lists;
import junit.framework.TestCase;

public class DawgTests extends TestCase {

    public void testNodeCount ()
    {
        DawgBuilder<Integer> dawgBuilder = new DawgBuilder<>(Integer.class);

        dawgBuilder.insert(Lists.charactersOf("tip"), 3);
        dawgBuilder.insert(Lists.charactersOf("tap"), 3);

        DawgTestsHelper<Integer> helper = new DawgTestsHelper<>();
        Dawg<Integer> rehydrated = helper.getDawg(dawgBuilder);

        assertEquals(4, rehydrated.getNodeCount());
    }

    private class DawgTestsHelper<TPayload> {
        public Dawg<TPayload> getDawg(DawgBuilder<TPayload> dawgBuilder)
        {
            return dawgBuilder.buildDawg();
        }

    }

}
