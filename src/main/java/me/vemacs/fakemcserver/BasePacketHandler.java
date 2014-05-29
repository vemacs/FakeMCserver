package me.vemacs.fakemcserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vemacs.fakemcserver.streams.MojewInputStream;

public class BasePacketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MojewInputStream buffer = new MojewInputStream((ByteBuf) msg);
        int length = buffer.readInt();
        int id = buffer.readInt();
        int version = buffer.readInt();
        String address = buffer.readUTF();
        int port = buffer.readUnsignedShort();
        int state = buffer.readInt();
        System.out.println(length + ", " + id + ", " + version + ", " + address + ", " + port + ", " + state);
        ((ByteBuf) msg).release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
