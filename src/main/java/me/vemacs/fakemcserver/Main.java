package me.vemacs.fakemcserver;

import me.vemacs.fakemcserver.data.StatusResponse;

import java.io.*;
import java.util.Properties;

public class Main {
    static Properties prop = new Properties();
    public static StatusResponse response = null;

    public static final char ESCAPE = '\u00A7';

    public static void main(String[] args) throws Exception {
        File config = new File("server.properties");

        if (!config.exists()) {
            try (OutputStream output = new FileOutputStream(config)) {
                prop.setProperty("version", "1.7.9");
                prop.setProperty("protocol", Integer.toString(5));
                prop.setProperty("max", Integer.toString(42069));
                prop.setProperty("online", Integer.toString(9001));
                prop.setProperty("description", "Blaze it");
                prop.store(output, null);
            }
        }

        try (InputStream input = new FileInputStream(config)) {
            prop.load(input);
            response = new StatusResponse(
                    prop.getProperty("version"),
                    Integer.parseInt(prop.getProperty("protocol")),
                    Integer.parseInt(prop.getProperty("max")),
                    Integer.parseInt(prop.getProperty("online")),
                    replaceColors(prop.getProperty("description")).replace("\\n", "\n")
            );
        }

        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 25565;
        }
        new SLPServer(port).run();
    }

    public static String replaceColors(String text) {
        char[] chrarray = text.toCharArray();
        for (int index = 0; index < chrarray.length; index++) {
            char chr = chrarray[index];
            if (chr != '&')
                continue;
            if ((index + 1) == chrarray.length)
                break;
            char forward = chrarray[index + 1];
            if ((forward >= '0' && forward <= '9') || (forward >= 'a' && forward <= 'f') || (forward >= 'k' && forward <= 'r'))
                chrarray[index] = ESCAPE;
        }
        return new String(chrarray);
    }
}
