package me.vemacs.fakemcserver.data;

public class StatusResponse {
    Version version;
    Players players;
    String description;

    public StatusResponse(String name, int protocol, int max, int online, String description) {
        this.version = new Version(name, protocol);
        this.players = new Players(max, online);
        this.description = description;
    }

    public class Version {
        String name = "1.7.9";
        int protocol = 5;

        public Version(String name, int protocol) {
            this.name = name;
            this.protocol = protocol;
        }
    }

    class Players {
        int max = 9000;
        int online = 420;

        Players(int max, int online) {
            this.max = max;
            this.online = online;
        }
    }
}
