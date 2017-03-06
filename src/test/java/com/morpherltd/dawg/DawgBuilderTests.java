package com.morpherltd.dawg;

import com.google.common.collect.Lists;
import junit.framework.TestCase;

public class DawgBuilderTests extends TestCase {

    public void testEmptyKey() {
        DawgBuilder<Integer> db = new DawgBuilder<>(Integer.class);

        _<Integer> n = new _<>(-1);
        assertEquals(true, db.tryGetValue("", n));
        assertEquals(0, (int) n.g());
    }

    public void testIncrementValue ()
    {
        DawgBuilder<Integer> db = new DawgBuilder<>(Integer.class);

        increment(db, "test");
        increment(db, "test");
        increment(db, "test");

        _<Integer> n = new _<>(-1);
        assertEquals(true, db.tryGetValue("test", n));
        assertEquals(3, (int) n.g());
    }

    private static void increment(DawgBuilder<Integer> db, String key)
    {
        _<Integer> n = new _<>(-1);
        db.tryGetValue(key, n);
        db.insert(key, n.g() + 1);
    }

}
