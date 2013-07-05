package net.esper.tacos.FakeMCserver;

public class Packet {

    public enum PacketType {
        PING,  // server list ping
        QUERY, // what most server list *websites* utilize
        JOIN, // Nothing much we can do except 0xFF, or we can do the auth handshake
        NOIDEA // placeholder for what we know nothing about
    }

    public static PacketType getPacketType(String header) {
        if (header.replace(" ", "").toUpperCase().equals("FE01")) {
            return PacketType.PING;
        }
        if (header.replace(" ", "").toUpperCase().equals("FEFD")) {
            return PacketType.QUERY;
        }
        if (header.replace(" ", "").toUpperCase().startsWith("02")) {
            return PacketType.JOIN;
        }
        return PacketType.NOIDEA;
    }
}
