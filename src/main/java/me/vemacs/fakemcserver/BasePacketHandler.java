package me.vemacs.fakemcserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

public class BasePacketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBufInputStream buffer = new ByteBufInputStream((ByteBuf) msg);
        int length = readVarInt(buffer);
        int id = readVarInt(buffer);
        int version = readVarInt(buffer);
        int len = readVarInt(buffer);
        String address = readString(buffer, len);
        int port = buffer.readUnsignedShort();
        int state = readVarInt(buffer);
        System.out.println(length + ", " + id + ", " + version + ", " + address + ", " + port + ", " + state);
        ((ByteBuf) msg).release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public int readVarInt(ByteBufInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public String readString(ByteBufInputStream in, int length) throws Exception {
        byte[] input = new byte[length];
        in.readFully(input);
        return new String(input);
    }
}
