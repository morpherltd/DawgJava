package com.morpherltd.dawg;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

class MReader {

    private ByteArrayInputStream _r;
    private final FileInputStream _stream;

    public MReader(String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        byte[] bytes = FileUtils.readFileToByteArray(new File(resource.getPath()));

        _stream = new FileInputStream(new File(resource.getPath()));

//        _r = new ByteArrayInputStream(bytes);
    }

    public int readInt() throws IOException {
//        byte[] bytesLength = new byte[4];
//        _r.read(bytesLength, 0, 4);
//        int num = ByteBuffer.wrap(bytesLength).getInt();
//        return num;
        return BinaryUtil.getInt(_stream);
    }

    public char readChar() throws IOException {
        return BinaryUtil.getChar(_stream);
    }

    public short readShort() throws IOException {
        return BinaryUtil.getShort(_stream);
    }

    public byte readByte() throws IOException {
        return BinaryUtil.getByte(_stream);
    }

    public String readString() throws IOException {
        return BinaryUtil.getString(_stream);
    }

    public Long readLong() throws IOException {
        return BinaryUtil.getLong(_stream);
    }

    public Boolean readBoolean() throws IOException {
        return BinaryUtil.getBoolean(_stream);
    }

    public Double readDouble() throws IOException {
        return BinaryUtil.getDouble(_stream);
    }

    public Float readFloat() throws IOException {
        return BinaryUtil.getFloat(_stream);
    }

    public void close() {
    }
}
