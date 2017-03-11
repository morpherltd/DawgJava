package com.morpherltd.dawg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.function.Function;

import static com.morpherltd.dawg.Dawg.Readers;

class DawgStatic<TPayload> {
    private final Class<TPayload> cls;

    public DawgStatic(Class<TPayload> cls) {
        this.cls = cls;
    }

    public Dawg<TPayload> load(MReader stream, Function<MReader, TPayload> readPayload) throws IOException { // TODO: ok if not static?
        Function<MReader, TPayload> f = readPayload != null ? readPayload : (Function<MReader, TPayload>) Readers.get(cls);
        return new Dawg<TPayload>(loadIDawg(stream, f), cls);
    }

    private IDawg<TPayload> loadIDawg(MReader reader,
                                      Function<MReader, TPayload>
                                          readPayload) throws IOException {
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

    private OldDawg loadOldDawg(MReader reader,
                                int nodeCount, Function<MReader, TPayload> readPayload)
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
