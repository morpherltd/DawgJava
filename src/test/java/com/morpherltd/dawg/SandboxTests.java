package com.morpherltd.dawg;


import com.morpherltd.dawg.adject.Adjectivizer;
import jdk.nashorn.internal.ir.annotations.Ignore;
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

    public void testNonRussian()
        throws IllegalAccessException, InstantiationException {
        int count = 0;
        for (String s: adjectivizer.getAdjectives ("Moscow")) {
            count++;
        }

        assertEquals(0, count);
    }

    public void test2()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "никарагуанский",
            adjectivizer.getAdjectives ("Никарагуа").iterator().next()
        );
    }

    public void test3()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "шахтинский",
            adjectivizer.getAdjectives ("Шахты").iterator().next()
        );
    }

    public void test4()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "немецкий",
            adjectivizer.getAdjectives ("Германия").iterator().next()
        );
    }

    public void test5()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "британский",
            adjectivizer.getAdjectives ("Великобритания").iterator().next()
        );
    }

    public void test6()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "",
            adjectivizer.getAdjectives ("Осло").iterator().next()
        );
    }

    public void test7()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "ткацкий",
            adjectivizer.getAdjectives ("ткач").iterator().next()
        );
    }

    public void test8()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "азиатский",
            adjectivizer.getAdjectives ("Азия").iterator().next()
        );
    }

    public void test9()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "франкфуртский",
            adjectivizer.getAdjectives ("Франкфурт").iterator().next()
        );
    }

    public void test10()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "боснийский",
            adjectivizer.getAdjectives ("Босния и Герцеговина").iterator().next()
        );
    }

    public void test11()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "белорусский",
            adjectivizer.getAdjectives ("Белоруссия").iterator().next()
        );
    }

    public void test12()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "южноазиатский",
            adjectivizer.getAdjectives ("Южная Азия").iterator().next()
        );
    }

//    public void test13()
//        throws IllegalAccessException, InstantiationException {
//        assertEquals(
//            "старооскольский",
//            adjectivizer.getAdjectives ("Старый Оскол").iterator().next()
//        );
//    }

    public void test14()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "нижегородский",
            adjectivizer.getAdjectives ("Нижний Новгород").iterator().next()
        );
    }

    public void test15()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "киргизский",
            adjectivizer.getAdjectives ("Кыргызстан").iterator().next()
        );
    }

    public void test16()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "астанинский",
            adjectivizer.getAdjectives ("Астана").iterator().next()
        );
    }

    public void test17()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "армянский",
            adjectivizer.getAdjectives ("Армения").iterator().next()
        );
    }

    public void test18()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "бытошский",
            adjectivizer.getAdjectives ("Бытошь").iterator().next()
        );
    }

    public void test19()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "набережночелнинский",
            adjectivizer.getAdjectives ("Набережные Челны").iterator().next()
        );
    }

    public void test20()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "николо-погостинский",
            adjectivizer.getAdjectives ("Николо-Погост").iterator().next()
        );
    }

    public void test21()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "китайгородский",
            adjectivizer.getAdjectives ("Китай-Город").iterator().next()
        );
    }

    public void test22()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "ростовский-на-дону",
            adjectivizer.getAdjectives ("Ростов-на-Дону").iterator().next()
        );
    }

    public void test23()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "вышневолоцкий",
            adjectivizer.getAdjectives ("Вышний Волочёк").iterator().next()
        );
    }

    public void test24()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "верхнетатышлинский",
            adjectivizer.getAdjectives ("Верхние Татышлы").iterator().next()
        );
    }

//    public void test25()
//        throws IllegalAccessException, InstantiationException {
//        assertEquals(
//            "Тыкапува",
//            adjectivizer.getAdjectives ("тыкапуовский").iterator().next()
//        );
//    }

    public void test26()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "карловарский",
            adjectivizer.getAdjectives ("Карловы Вары").iterator().next()
        );
    }

//    public void test27()
//        throws IllegalAccessException, InstantiationException {
//        assertEquals(
//            "русский",
//            adjectivizer.getAdjectives ("Русская").iterator().next()
//        );
//    }

    public void test28()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "люберецкий",
            adjectivizer.getAdjectives ("Люберцы").iterator().next()
        );
    }

    public void test29()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "старощербиновский",
            adjectivizer.getAdjectives ("Старощербиновская").iterator().next()
        );
    }

//    public void test30()
//        throws IllegalAccessException, InstantiationException {
//        assertEquals(
//            "щербиновский",
//            adjectivizer.getAdjectives ("Щербиновская").iterator().next()
//        );
//    }

    public void testTwoWords()
        throws IllegalAccessException, InstantiationException {
        adjectivizer.getAdjectives("Москва фыва");
    }

    public void test32()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "нью-йоркский",
            adjectivizer.getAdjectives ("Нью Йорк").iterator().next()
        );
    }

    public void test33()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "торонтский",
            adjectivizer.getAdjectives ("Торонто").iterator().next()
        );
    }

//    public void test34()
//        throws IllegalAccessException, InstantiationException {
//        assertEquals(
//            "корейский",
//            adjectivizer.getAdjectives ("Республика Корея").iterator().next()
//        );
//    }

//    public void test35()
//        throws IllegalAccessException, InstantiationException {
//        assertEquals(
//            "российский",
//            adjectivizer.getAdjectives ("Российская Федерация").iterator().next()
//        );
//    }

    public void test36()
        throws IllegalAccessException, InstantiationException {
        assertEquals(
            "гвинейский",
            adjectivizer.getAdjectives ("Папуа Новая Гвинея").iterator().next()
        );
    }
}
