package com.morpherltd.dawg;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.Map;

public class DawgBuilderTests extends TestCase {

    public void testEmptyKey() {
        DawgBuilder<Integer> db = new DawgBuilder<>(Integer.class);

        _<Integer> n = new _<>(-1);
        assertEquals(true, db.tryGetValue(Lists.charactersOf(""), n));
        assertEquals(0, (int) n.g());
    }

    public void testIncrementValue() {
        DawgBuilder<Integer> db = new DawgBuilder<>(Integer.class);

        increment(db, Lists.charactersOf("test"));
        increment(db, Lists.charactersOf("test"));
        increment(db, Lists.charactersOf("test"));

        _<Integer> n = new _<>(-1);
        assertEquals(true, db.tryGetValue(Lists.charactersOf("test"), n));
        assertEquals(3, (int) n.g());
    }

    private static void increment(DawgBuilder<Integer> db, Iterable<Character> key) {
        _<Integer> n = new _<>(-1);
        db.tryGetValue(key, n);
        db.insert(key, n.g() + 1);
    }

    public void testIterableCountWorksForDawg()
        throws IllegalAccessException, InstantiationException {
        String[] fruit = new String[]{"apple", "banana", "orange"};

        Dawg<Boolean> dawg = DawgExtensions.<Boolean>toDawg(fruit, f -> Lists.charactersOf(f), f -> true, Boolean.class);

        assertTrue(dawg.get(Lists.charactersOf("apple")));
        assertTrue(dawg.get(Lists.charactersOf("banana")));
        assertTrue(dawg.get(Lists.charactersOf("orange")));
        assertFalse(dawg.get(Lists.charactersOf("kiwi")));


        Iterator<Map.Entry<String, Boolean>> iterator = dawg.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

//        assertEquals(3, dawg.iterator());
    }

    private static String iterToStr(Iterable<Character> chars) {
        return Joiner.on("").join(chars);
    }
}
