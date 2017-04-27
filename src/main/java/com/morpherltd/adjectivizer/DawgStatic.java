package com.morpherltd.adjectivizer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.morpherltd.adjectivizer.Dawg.Readers;

class DawgStatic<TPayload> {
    private final Class<TPayload> cls;

    DawgStatic(Class<TPayload> cls) {
        this.cls = cls;
    }

    Dawg<TPayload> load(MReader stream,
                        ExecutionStrategy<MReader, TPayload> readPayload) throws IOException { // TODO: ok if not static?
        if (readPayload == null) {
            readPayload = (ExecutionStrategy<MReader, TPayload>) Readers.get(cls);
        }

        return new Dawg<TPayload>(loadIDawg(stream, readPayload), cls);
    }

    private IDawg<TPayload> loadIDawg(MReader reader,
                                      ExecutionStrategy<MReader, TPayload> readPayload) throws IOException {
        int signature = getSignature();
        int firstInt = reader.readInt();

        if (firstInt == signature) {
            int version = reader.readInt();

            switch (version)
            {
                case 1: return new MatrixDawg<TPayload>(reader, readPayload, cls);
                case 2: return new YaleDawg<TPayload>(reader, readPayload, cls);
            }

            throw new RuntimeException(
                "This file was produced by a more recent version of DawgSharp."
            );
        }

        // The old, unversioned, file format had the number of nodes as the first 4 bytes of the stream.
        // It is extremely unlikely that they happen to be exactly the same as the signature "DAWG".
        OldDawg<TPayload> result = loadOldDawg(reader, firstInt, readPayload);

        return result;
    }

    int getSignature() throws UnsupportedEncodingException {
        byte[] bytes = "DAWG".getBytes("UTF-8");
        return bytes [0]
            + bytes [1] << 8
            + bytes [2] << 16
            + bytes [3] << 24;
    }

    private OldDawg loadOldDawg(MReader reader, int nodeCount, ExecutionStrategy<MReader, TPayload> readPayload)
        throws IOException {
        Node<TPayload>[] nodes = (Node<TPayload>[]) new Object[nodeCount];

        int rootIndex = reader.readInt();

        char[] chars = new char[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            chars[i] = reader.readChar();
        }

        for (int i = 0; i < nodeCount; ++i)
        {
            Node<TPayload> node = new Node<TPayload>(cls);

            short childCount = reader.readShort();

            while (childCount --> 0)
            {
                int childIndex = reader.readInt();

                node.children().put(chars[childIndex], nodes[childIndex]);
            }

            node.setPayload(readPayload.apply(reader));

            nodes [i] = node;
        }

        return new OldDawg<TPayload>(nodes [rootIndex], cls);
    }
}
