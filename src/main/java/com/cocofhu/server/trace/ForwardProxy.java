package com.cocofhu.server.trace;

import com.cocofhu.server.mysql.Payload;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ForwardProxy {
    private static final int LISTENED_PORT = 9988;

    private static class ParameterHandler {
        final String prefix;
        final String attribute;
        final Map<String, Object> config;
        final Function<String, Object> convert;

        ParameterHandler(String prefix, String attribute, Map<String, Object> config, Function<String, Object> convert) {
            this.prefix = prefix;
            this.attribute = attribute;
            this.config = config;
            this.convert = convert;
        }

        boolean handle(String arg) {
            if (!arg.startsWith(prefix)) return false;
            String val = arg.replace(prefix, "");
            try {
                Object o = convert.apply(val);
                config.put(attribute, o);
            } catch (Exception e) {
                log.warn("{}, cannot parse {}, using '{}' as default. ", e.getMessage(), attribute, config.get(attribute));
            }
            return true;
        }
    }

    private static class PacketHandler implements Runnable{
        private final String name;
        private final OutputStream out;
        private final InputStream in;
        private final Socket server;
        private final Socket client;

        private PacketHandler(String name, OutputStream out, InputStream in, Socket server, Socket client) {
            this.name = name;
            this.out = out;
            this.in = in;
            this.server = server;
            this.client = client;
        }

        @Override
        public void run() {
            while(true){
                try {
                    Payload packet = Payload.readPackage(in);
                    log.info("packet read from {} \n\n{}\n\n", name, packet.dumpAsHex());
                    packet.writePackage(out);
                } catch (IOException e) {
                    log.info("an error occurred, stop 2 connections, message: {}.", e.getMessage());
                    try {
                        in.close();
                        out.close();
                        server.close();
                        client.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        // File -> Project Structure -> artifacts
        Map<String, Object> config = new HashMap<>();
        config.put("ip", "127.0.0.1");
        config.put("port", 3306);
        config.put("maxClient", 1000);
        List<ParameterHandler> handlers = new ArrayList<>();
        handlers.add(new ParameterHandler("-ip=", "ip", config, a -> a));
        handlers.add(new ParameterHandler("-p=", "port", config, Integer::valueOf));
        for (String arg : args) {
            for (ParameterHandler h : handlers) if (h.handle(arg)) break;
        }
        String ip = (String) config.get("ip");
        int port = (int) config.get("port");
        int maxClient = (int) config.get("maxClient");
        log.info("mysql packet proxy started. mysql ip = {}, mysql port = {}, listened port = {}", ip, port, LISTENED_PORT);
        log.info("using mysql -h 127.0.0.1 -P{} -u root -proot --ssl-mode=DISABLE to debugging", LISTENED_PORT);
        try (ServerSocket serverSocket = new ServerSocket(LISTENED_PORT)) {
            while(maxClient > 0){
                Socket socket = serverSocket.accept();
                Socket proxy = new Socket(ip, port);
                Thread t1 = new Thread(new PacketHandler("Client", socket.getOutputStream(), proxy.getInputStream(), socket, proxy));
                Thread t2 = new Thread(new PacketHandler("Server", proxy.getOutputStream(), socket.getInputStream(), socket, proxy));
                t1.start();
                t2.start();
                log.info("someone connected, 2 thread started for debugging.");
                --maxClient;
            }
        } catch (Exception e) {
            log.error("fatal error, system exited, message:{}", e.getMessage());
        }

    }
}
