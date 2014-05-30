package me.vemacs.fakemcserver;

import me.vemacs.fakemcserver.data.StatusResponse;

import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;

import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;

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
                prop.setProperty("description", "&cBl&baze it\\n&fmaggots");
                prop.setProperty("engine", "classic");
                prop.store(output, null);
            }
        }

        String favicon = null;
        File iconFile = new File("server-icon.png");
        if (iconFile.exists()) {
            BufferedImage image = read(iconFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            write(image, "png", baos);
            favicon = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
        }

        try (InputStream input = new FileInputStream(config)) {
            prop.load(input);

            Message description;
            switch (prop.getProperty("engine")) {
                case "json":
                    description = ChatConverter.toJSONChat(
                            ChatConverter.replaceColors(prop.getProperty("description")
                                    .replace("\\n", "\n")));
                    break;
                default:
                    Message classicMsg = new Message();
                    classicMsg.text = ChatConverter.replaceColors(
                            prop.getProperty("description")).replace("\\n", "\n");
                    description = classicMsg;
            }

            response = new StatusResponse(
                    prop.getProperty("version"),
                    Integer.parseInt(prop.getProperty("protocol")),
                    Integer.parseInt(prop.getProperty("max")),
                    Integer.parseInt(prop.getProperty("online")),
                    description,
                    favicon
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
