// File: main.java.socket/ChatClient.java
package socket;

import models.Message;
import models.MessageDAO;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dataOut = new DataOutputStream(socket.getOutputStream());
        dataIn = new DataInputStream(socket.getInputStream());
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
            chatClient.connect(serverAddress, port);
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
            // Parse message from main.java.socket format to Message object
            // Implementation depends on your message format
            return null;
        }

        public void disconnect() {
            chatClient.disconnect();
        }
    }
}
