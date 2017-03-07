package com.morpherltd.dawg.adject;

import com.google.common.collect.Iterables;
import com.morpherltd.dawg.helpers.DataStreamStrings;
import com.morpherltd.dawg.helpers.MReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class DictionaryPayloadCollection {
    private final DictionaryPayload[] payloads;

    public DictionaryPayloadCollection (Iterable<DictionaryPayload> payloads) {
        this.payloads = Iterables.toArray(payloads, DictionaryPayload.class);
    }

    @Override
    public boolean equals(Object obj) {
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

        if (length == 0) {
            return null;
        } else {
            ArrayList<DictionaryPayload> arr = new ArrayList<>();
            for (int i = 0; i < length; i++) {  // TODO: Possible bug in the orig code? < or <= ?
                DictionaryPayload dp = new DictionaryPayload();
//                dp.NounSuffix = DataStreamStrings.readString(r);
//                dp.AdjvSuffix = DataStreamStrings.readString(r);
                arr.add(dp);
            }
            return new DictionaryPayloadCollection(arr);
        }
    }

    Iterable<DictionaryPayload> GetEnumerator() {
        return Arrays.asList(this.payloads);
    }
}