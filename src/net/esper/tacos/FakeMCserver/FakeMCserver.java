package net.esper.tacos.FakeMCserver;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

public class FakeMCserver {

    public static final char ESCAPE = '\u00A7';

    public static void main(String[] args) throws Exception {
        // start configuration value load
        InputStream input = new FileInputStream(new File("config.yml"));
        Yaml yaml = new Yaml();
        Map config = (Map) yaml.load(input);
        String ip = (String) config.get("ip");
        int port = Integer.parseInt(((Integer) config.get("port")).toString());
        final int prot = Integer.parseInt(((Integer) config.get("prot")).toString());
        final String ver = replaceColors((String) config.get("ver"));
        final String motd = replaceColors((String) config.get("motd"));
        final int cur = Integer.parseInt(((Integer) config.get("cur")).toString());
        final int max = Integer.parseInt(((Integer) config.get("max")).toString());
        final String kick = replaceColors((String) config.get("kick"));
        ChannelFactory factory =
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new FakeMCserverHandler(prot, ver, motd, cur, max, kick));
            }
        });
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.bind(new InetSocketAddress(ip, port));
    }

    public static String replaceColors(String text) {
        char[] chrarray = text.toCharArray();
        for (int index = 0; index < chrarray.length; index++) {
            char chr = chrarray[index];
            if (chr != '&') {
                continue;
            }
            if ((index + 1) == chrarray.length) {
                break;
            }
            char forward = chrarray[index + 1];
            if ((forward >= '0' && forward <= '9') || (forward >= 'a' && forward <= 'f') || (forward >= 'k' && forward <= 'r')) {
                chrarray[index] = ESCAPE;
            }
        }
        return new String(chrarray);
    }

}