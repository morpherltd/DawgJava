package ru.morpher.adjectivizer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class DataStreamStrings {
    static void writeString(DataOutputStream out, String str)
            throws IOException {
        byte[] data=str.getBytes("UTF-8");
        out.writeInt(data.length);
        out.write(data);
    }

    public static String readString(DataInputStream in) throws IOException {
        int length=in.readInt();
        byte[] data=new byte[length];
        in.readFully(data);
        return new String(data,"UTF-8");
    }
}
