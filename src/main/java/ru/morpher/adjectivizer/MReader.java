package ru.morpher.adjectivizer;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class MReader {

    private final FileInputStream _stream;

    MReader(String fileName) throws IOException {
        File temp = File.createTempFile("Dictionary-", ".dawg");
        InputStream originalFileStream = getClass().getClassLoader().getResourceAsStream(fileName);
        IOUtils.copy(originalFileStream, new FileOutputStream(temp, true));

        _stream = new FileInputStream(temp);
    }

    int readInt() throws IOException {
//        byte[] bytesLength = new byte[4];
//        _r.read(bytesLength, 0, 4);
//        int num = ByteBuffer.wrap(bytesLength).getInt();
//        return num;
        byte[] buffer = new byte[4];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        if (_stream.read(buffer) < 0) {
            throw new IOException("EOF");
        }
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        return bb.getInt();
    }

    char readChar() throws IOException {
        byte[] firstByte = new byte[1];
        if (_stream.read(firstByte) < 0) {
            throw new IOException("EOF");
        }

        if ((firstByte[0] & 0xFF) <= 0x7f) {
            return (char) firstByte[0];
        } else if (0xC0 <= (firstByte[0] & 0xFF) && (firstByte[0] & 0xFF) <= 0xDF) {
            byte[] secondByte = new byte[1];
            if (_stream.read(secondByte) < 0) {
                throw new IOException("EOF");
            }
            byte[] combined = new byte[2];
            combined[0] = firstByte[0];
            combined[1] = secondByte[0];

            ByteBuffer bb = ByteBuffer.wrap(combined);

            byte[] tmp = new byte[3];
            tmp[0] = combined[0];
            tmp[1] = combined[1];
            tmp[2] = 0x00;
            return new String(tmp).charAt(0);
        } else {
            throw new RuntimeException("Unknown byte char");
        }
    }

    short readShort() throws IOException {
        byte[] buffer = new byte[2];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        if (_stream.read(buffer) < 0) {
            throw new IOException("EOF");
        }
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        return bb.getShort();
    }

    byte readByte() throws IOException {
        byte[] buffer = new byte[1];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        if (_stream.read(buffer) < 0) {
            throw new IOException("EOF");
        }
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        return bb.get();
    }

    String readString() throws IOException {
        int count = 0;
        int shift = 0;
        boolean more = true;
        while (more) {
            byte b = (byte) _stream.read();
            count |= (b & 0x7F) << shift;
            shift += 7;
            if((b & 0x80) == 0) {
                more = false;
            }
        }
        int val = count;
//        System.out.println("d:" + val);

        byte[] buffer = new byte[val];
        if (_stream.read(buffer) < 0) {
            throw new IOException("EOF");
        }
        return new String(buffer);
    }

    Long readLong() throws IOException {
        byte[] buffer = new byte[8];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        if (_stream.read(buffer) < 0) {
            throw new IOException("EOF");
        }
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        return bb.getLong();
    }

    Boolean readBoolean() throws IOException {
        throw new RuntimeException("Unable to read boolean byte");
//        byte[] buffer = new byte[1];
//        ByteBuffer bb = ByteBuffer.wrap(buffer);
//        if (fin.read(buffer) < 0) {
//            throw new IOException("EOF");
//        }
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        bb.position(0);
//        return bb.getBoolean();
    }

    Double readDouble() throws IOException {
        byte[] buffer = new byte[8];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        if (_stream.read(buffer) < 0) {
            throw new IOException("EOF");
        }
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        return bb.getDouble();
    }

    Float readFloat() throws IOException {
        byte[] buffer = new byte[3];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        if (_stream.read(buffer) < 0) {
            throw new IOException("EOF");
        }
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(0);
        return bb.getFloat();
    }
}
