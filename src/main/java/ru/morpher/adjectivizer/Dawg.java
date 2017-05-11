package ru.morpher.adjectivizer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class Dawg<TPayload> implements Iterable<Map.Entry<String, TPayload>> {
    private final IDawg<TPayload> dawg;
    private final Class<TPayload> cls;

    Dawg(IDawg<TPayload> dawg, Class<TPayload> cls) {
        this.dawg = dawg;
        this.cls = cls;

        Writers.put(Boolean.class, new BiConsumer<DataOutputStream, Boolean>() {
            @Override
            public void accept(DataOutputStream r, Boolean payload) {
                try {
                    r.writeBoolean(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(Integer.class, new BiConsumer<DataOutputStream, Integer>() {
            @Override
            public void accept(DataOutputStream r, Integer payload) {
                try {
                    r.writeInt(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(Long.class, new BiConsumer<DataOutputStream, Long>() {
            @Override
            public void accept(DataOutputStream r, Long payload) {
                try {
                    r.writeLong(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(Byte.class, new BiConsumer<DataOutputStream, Byte>() {
            @Override
            public void accept(DataOutputStream r, Byte payload) {
                try {
                    r.writeByte(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(Short.class, new BiConsumer<DataOutputStream, Short>() {
            @Override
            public void accept(DataOutputStream r, Short payload) {
                try {
                    r.writeShort(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(String.class, new BiConsumer<DataOutputStream, String>() {
            @Override
            public void accept(DataOutputStream r, String payload) {
                try {
                    byte[] data = payload.getBytes("UTF-8");
                    r.writeInt(data.length);
                    r.write(data);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(Character.class, new BiConsumer<DataOutputStream, Character>() {
            @Override
            public void accept(DataOutputStream r, Character payload) {
                try {
                    r.write(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(Double.class, new BiConsumer<DataOutputStream, Double>() {
            @Override
            public void accept(DataOutputStream r, Double payload) {
                try {
                    r.writeDouble(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        Writers.put(Float.class, new BiConsumer<DataOutputStream, Float>() {
            @Override
            public void accept(DataOutputStream r, Float payload) {
                try {
                    r.writeFloat(payload);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });

        Readers.put(Boolean.class, new ExecutionStrategy<MReader, Boolean>() {
            @Override
            public Boolean apply(MReader r) {
                try {
                    return r.readBoolean();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Readers.put(Integer.class, new ExecutionStrategy<MReader, Integer>() {
            @Override
            public Integer apply(MReader r) {
            try {
                return r.readInt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } }
        });
        Readers.put(Long.class, new ExecutionStrategy<MReader, Long>() {
            @Override
            public Long apply(MReader r) {
            try {
                return r.readLong();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}
        });
        Readers.put(Byte.class, new ExecutionStrategy<MReader, Byte>() {
                @Override
                public Byte apply(MReader r) {
            try {
                return r.readByte();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } }
        });
        Readers.put(Short.class, new ExecutionStrategy<MReader, Short>() {
            @Override
            public Short apply(MReader r) {
            try {
                return r.readShort();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }  }
        });
        Readers.put(String.class, new ExecutionStrategy<MReader, String>() {
                @Override
                public String apply(MReader r) {
            try {
                return r.readString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } }
        });
        Readers.put(Character.class, new ExecutionStrategy<MReader, Character>() {
            @Override
            public Character apply(MReader r) {
            try {
                return r.readChar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }  }
        });
        Readers.put(Double.class, new ExecutionStrategy<MReader, Double>() {
                @Override
                public Double apply(MReader r) {
            try {
                return r.readDouble();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } }
        });
        Readers.put(Float.class, new ExecutionStrategy<MReader, Float>() {
            @Override
            public Float apply(MReader r) {
            try {
                return r.readFloat();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }   }
        });
    }

    public TPayload get(Iterable<Character> word) {
        return dawg.get(word);
    }

    int getLongestCommonPrefixLength(Iterable<Character> word) {
        return dawg.getLongestCommonPrefixLength(word);
    }

    Iterable<Map.Entry<String, TPayload>> matchPrefix(
            Iterable<Character> prefix) {
        return dawg.matchPrefix(prefix);
    }

    int getNodeCount() {
        return dawg.getNodeCount();
    }

    private Iterable<Map.Entry<String, TPayload>> getEnumerator() {
        return matchPrefix(new ArrayList<Character>());
    }

    @Override
    public Iterator<Map.Entry<String, TPayload>> iterator() {
        try {
            Iterable<Map.Entry<String, TPayload>> enumerator = getEnumerator();
            return enumerator.iterator();
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
    private void saveAsYaleDawg(DataOutputStream stream,
                                final BiConsumer<DataOutputStream, TPayload> writePayload)
            throws Exception {
        save(stream, new BiConsumer<OldDawg<TPayload>, DataOutputStream>() {
            @Override
            public void accept(OldDawg<TPayload> d, DataOutputStream w) {
                try {
                    d.saveAsYaleDawg(w, writePayload != null ? writePayload : Dawg.this.getStandardWriter());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // Testing only.
    public void saveAsMatrixDawg(DataOutputStream stream,
                                 final BiConsumer<DataOutputStream, TPayload> writePayload)
            throws Exception {
        save(stream,  new BiConsumer<OldDawg<TPayload>, DataOutputStream>() {
            @Override
            public void accept(OldDawg<TPayload> d, DataOutputStream w) {
            try {
                d.saveAsMatrixDawg(w, writePayload != null ? writePayload : getStandardWriter());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } }
        });
    }

    private void save(DataOutputStream stream,
                      BiConsumer<OldDawg<TPayload>, DataOutputStream> pSave)
            throws Exception {
        // Do not close the BinaryWriter. Users might want to append more data to the stream.
        DataOutputStream writer = new DataOutputStream(stream);

        writer.write(new DawgStatic(cls).getSignature());

        pSave.accept((OldDawg<TPayload>) dawg, writer);
    }

    BiConsumer<DataOutputStream, TPayload> getStandardWriter()  // TODO: ok if not static?
    {
        if (!Writers.containsKey(cls)) {
            throw new RuntimeException(
                    "Could not find a serialization method for " + cls +
                            ". Use a saveXXX overload with a 'writePayload' parameter.");

        }

        return (BiConsumer<DataOutputStream, TPayload>) Writers.get(cls);
    }

    static final HashMap<Class, Object> Writers = new HashMap<Class, Object>();
    static final HashMap<Class, Object> Readers = new HashMap<Class, Object>();


}
