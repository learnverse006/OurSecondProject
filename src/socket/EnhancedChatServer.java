package socket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class EnhancedChatServer {
    private static final int PORT = 1234;
    private static HashSet<ClientHandler> clients = new HashSet<>();
    private static ArrayList<String> usernames = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Chat Server started on port " + PORT);
            System.out.println("Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create new client handler
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                // Start client handler thread
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Broadcast message to all clients
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            try {
                client.sendMessage("TEXT", message);
            } catch (IOException e) {
                System.out.println("Error broadcasting message: " + e.getMessage());
            }
        }
    }

    // Send file to specific client
    public static void sendFile(String fileName, long fileSize, byte[] fileData, String senderName, ClientHandler recipient) {
        try {
            recipient.sendFile(fileName, senderName, fileSize, fileData);
        } catch (IOException e) {
            System.out.println("Error sending file: " + e.getMessage());
        }
    }

    // Remove client from list
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        usernames.remove(client.getUsername());
        System.out.println("Client disconnected: " + client.getUsername());
        broadcastMessage(client.getUsername() + " has left the chat.", null);
        updateUserList();
    }

    // Update user list for all clients
    public static void updateUserList() {
        StringBuilder userList = new StringBuilder("USERLIST:");
        for (String username : usernames) {
            userList.append(username).append(",");
        }
        broadcastMessage(userList.toString(), null);
    }

    // Add user to list
    public static void addUser(String username) {
        usernames.add(username);
        updateUserList();
    }

    // Client handler class
    static class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private String username;

        //        public ClientHandler(Socket socket) throws IOException {
//            this.socket = socket;
//            this.inputStream = new DataInputStream(socket.getInputStream());
//            this.outputStream = new DataOutputStream(socket.getOutputStream());
//            this.username = inputStream.readUTF();
//
//            // Add user to list
//            addUser(username);
//
//            // Notify all clients about new user
//            broadcastMessage(username + " has joined the chat.", null);
//        }
        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.username = inputStream.readUTF();

            // Add user to list
            addUser(username);

            // Send current user list to the new client first
            StringBuilder userList = new StringBuilder("USERLIST:");
            for (String username : usernames) {
                userList.append(username).append(",");
            }
            sendMessage("TEXT", userList.toString());

            // Then notify all clients about new user
            broadcastMessage(username + " has joined the chat.", null);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String messageType = inputStream.readUTF();

                    if (messageType.equals("TEXT")) {
                        String message = inputStream.readUTF();
                        broadcastMessage(username + ": " + message, this);
                    } else if (messageType.equals("FILE")) {
                        handleFileTransfer();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
                removeClient(this);
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("Error closing socket: " + ex.getMessage());
                }
            }
        }

        private void handleFileTransfer() throws IOException {
            String fileName = inputStream.readUTF();
            long fileSize = inputStream.readLong();

            // Read file data
            byte[] fileData = new byte[(int) fileSize];
            int bytesRead = 0;
            int currentRead;

            while (bytesRead < fileSize) {
                currentRead = inputStream.read(fileData, bytesRead, (int) (fileSize - bytesRead));
                if (currentRead == -1) break;
                bytesRead += currentRead;
            }

            // Notify all clients about the file
            broadcastMessage(username + " has shared a file: " + fileName, null);

            // Send the file to each client
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendFile(fileName, username, fileSize, fileData);
                }
            }
        }

        public void sendMessage(String type, String message) throws IOException {
            outputStream.writeUTF(type);
            outputStream.writeUTF(message);
        }

        public void sendFile(String fileName, String sender, long fileSize, byte[] fileData) throws IOException {
            outputStream.writeUTF("FILE");
            outputStream.writeUTF(fileName);
            outputStream.writeUTF(sender);
            outputStream.writeLong(fileSize);
            outputStream.write(fileData, 0, (int) fileSize);
        }

        public String getUsername() {
            return username;
        }
    }
}

