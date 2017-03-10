package com.morpherltd.dawg;

import com.morpherltd.dawg.adject.DictionaryPayloadCollection;

import java.lang.reflect.Constructor;

public class NewInstance {

    public static <TPayload> TPayload make(Class cls) {
//        return null;
        try {
            return (TPayload) cls.newInstance();
        } catch (InstantiationException e) {
            try {
                if (cls == DictionaryPayloadCollection.class) {
                    return null;
                }
                Constructor<TPayload> ctor = cls.getConstructor(String.class);
                TPayload tp = ctor.newInstance(get(cls).toString());
                return tp;
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object get(Class tp) {
        if (tp.equals(Integer.class)) {
            return 0;
        } else if (tp.equals(Boolean.class)) {
            return false;
        }

        throw new RuntimeException("Unknown class");
    }
}
