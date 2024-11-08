package org.nsu.networks.client;


import java.io.IOException;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        if (args.length == 6 && args[0].equals("-port") && args[2].equals("-address") && args[4].equals("-path")) {
            try {
                int port = Integer.parseInt(args[1]);
                InetAddress inetAddress = InetAddress.getByName(args[3]);
                String path = args[5];
                Client client = new Client(port, inetAddress, path);
                client.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Usage: -port <int> -address <ip> -path <path>");
        }
    }
}
