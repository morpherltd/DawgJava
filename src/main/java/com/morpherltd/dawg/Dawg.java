package com.morpherltd.dawg;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Dawg<TPayload> implements Iterable<Map.Entry<String, TPayload>> {
    private final IDawg<TPayload> dawg;
    private final Class<TPayload> cls;

    public Dawg(IDawg<TPayload> dawg, Class<TPayload> cls) {
        this.dawg = dawg;
        this.cls = cls;

        Writers.put(Boolean.class, (BiConsumer<DataOutputStream, Boolean>) (r, payload) -> {
            try { r.writeBoolean(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Writers.put(Integer.class, (BiConsumer<DataOutputStream, Integer>) (r, payload) -> {
            try { r.writeInt(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Writers.put(Long.class, (BiConsumer<DataOutputStream, Long>) (r, payload) -> {
            try { r.writeLong(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Writers.put(Byte.class, (BiConsumer<DataOutputStream, Byte>) (r, payload) -> {
            try { r.writeByte(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Writers.put(Short.class, (BiConsumer<DataOutputStream, Short>) (r, payload) -> {
            try { r.writeShort(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Writers.put(String.class, (BiConsumer<DataOutputStream, String>) (r, payload) -> {
            try {
                byte[] data = payload.getBytes("UTF-8");
                r.writeInt(data.length);
                r.write(data);
            } catch (IOException e) { throw new RuntimeException(e.getMessage()); }

        });
        Writers.put(Character.class, (BiConsumer<DataOutputStream, Character>) (r, payload) -> {
            try { r.write(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Writers.put(Double.class, (BiConsumer<DataOutputStream, Double>) (r, payload) -> {
            try { r.writeDouble(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Writers.put(Float.class, (BiConsumer<DataOutputStream, Float>) (r, payload) -> {
            try { r.writeFloat(payload); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });

        Readers.put(Boolean.class, (Function<DataInputStream, Boolean>) (r) -> {
            try { return r.readBoolean(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(Integer.class, (Function<DataInputStream, Integer>) (r) -> {
            try { return r.readInt(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(Long.class, (Function<DataInputStream, Long>) (r) -> {
            try { return r.readLong(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(Byte.class, (Function<DataInputStream, Byte>) (r) -> {
            try { return r.readByte(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(Short.class, (Function<DataInputStream, Short>) (r) -> {
            try { return r.readShort(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(String.class, (Function<DataInputStream, String>) (r) -> {
            try {
                int length=r.readInt();
                byte[] data=new byte[length];
                r.readFully(data);
                return new String(data,"UTF-8");
            } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(Character.class, (Function<DataInputStream, Character>) (r) -> {
            try { return r.readChar(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(Double.class, (Function<DataInputStream, Double>) (r) -> {
            try { return r.readDouble(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
        Readers.put(Float.class, (Function<DataInputStream, Float>) (r) -> {
            try { return r.readFloat(); } catch (IOException e) { throw new RuntimeException(e.getMessage()); }
        });
    }

    public TPayload get(Iterable<Character> word)
            throws InstantiationException, IllegalAccessException {
        return dawg.get(word);
    }

    public int getLongestCommonPrefixLength (Iterable<Character> word) {
        return dawg.getLongestCommonPrefixLength(word);
    }

    public Iterable<Map.Entry<String, TPayload>> matchPrefix(
            Iterable<Character> prefix)
            throws InstantiationException, IllegalAccessException {
        return dawg.matchPrefix(prefix);
    }

    public int getNodeCount () {
        return dawg.getNodeCount ();
    }

    public Iterable<Map.Entry<String, TPayload>> getEnumerator()
            throws IllegalAccessException, InstantiationException {
        return matchPrefix(new ArrayList<>());
    }

    @Override
    public Iterator<Map.Entry<String, TPayload>> iterator() {
        try {
            return getEnumerator().iterator();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveTo(DataOutputStream stream,
                       BiConsumer<DataOutputStream, TPayload> writePayload)
            throws Exception {
        saveAsYaleDawg(stream, writePayload != null ? writePayload : getStandardWriter());
    }

    // Testing only.
    public void saveAsYaleDawg(DataOutputStream stream,
                               BiConsumer<DataOutputStream, TPayload>
                                   writePayload)
            throws Exception {
        save(stream, (d, w) -> {
            try {
                d.saveAsYaleDawg(w, writePayload != null ? writePayload : getStandardWriter());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Testing only.
    public void saveAsMatrixDawg(DataOutputStream stream,
                                 BiConsumer<DataOutputStream, TPayload>
                                     writePayload)
            throws Exception {
        save(stream, (d, w) -> {
            try {
                d.saveAsMatrixDawg(w, writePayload != null ? writePayload : getStandardWriter());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void save(DataOutputStream stream,
                      BiConsumer<OldDawg<TPayload>, DataOutputStream> pSave)
            throws Exception {
        // Do not close the BinaryWriter. Users might want to append more data to the stream.
        DataOutputStream writer = new DataOutputStream(stream);

        writer.write(new DawgStatic().getSignature());

        pSave.accept((OldDawg<TPayload>) dawg, writer);
    }

    BiConsumer<DataOutputStream, TPayload> getStandardWriter ()  // TODO: ok if not static?
    {
        if (!Writers.containsKey(cls)) {
            throw new RuntimeException(
                "Could not find a serialization method for " + cls +
                ". Use a saveXXX overload with a 'writePayload' parameter.");

        }

        return (BiConsumer<DataOutputStream, TPayload>) Writers.get(cls);
    }

    static final HashMap<Class, Object> Writers = new HashMap<>();
    static final HashMap<Class, Object> Readers = new HashMap<>();


}
