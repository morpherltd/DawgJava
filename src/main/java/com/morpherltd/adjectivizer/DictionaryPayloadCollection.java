package com.morpherltd.adjectivizer;

import com.google.common.collect.Iterables;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class DictionaryPayloadCollection {
    private DictionaryPayload[] payloads;

    private DictionaryPayloadCollection(Iterable<DictionaryPayload> payloads) {
        this.payloads = Iterables.toArray(payloads, DictionaryPayload.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null && this == null) return true;
        if (obj == null || this == null) return false;
        DictionaryPayloadCollection that = (DictionaryPayloadCollection) obj;

        return this.payloads.equals(that.payloads);
    }

    @Override
    public int hashCode() {
        int hc = 0;
        for (int i = 0; i < payloads.length; i++) {
            hc ^= payloads.hashCode();
        }
        return hc;
    }

    public DictionaryPayload get(int i) {
        return this.payloads[i];
    }

    public static void write(DataOutputStream w, DictionaryPayloadCollection pc) throws IOException {
        if (pc == null)
        {
            w.writeInt(0);
        }
        else
        {
            w.writeInt(pc.payloads.length);

            for (DictionaryPayload payload : pc.payloads)
            {
                DataStreamStrings.writeString(w, payload.NounSuffix);
                DataStreamStrings.writeString(w, payload.AdjvSuffix);
            }
        }
    }

    public static DictionaryPayloadCollection read(MReader r)
            throws IOException {
        int length = r.readInt();

        if (length < 0) {
            throw new RuntimeException("Length can't be less than 0: " + length);
        }

        if (length == 0) {
            return null;
        } else {
            ArrayList<DictionaryPayload> arr = new ArrayList<DictionaryPayload>();
            for (int i = 0; i < length; i++) {  // TODO: Possible bug in the orig code? < or <= ?
                DictionaryPayload dp = new DictionaryPayload();
                dp.NounSuffix = r.readString();
                dp.AdjvSuffix = r.readString();
                arr.add(dp);
            }
            return new DictionaryPayloadCollection(arr);
        }
    }

    Iterable<DictionaryPayload> GetEnumerator() {
        return Arrays.asList(this.payloads);
    }
}
