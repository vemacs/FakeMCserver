package me.vemacs.fakemcserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vemacs.fakemcserver.streams.MojewInputStream;
import me.vemacs.fakemcserver.streams.MojewOutputStream;

public class BasePacketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MojewInputStream in = new MojewInputStream((ByteBuf) msg);
        // handshake
        int length = in.readInt();
        int id = in.readInt();
        int version = in.readInt();
        String address = in.readUTF();
        int port = in.readUnsignedShort();
        int state = in.readInt();
        System.out.println(length + ", " + id + ", " + version + ", " + address + ", " + port + ", " + state);
        // status request
        length = in.readInt();
        id = in.readInt();
        System.out.println(length + ", " + id);
        in.close();
        // status response
        String response = "{\"version\":{\"name\":\"1.7.2\",\"protocol\":4},\"players\":{\"max\":100,\"online\":5,\"sample\":[{\"name\":\"Thinkofdeath\",\"id\":\"4566e69fc90748ee8d71d7ba5aa00d20\"}]},\"description\":{\"text\":\"Hello world\"},\"favicon\":\"data:image/png;base64,<data>\"}";
        MojewOutputStream out = new MojewOutputStream((ByteBuf) msg);
        MojewOutputStream data = new MojewOutputStream(Unpooled.buffer());
        data.writeInt(0);
        data.writeUTF(response);
        data.close();
        out.writeInt(data.writtenBytes());
        out.write(data.buffer().array());
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
