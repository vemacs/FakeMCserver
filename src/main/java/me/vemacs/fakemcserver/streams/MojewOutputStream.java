package me.vemacs.fakemcserver.streams;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MojewOutputStream extends ByteBufOutputStream {
    public MojewOutputStream(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        writeInt(s.length());
        write(data);
    }

    @Override
    public void writeInt(int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                writeByte(paramInt);
                return;
            }
            writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
}
