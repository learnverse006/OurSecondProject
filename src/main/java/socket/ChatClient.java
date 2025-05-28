// File: main.java.socket/ChatClient.java
package socket;

import models.Message;
import models.MessageDAO;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class ChatClient {
    // Message types for friend requests
    public static final String FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String FRIEND_ACCEPT = "FRIEND_ACCEPT";
    public static final String FRIEND_REJECT = "FRIEND_REJECT";
    public static final String FRIEND_REQUEST_NOTIFICATION = "FRIEND_REQUEST_NOTIFICATION";
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Consumer<String> onFriendRequestReceived;
    private Consumer<String> onFriendRequestAccepted;
    private Consumer<String> onFriendRequestRejected;

    public void connect(String host, int port, int userId) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dataOut = new DataOutputStream(socket.getOutputStream());
        dataIn = new DataInputStream(socket.getInputStream());
        send("LOGIN:" + userId);
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getWriter() {
        return out;
    }

    public DataInputStream getDataInputStream() {
        return dataIn;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOut;
    }

    public void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void listen(Consumer<String> onMessageReceived) {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith(FRIEND_REQUEST_NOTIFICATION)) {
                        String[] parts = msg.split(":");
                        if (parts.length == 3 && onFriendRequestReceived != null) {
                            onFriendRequestReceived.accept(parts[1]);
                        }
                    } else if (msg.startsWith(FRIEND_ACCEPT)) {
                        String[] parts = msg.split(":");
                        if (parts.length == 3 && onFriendRequestAccepted != null) {
                            onFriendRequestAccepted.accept(parts[1]);
                        }
                    } else if (msg.startsWith(FRIEND_REJECT)) {
                        String[] parts = msg.split(":");
                        if (parts.length == 3 && onFriendRequestRejected != null) {
                            onFriendRequestRejected.accept(parts[1]);
                        }
                    }
                    onMessageReceived.accept(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (dataIn != null) dataIn.close();
            if (dataOut != null) dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    public void sendFriendRequest(int fromUserId, int toUserId) {
        String message = String.format("%s:%d:%d", FRIEND_REQUEST, fromUserId, toUserId);
        send(message);
    }

    public void acceptFriendRequest(int fromUserId, int toUserId) {
        String message = String.format("%s:%d:%d", FRIEND_ACCEPT, fromUserId, toUserId);
        send(message);
    }

    public void rejectFriendRequest(int fromUserId, int toUserId) {
        String message = String.format("%s:%d:%d", FRIEND_REJECT, fromUserId, toUserId);
        send(message);
    }

    public void setOnFriendRequestReceived(Consumer<String> callback) {
        this.onFriendRequestReceived = callback;
    }

    public void setOnFriendRequestAccepted(Consumer<String> callback) {
        this.onFriendRequestAccepted = callback;
    }

    public void setOnFriendRequestRejected(Consumer<String> callback) {
        this.onFriendRequestRejected = callback;
    }

    public static class ChatManager {
        private final ChatClient chatClient;
        private final int userId;
        private final int chatId;
        private Consumer<Message> onMessageReceived;
        private Consumer<Exception> onError;

        public ChatManager(int userId, int chatId) {
            this.userId = userId;
            this.chatId = chatId;
            this.chatClient = new ChatClient();
        }

        public void connect(String serverAddress, int port) throws Exception {
            chatClient.connect(serverAddress, port, userId);
            chatClient.send(String.valueOf(userId));

            chatClient.listen(message -> {
                try {
                    Message msg = parseMessage(message);
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(msg);
                    }
                } catch (Exception e) {
                    if (onError != null) {
                        onError.accept(e);
                    }
                }
            });
        }
// đang bị bug không load được fx
        public void loadHistory() throws Exception {
            List<Message> messages = MessageDAO.getMessageByChatID(chatId);
            for (Message msg : messages) {
                if (onMessageReceived != null) {
                    onMessageReceived.accept(msg);
                }
            }
        }

        public void sendMessage(String content, Message.MessageType type) throws Exception {
            Message message = new Message();
            message.setChatID(chatId);
            message.setSenderID(userId);
            message.setReceiverID(0);
            message.setContent(content);
            message.setMessageType(type);
            message.setCreateAt(java.time.LocalDateTime.now());

            MessageDAO.saveMessage(message);
            chatClient.send(content);
        }

        public void setOnMessageReceived(Consumer<Message> callback) {
            this.onMessageReceived = callback;
        }

        public void setOnError(Consumer<Exception> callback) {
            this.onError = callback;
        }

        private Message parseMessage(String rawMessage) {
            try {
                Message message = new Message();
                if (rawMessage.startsWith("[")) {
                    // Regular chat message format: [userId] content
                    int endBracket = rawMessage.indexOf("]");
                    if (endBracket > 0) {
                        String senderId = rawMessage.substring(1, endBracket);
                        String content = rawMessage.substring(endBracket + 2);
                        message.setSenderID(Integer.parseInt(senderId));
                        message.setContent(content);
                        message.setMessageType(Message.MessageType.TEXT);
                    }
                } else if (rawMessage.startsWith("[FILE]:")) {
                    // File message format: [FILE]:filename:base64data
                    String[] parts = rawMessage.split(":", 3);
                    if (parts.length == 3) {
                        message.setContent(rawMessage);
                        message.setMessageType(Message.MessageType.FILE);
                    }
                } else if (rawMessage.startsWith("[IMG]")) {
                    // Image message format: [IMG]base64data
                    message.setContent(rawMessage);
                    message.setMessageType(Message.MessageType.IMAGE);
                }
                message.setCreateAt(java.time.LocalDateTime.now());
                return message;
            } catch (Exception e) {
                System.err.println("Error parsing message: " + e.getMessage());
                return null;
            }
        }

        public void disconnect() {
            try {
                if (chatClient != null) {
                    chatClient.disconnect();
                }
            } catch (Exception e) {
                System.err.println("Error disconnecting chat manager: " + e.getMessage());
            }
        }
    }
}
