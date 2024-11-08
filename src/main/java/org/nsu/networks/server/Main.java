package org.nsu.networks.server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 2 && args[0].equals("-port")) {
            try {
                int port = Integer.parseInt(args[1]);
                Server server = new Server();
                server.start(port);
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Usage: -port <int>");
        }
    }

}
