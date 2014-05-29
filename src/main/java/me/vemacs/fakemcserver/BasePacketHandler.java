package me.vemacs.fakemcserver;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vemacs.fakemcserver.data.StatusResponse;
import me.vemacs.fakemcserver.streams.MojewInputStream;
import me.vemacs.fakemcserver.streams.MojewOutputStream;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;

public class BasePacketHandler extends ChannelInboundHandlerAdapter {
    private final Gson gson = new Gson();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MojewInputStream in = new MojewInputStream((ByteBuf) msg);
        // handshake
        int length = in.readInt();
        int id = in.readInt();
        if (id == 0) {
            // status request
            try {
                int version = in.readInt();
                String address = in.readUTF();
                int port = in.readUnsignedShort();
                int state = in.readInt();
                System.out.println(length + ", " + id + ", " + version + ", " + address + ", " + port + ", " + state);
                length = in.readInt();
                id = in.readInt();
            } catch (Exception ignored) {
            }
            in.close();
            System.out.println(length + ", " + id);
            // status response
            String response = gson.toJson(Main.response);
            System.out.println(response);
            MojewOutputStream out = new MojewOutputStream(Unpooled.buffer());
            MojewOutputStream data = new MojewOutputStream(Unpooled.buffer());
            data.writeInt(0);
            data.writeUTF(response);
            data.close();
            out.writeInt(data.writtenBytes());
            out.write(data.getData());
            out.close();
            // System.out.println(DatatypeConverter.printHexBinary(out.getData()));
            ctx.writeAndFlush(out.buffer());
        } else if (id == 1) {
            // ping request
            long time = in.readLong();
            System.out.println(length + ", " + id + ", " + time);
            // ping response
            MojewOutputStream out = new MojewOutputStream(Unpooled.buffer());
            MojewOutputStream data = new MojewOutputStream(Unpooled.buffer());
            data.writeInt(1);
            data.writeLong(time);
            data.close();
            out.writeInt(data.writtenBytes());
            out.write(data.getData());
            out.close();
            // System.out.println(DatatypeConverter.printHexBinary(out.getData()));
            ctx.writeAndFlush(out.buffer());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
