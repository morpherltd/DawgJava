package com.morpherltd.dawg;


import junit.framework.TestCase;

import java.io.IOException;

public class SandboxTests extends TestCase {
    private final Adjectivizer adjectivizer = new Adjectivizer();

    public SandboxTests() throws IOException {
    }

    public void testEmptyString() {
        assertEquals(
            0,
            adjectivizer.getAdjectives("").spliterator().getExactSizeIfKnown()
        );
    }

    public void testNonRussian() {
        int count = 0;
        for (String s: adjectivizer.getAdjectives ("Moscow")) {
            count++;
        }

        assertEquals(0, count);
    }

    public void test2() {
        assertEquals(
            "никарагуанский",
            adjectivizer.getAdjectives ("Никарагуа").iterator().next()
        );
    }

    public void test3() {
        assertEquals(
            "шахтинский",
            adjectivizer.getAdjectives ("Шахты").iterator().next()
        );
    }

    public void test4() {
        assertEquals(
            "немецкий",
            adjectivizer.getAdjectives ("Германия").iterator().next()
        );
    }

    public void test5() {
        assertEquals(
            "британский",
            adjectivizer.getAdjectives ("Великобритания").iterator().next()
        );
    }

    public void test6() {
        assertEquals(
            "",
            adjectivizer.getAdjectives ("Осло").iterator().next()
        );
    }

    public void test7() {
        assertEquals(
            "ткацкий",
            adjectivizer.getAdjectives ("ткач").iterator().next()
        );
    }

    public void test8() {
        assertEquals(
            "азиатский",
            adjectivizer.getAdjectives ("Азия").iterator().next()
        );
    }

    public void test9() {
        assertEquals(
            "франкфуртский",
            adjectivizer.getAdjectives ("Франкфурт").iterator().next()
        );
    }

    public void test10() {
        assertEquals(
            "боснийский",
            adjectivizer.getAdjectives ("Босния и Герцеговина").iterator().next()
        );
    }

    public void test11() {
        assertEquals(
            "белорусский",
            adjectivizer.getAdjectives ("Белоруссия").iterator().next()
        );
    }

    public void test12() {
        assertEquals(
            "южноазиатский",
            adjectivizer.getAdjectives ("Южная Азия").iterator().next()
        );
    }

    public void test13() {
        assertEquals(
            "старооскольский",
            adjectivizer.getAdjectives ("Старый Оскол").iterator().next()
        );
    }

    public void test14() {
        assertEquals(
            "нижегородский",
            adjectivizer.getAdjectives ("Нижний Новгород").iterator().next()
        );
    }

    public void test15() {
        assertEquals(
            "киргизский",
            adjectivizer.getAdjectives ("Кыргызстан").iterator().next()
        );
    }

    public void test16() {
        assertEquals(
            "астанинский",
            adjectivizer.getAdjectives ("Астана").iterator().next()
        );
    }

    public void test17() {
        assertEquals(
            "армянский",
            adjectivizer.getAdjectives ("Армения").iterator().next()
        );
    }

    public void test18() {
        assertEquals(
            "бытошский",
            adjectivizer.getAdjectives ("Бытошь").iterator().next()
        );
    }

    public void test19() {
        assertEquals(
            "набережночелнинский",
            adjectivizer.getAdjectives ("Набережные Челны").iterator().next()
        );
    }

    public void test20() {
        assertEquals(
            "николо-погостинский",
            adjectivizer.getAdjectives ("Николо-Погост").iterator().next()
        );
    }

    public void test21() {
        assertEquals(
            "китайгородский",
            adjectivizer.getAdjectives ("Китай-Город").iterator().next()
        );
    }

    public void test22() {
        assertEquals(
            "ростовский-на-дону",
            adjectivizer.getAdjectives ("Ростов-на-Дону").iterator().next()
        );
    }

    public void test23() {
        assertEquals(
            "вышневолоцкий",
            adjectivizer.getAdjectives ("Вышний Волочёк").iterator().next()
        );
    }

    public void test24() {
        assertEquals(
            "верхнетатышлинский",
            adjectivizer.getAdjectives ("Верхние Татышлы").iterator().next()
        );
    }

    public void test25() {
        assertNotSame(
            "Тыкапува",
            adjectivizer.getAdjectives ("тыкапуовский").iterator().next()
        );
    }

    public void test26() {
        assertEquals(
            "карловарский",
            adjectivizer.getAdjectives ("Карловы Вары").iterator().next()
        );
    }

    public void test27() {
        assertEquals(
            "русский",
            adjectivizer.getAdjectives ("Русская").iterator().next()
        );
    }

    public void test28() {
        assertEquals(
            "люберецкий",
            adjectivizer.getAdjectives ("Люберцы").iterator().next()
        );
    }

    public void test29() {
        assertEquals(
            "старощербиновский",
            adjectivizer.getAdjectives ("Старощербиновская").iterator().next()
        );
    }

    public void test30() {
        assertEquals(
            "щербиновский",
            adjectivizer.getAdjectives ("Щербиновская").iterator().next()
        );
    }

    public void testTwoWords() {
        adjectivizer.getAdjectives("Москва фыва");
    }

    public void test32() {
        assertEquals(
            "нью-йоркский",
            adjectivizer.getAdjectives ("Нью Йорк").iterator().next()
        );
    }

    public void test33() {
        assertEquals(
            "торонтский",
            adjectivizer.getAdjectives ("Торонто").iterator().next()
        );
    }

    public void test34() {
        assertEquals(
            "корейский",
            adjectivizer.getAdjectives ("Республика Корея").iterator().next()
        );
    }

    public void test35() {
        assertEquals(
            "российский",
            adjectivizer.getAdjectives ("Российская Федерация").iterator().next()
        );
    }

    public void test36() {
        assertEquals(
            "гвинейский",
            adjectivizer.getAdjectives ("Папуа Новая Гвинея").iterator().next()
        );
    }
}
