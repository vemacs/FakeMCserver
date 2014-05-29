package me.vemacs.fakemcserver;

import me.vemacs.fakemcserver.data.StatusResponse;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class Main {
    static Properties prop = new Properties();
    public static StatusResponse response = null;

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
            List<Message> description = ChatConverter.toJSONChat(
                    ChatConverter.replaceColors(prop.getProperty("description")
                            .replace("\\n", "\n")));
            description.remove(0);
            response = new StatusResponse(
                    prop.getProperty("version"),
                    Integer.parseInt(prop.getProperty("protocol")),
                    Integer.parseInt(prop.getProperty("max")),
                    Integer.parseInt(prop.getProperty("online")),
                    description
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

}
