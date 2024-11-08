package org.nsu.networks.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Server {
    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            (new Thread(new ClientHandler(serverSocket.accept()))).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        private long startTime;
        private long lastReportedTime;
        private long bytesReceived;
        private long lastBytesReceived;
        private boolean speedReported;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                 PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)) {
                String path = dataInputStream.readUTF();
                long size = dataInputStream.readLong();

                int read;
                byte[] buffer = new byte[4096];

                bytesReceived = 0;
                lastBytesReceived = 0;
                startTime = System.currentTimeMillis();
                lastReportedTime = startTime;
                speedReported = false;

                scheduler.scheduleAtFixedRate(new Timer(), 3, 3, TimeUnit.SECONDS);

                try (FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + "/src/main/java/org/nsu/networks/server/uploads/" + path)) {
                    while ((bytesReceived < size) && (read = dataInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, read);
                        bytesReceived += read;
                    }
                }

                if (!speedReported) {
                    long currentTime = System.currentTimeMillis();
                    System.out.printf("Average Speed: %.2f bytes/s%n", (double) bytesReceived / ((currentTime - startTime) / 1000.0));
                }

                if (bytesReceived == size) {
                    printWriter.println("Success");
                } else {
                    printWriter.println("Failed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                scheduler.shutdown();
            }
        }

        private class Timer implements Runnable {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - lastReportedTime;
                long bytesTransferred = bytesReceived - lastBytesReceived;
                double speedInstantaneous = (double) bytesTransferred / (elapsedTime / 1000.0);
                double speedAverage = (double) bytesReceived / ((currentTime - startTime) / 1000.0);

                System.out.printf("Instantaneous Speed: %.2f bytes/s, Average Speed: %.2f bytes/s%n",
                        speedInstantaneous,
                        speedAverage);
                speedReported = true;

                lastReportedTime = currentTime;
                lastBytesReceived = bytesReceived;
            }
        }
    }
}