package ru.morpher.adjectivizer;

import junit.framework.TestCase;

/**
 * Created by rok on 09-Mar-17.
 */
public class JoinWords extends TestCase {

    public void test1() {
        Test ("Нижний Новгород", "нижненовгород");
    }

    public void test2 () {
        Test("Старый Оскол", "старооскол");
    }

    public void test3 () {
        Test("Сергиев Посад", "сергиевопосад");
    }

    public void test4 () {
        Test("Марьина Роща", "марьинороща");
    }

    public void test5 () {
        Test("Вятские Поляны", "вятскополяны");
    }

    public void test6 () {
        Test("Большой Камень", "большекамень");
    }

    public void test7 () {
        Test("Зелёная Гура", "зеленогура");
    }

    public void test8 () {
        Test("Набережные Челны", "набережночелны");
    }

    public void test9 () {
        Test("Северная Африка", "североафрика");
    }

    private static void Test (String noun, String oneWord)
    {
        assertEquals (oneWord, WordJoiner.toLowerAndJoinWords (noun));
    }

}
