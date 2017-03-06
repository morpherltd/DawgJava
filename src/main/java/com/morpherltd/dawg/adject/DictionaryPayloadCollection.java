package com.morpherltd.dawg.adject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DictionaryPayloadCollection {
    private final DictionaryPayload[] payloads;

    public DictionaryPayloadCollection (Iterable<DictionaryPayload> payloads) {
        this.payloads = payloads.toArray ();
    }

    @Override
    public boolean equals(Object obj) {
        DictionaryPayloadCollection that = (DictionaryPayloadCollection) obj;

        return this.payloads.SequenceEqual (that.payloads);
    }

    @Override
    public int hashCode() {
        return payloads.Aggregate (0, (hc, p) => hc ^ p.GetHashCode ());
    }

    public DictionaryPayload get(int i) {
        return this.payloads[i];
    }

    public static void write(DataOutputStream w, DictionaryPayloadCollection pc) throws IOException {
        if (pc == null)
        {
            w.write(0);
        }
        else
        {
            w.write(pc.payloads.length);

            for (DictionaryPayload payload : pc.payloads)
            {
                w.write(payload.NounSuffix);
                w.write(payload.AdjvSuffix);
            }
        }
    }

    public static DictionaryPayloadCollection read(DataInputStream r)
            throws IOException {
        int length = r.readInt();

        new DictionaryPayload();

        return length == 0 ? null : new DictionaryPayloadCollection (
            Enumerable.Range (0, length)
                .Select (i => new DictionaryPayload {
            NounSuffix = r.ReadString (),
            AdjvSuffix = r.ReadString ()}));
    }

    Iterable<DictionaryPayload> GetEnumerator() {
        return this.payloads.iterator();
    }
}
