package org.nsu.networks.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private int port;
    private InetAddress address;
    private String path;

    public Client(int port, InetAddress address, String path) {
        this.port = port;
        this.address = address;
        this.path = path;
    }

    public void start() {
        try (Socket socket = new Socket(address, port);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             FileInputStream fileInputStream = new FileInputStream(path)) {
            File file = new File(path);
            if (file.exists()) {
                String[] paths = path.split("/");
                dataOutputStream.writeUTF(paths[paths.length - 1]);
                dataOutputStream.writeLong(file.length());
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String result = bufferedReader.readLine();
                    System.out.println(result);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}