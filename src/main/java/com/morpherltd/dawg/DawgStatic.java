package com.morpherltd.dawg;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.function.Function;

import static com.morpherltd.dawg.Dawg.Readers;

public class DawgStatic<TPayload> {
    private final Class<TPayload> cls;

    public DawgStatic() {
        this.cls = (Class<TPayload>)
            ((ParameterizedType)getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public Dawg<TPayload> load(DataInputStream stream, Function<DataInputStream, TPayload> readPayload) throws IOException { // TODO: ok if not static?
        Function<DataInputStream, TPayload> f = readPayload != null ? readPayload : (Function<DataInputStream, TPayload>) Readers.get(cls);
        return new Dawg<TPayload>(loadIDawg(stream, f), cls);
    }

    private IDawg<TPayload> loadIDawg(DataInputStream stream,
                                      Function<DataInputStream, TPayload>
                                          readPayload) throws IOException {
        DataInputStream reader = new DataInputStream(stream);
        int signature = getSignature();
        int firstInt = reader.readInt();

        if (firstInt == signature) {
            int version = reader.readInt();

            switch (version)
            {
                case 1: return new <TPayload>MatrixDawg(reader, readPayload, cls);
                case 2: return new <TPayload>YaleDawg(reader, readPayload, cls);
            }

            throw new RuntimeException(
                "This file was produced by a more recent version of DawgSharp."
            );
        }

        // The old, unversioned, file format had the number of nodes as the first 4 bytes of the stream.
        // It is extremely unlikely that they happen to be exactly the same as the signature "DAWG".
        OldDawg<TPayload> result = loadOldDawg(reader, firstInt, readPayload);
        reader.close();

        return result;
    }

    public int getSignature() throws UnsupportedEncodingException {
        byte[] bytes = "DAWG".getBytes("UTF-8");
        return bytes [0]
            + bytes [1] << 8
            + bytes [2] << 16
            + bytes [3] << 24;
    }

    private OldDawg loadOldDawg(DataInputStream reader,
                                int nodeCount, Function<DataInputStream, TPayload> readPayload)
        throws IOException {
        Node<TPayload>[] nodes = (Node<TPayload>[]) new Object[nodeCount];

        int rootIndex = reader.readInt();

        char[] chars = new char[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            chars[i] = reader.readChar();
        }

        for (int i = 0; i < nodeCount; ++i)
        {
            Node<TPayload> node = new Node<> (cls);

            short childCount = reader.readShort();

            while (childCount --> 0)
            {
                int childIndex = reader.readInt();

                node.children().put(chars[childIndex], nodes[childIndex]);
            }

            node.setPayload(readPayload.apply(reader));

            nodes [i] = node;
        }

        return new <TPayload>OldDawg(nodes [rootIndex], cls);
    }
}
