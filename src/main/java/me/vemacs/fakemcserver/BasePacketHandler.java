package me.vemacs.fakemcserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vemacs.fakemcserver.streams.MojewInputStream;
import me.vemacs.fakemcserver.streams.MojewOutputStream;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;

public class BasePacketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MojewInputStream in = new MojewInputStream((ByteBuf) msg);
        // handshake
        int length = in.readInt();
        int id = in.readInt();
        if (id == 0) {
            int version = in.readInt();
            String address = in.readUTF();
            int port = in.readUnsignedShort();
            int state = in.readInt();
            System.out.println(length + ", " + id + ", " + version + ", " + address + ", " + port + ", " + state);
            // status request
            int len2 = in.readInt();
            id = in.readInt();
            System.out.println(len2 + ", " + id);
            in.close();
            // test packet response length
            MojewOutputStream data = new MojewOutputStream(Unpooled.buffer());
            data.writeInt(id);
            data.writeInt(version);
            data.writeUTF(address);
            data.writeShort(port);
            data.writeInt(state);
            data.close();
            System.out.println("Proper length: " + data.writtenBytes() + ", " + length);
            // status response
            String response = "{\"version\":{\"name\":\"1.7.9\",\"protocol\":5},\"players\":{\"max\":100,\"online\":5},\"description\":{\"text\":\"Hello world\"}}";
            MojewOutputStream out = new MojewOutputStream(Unpooled.buffer());
            data = new MojewOutputStream(Unpooled.buffer());
            data.writeInt(0);
            data.writeUTF(response);
            data.close();
            out.writeInt(data.writtenBytes());
            out.write(data.getData());
            out.close();
            System.out.println(DatatypeConverter.printHexBinary(out.getData()));
            ctx.writeAndFlush(out.buffer());
        } else if (id == 1) {
            // ping request
            long time = in.readLong();
            System.out.println(length + ", " + id + ", " + time);
            // ping response
            MojewOutputStream out = new MojewOutputStream(Unpooled.buffer());
            MojewOutputStream data = new MojewOutputStream(Unpooled.buffer());
            data.writeInt(length);
            data.writeInt(id);
            data.writeLong(time);
            data.close();
            out.writeInt(data.writtenBytes());
            out.write(data.getData());
            out.close();
            System.out.println(DatatypeConverter.printHexBinary(out.getData()));
            ctx.writeAndFlush(out.buffer());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
