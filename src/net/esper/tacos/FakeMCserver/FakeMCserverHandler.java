package net.esper.tacos.FakeMCserver;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class FakeMCserverHandler extends SimpleChannelHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel ch = e.getChannel();
        ch.write(e.getMessage());
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        String temp = "";
        while(buf.readable()) {
            temp = temp + " " + buf.readByte();
        }
        temp = temp.replace("\n", "");
        System.out.println(temp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }

}
