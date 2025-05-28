package socket;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import models.FriendshipDAO;

public class ChatServer {
    private static final int PORT = 1234;
    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("Error in server: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("FRIEND_REQUEST:")) {
                        handleFriendRequest(inputLine);
                    } else if (inputLine.startsWith("FRIEND_ACCEPT:")) {
                        handleFriendAccept(inputLine);
                    } else if (inputLine.startsWith("FRIEND_REJECT:")) {
                        handleFriendReject(inputLine);
                    } else if (inputLine.startsWith("LOGIN:")) {
                        handleLogin(inputLine);
                    } else if (inputLine.startsWith("MESSAGE:")) {
                        handleMessage(inputLine);
                    } else {
                        broadcastMessage(inputLine);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client " + username + ": " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void handleLogin(String message) {
            username = message.substring(6);
            clients.put(username, this);
            System.out.println(username + " joined the chat");
        }

        private void handleFriendRequest(String message) {
            // Format: FRIEND_REQUEST:fromUserId:toUserId
            String[] parts = message.split(":");
            if (parts.length == 3) {
                String fromUserId = parts[1];
                String toUserId = parts[2];
                
                // Store the request in database
                try {
                    FriendshipDAO.sendFriendRequest(Integer.parseInt(fromUserId), Integer.parseInt(toUserId));
                    
                    // Send notification to the recipient
                    ClientHandler recipient = clients.get(toUserId);
                    if (recipient != null) {
                        recipient.out.println("FRIEND_REQUEST_NOTIFICATION:" + fromUserId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleFriendAccept(String message) {
            // Format: FRIEND_ACCEPT:fromUserId:toUserId
            String[] parts = message.split(":");
            if (parts.length == 3) {
                String fromUserId = parts[1];
                String toUserId = parts[2];
                
                try {
                    FriendshipDAO.acceptFriendRequest(Integer.parseInt(fromUserId), Integer.parseInt(toUserId));
                    
                    // Notify both users
                    ClientHandler sender = clients.get(fromUserId);
                    if (sender != null) {
                        sender.out.println("FRIEND_REQUEST_ACCEPTED:" + toUserId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleFriendReject(String message) {
            // Format: FRIEND_REJECT:fromUserId:toUserId
            String[] parts = message.split(":");
            if (parts.length == 3) {
                String fromUserId = parts[1];
                String toUserId = parts[2];
                
                try {
                    FriendshipDAO.rejectFriendRequest(Integer.parseInt(fromUserId), Integer.parseInt(toUserId));
                    
                    // Notify the sender
                    ClientHandler sender = clients.get(fromUserId);
                    if (sender != null) {
                        sender.out.println("FRIEND_REQUEST_REJECTED:" + toUserId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleMessage(String message) {
            // Format: MESSAGE:chat_id:sender_id:receiver_id:content
            String[] parts = message.split(":", 5);
            if (parts.length == 5) {
                String chatId = parts[1];
                String senderId = parts[2];
                String receiverId = parts[3];
                String content = parts[4];
                // Gửi cho receiver
                ClientHandler receiver = clients.get(receiverId);
                if (receiver != null) {
                    receiver.out.println(message);
                }
                // Gửi lại cho sender nếu khác receiver
                if (!senderId.equals(receiverId)) {
                    ClientHandler sender = clients.get(senderId);
                    if (sender != null) {
                        sender.out.println(message);
                    }
                }
            }
        }

        private void broadcastMessage(String message) {
            for (ClientHandler client : clients.values()) {
                if (client != this) {
                    client.out.println(username + ": " + message);
                }
            }
        }

        private void cleanup() {
            try {
                if (username != null) {
                    clients.remove(username);
                    System.out.println(username + " left the chat");
                }
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error during cleanup: " + e.getMessage());
            }
        }
    }
}