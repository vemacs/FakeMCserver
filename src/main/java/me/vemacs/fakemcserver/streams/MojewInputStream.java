package me.vemacs.fakemcserver.streams;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MojewInputStream extends ByteBufInputStream {
    public MojewInputStream(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public String readUTF() throws IOException {
        byte[] input = new byte[readInt()];
        readFully(input);
        return new String(input, StandardCharsets.UTF_8);
    }

    @Override
    public int readInt() throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }
}
