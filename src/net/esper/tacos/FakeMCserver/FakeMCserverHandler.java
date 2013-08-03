package net.esper.tacos.FakeMCserver;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.math.BigInteger;
import java.nio.charset.Charset;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

public class FakeMCserverHandler extends SimpleChannelHandler {

    private static String start = "ff00";
    private static String header = "00 a700 3100 0000";
    private static String sep = "00 00 00";

    private int prot;
    private String ver;
    private String motd;
    private int cur;
    private int max;
    private String kick;

    public FakeMCserverHandler(int prot, String ver, String motd, int cur, int max, String kick) {
        this.prot = prot;
        this.ver = ver;
        this.motd = motd;
        this.cur = cur;
        this.max = max;
        this.kick = kick;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        Channel ch = e.getChannel();
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        byte[] tmp = buf.array();
        String temp = printHexBinary(tmp);
        System.out.println("Received packet: " + temp.trim());
        String response = "";
        switch (Packet.getPacketType(temp)) {
            case PING:
                response = generate();
                break;
            case JOIN:
                response = kick();
                break;
            case QUERY:
                break;
            case NOIDEA:
                break;
        }
        ChannelBuffer res = copiedBuffer(parseHexBinary(response));
        ch.write(res);
        System.out.println("Sent packet: " + response);
        ch.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        // e.getCause().printStackTrace();
        System.out.println(e.getChannel().getRemoteAddress().toString() + " disconnected");
        Channel ch = e.getChannel();
        ch.close();
    }

    private String kick() {
        System.out.println("Length: " + kick.length());
        return (start + Integer.toHexString(kick.length()) + "00" + toHex(kick)).toUpperCase();
    }

    private String generate() {
        String buf = header + toHex(Integer.toString(prot)) + sep + toHex(ver) + sep + toHex(motd) + sep +
                toHex(Integer.toString(cur)) + sep + toHex(Integer.toString(max));
        int stringLength = Integer.toString(prot).length() + 1 + ver.length() + 1 + motd.length() +
                1 + Integer.toString(cur).length() + 1 + Integer.toString(max).length() + 3;
        buf = start + Integer.toHexString(stringLength) + buf;
        buf = buf.replace(" ", "").toUpperCase();
        return buf;
    }

    public static String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(Charset.forName("UTF-16BE"))));
    }

}
