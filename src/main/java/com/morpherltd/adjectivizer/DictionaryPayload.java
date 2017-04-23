package com.morpherltd.adjectivizer;

class DictionaryPayload {
    String NounSuffix;
    String AdjvSuffix;

    @Override
    public boolean equals(Object obj) {
        DictionaryPayload that = (DictionaryPayload) obj;

        return NounSuffix.equals (that.NounSuffix) && AdjvSuffix.equals (that.AdjvSuffix);
    }

    @Override
    public int hashCode() {
        return NounSuffix.hashCode () ^ AdjvSuffix.hashCode ();
    }

}
