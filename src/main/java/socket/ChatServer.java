// File: main.java.socket/ChatServer.java
package socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 1234;
    private static final Map<String, PrintWriter> clientOutputs = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Chat server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (BindException e) {
            System.err.println("Error: Port " + PORT + " is already in use.");
            System.err.println("Please try one of the following:");
            System.err.println("1. Close the application using this port");
            System.err.println("2. Wait a few minutes and try again");
            System.err.println("3. Use a different port by changing the PORT constant");
            System.exit(1);
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String userId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Read userId as first message from client
                userId = in.readLine();
                if (userId == null || userId.trim().isEmpty()) return;

                clientOutputs.put(userId, out);
                System.out.println(userId + " joined the chat");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("From " + userId + ": " + message);
                    broadcast("[" + userId + "] " + message, userId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}

                if (userId != null) {
                    clientOutputs.remove(userId);
                    System.out.println(userId + " left the chat");
                }
            }
        }

        private void broadcast(String message, String sender) {
            synchronized (clientOutputs) {
                for (Map.Entry<String, PrintWriter> entry : clientOutputs.entrySet()) {
                    if (!entry.getKey().equals(sender)) {
                        entry.getValue().println(message);
                    }
                }
            }
        }
    }
}
