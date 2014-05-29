package me.vemacs.fakemcserver;

public class Main {
    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 25565;
        }
        new SLPServer(port).run();
    }
}
