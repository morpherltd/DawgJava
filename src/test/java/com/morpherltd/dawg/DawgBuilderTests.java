package com.morpherltd.dawg;

import com.google.common.collect.Lists;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class DawgBuilderTests extends TestCase {

    public void testEmptyKey() {
        DawgBuilder<Integer> db = new DawgBuilder<>(Integer.class);

        _<Integer> n = new _<>(-1);

        assertEquals(true, db.tryGetValue(Lists.charactersOf(""), n, 0));
        assertEquals(0, (int) n.g());
    }

}
