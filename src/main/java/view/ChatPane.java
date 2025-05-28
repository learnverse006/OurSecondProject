// File: main.java.view/ChatPane.java
package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import socket.*;
import models.*;
import java.util.*;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.shape.Circle;

public class ChatPane {
    private final BorderPane view;
    private final VBox messagesBox = new VBox(10);
    private final ChatClient chatClient = new ChatClient();
    private final String userId;
    private Window window;
    private Consumer<String> onFriendRequestReceived;
    private Consumer<String> onFriendRequestAccepted;
    private Consumer<String> onFriendRequestRejected;
    private int chatId;
    private int currID;
    private int friendId;
    private String myAvatarUrl;
    private String friendAvatarUrl;

    // chat pane:
    public ChatPane(int chatId, int currID, int friendId) {
        this.chatId = chatId;
        this.currID = currID;
        this.friendId = friendId;
        view = new BorderPane();
        this.userId = String.valueOf(currID);
        view.setStyle("-fx-background-color: #ffffff;");

        // Add scene listener to update window reference
        view.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                window = newScene.getWindow();
            }
        });

        // Header
        Label headerLabel = new Label("GROUP 1");
        headerLabel.setFont(Font.font(18));
        headerLabel.setPadding(new Insets(10));
        BorderPane.setAlignment(headerLabel, Pos.CENTER_LEFT);
        view.setTop(headerLabel);

        // Message area (center)
        messagesBox.setPadding(new Insets(10));
        messagesBox.setStyle("-fx-background-color: #f9f9f9;");

        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        view.setCenter(scrollPane);

        // Footer (input)
        HBox toolBar = new HBox(10);
        toolBar.setPadding((new Insets(10)));
        toolBar.setStyle("-fx-background-color: #4D55CC;");
        toolBar.setPrefWidth(500);
        Button sendEmoji = new Button("Emoji");
        Button sendFile = new Button("Choose File");
        Button sendGifFile = new Button("GIF");
        Button sendRecord = new Button("Voice Chat");
        Button sendImg = new Button("Image");
        Button sendVoice = new Button("Send voice");
        toolBar.getChildren().addAll(sendEmoji, sendFile, sendGifFile, sendRecord, sendImg, sendVoice);

//        main.java.view.setBottom(toolBar);

        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: #eeeeee;");

        TextField messageField = new TextField();
        messageField.setPromptText("Nhập tin nhắn...");
        messageField.setPrefWidth(560);
        messagesBox.setFillWidth(true);


        Button sendButton = new Button("Gửi");
//        Button sendEmoji = new Button("Emoji");
//        Button sendFile = new Button("Choose File");
        inputBox.getChildren().addAll(messageField, sendButton);

        VBox chatField = new VBox(3, toolBar, inputBox);

        view.setBottom(chatField);
        // Load history and scroll to bottom
        try {
            System.out.println("[ChatPane] Loading history for chatId: " + chatId + ", currID: " + currID);
            List<Message> oldMessages = MessageDAO.getMessageByChatID(chatId);
            System.out.println("[ChatPane] Loaded messages: " + oldMessages.size());
            messagesBox.getChildren().clear();
            Runnable loadHistory = () -> {
                if (oldMessages.isEmpty()) {
                    System.out.println("[ChatPane] No messages found for chatId: " + chatId);
                }
                for (Message msg : oldMessages) {
                    int senderId = msg.getSenderID();
                    boolean isSender = senderId == currID;
                    String avatarUrl = getAvatarUrlByUserId(senderId);
                    messagesBox.getChildren().add(createMessageBubble(msg.getContent(), isSender, avatarUrl, msg.getCreateAt()));
                }
                scrollToBottom();
            };
            if (window == null) {
                Platform.runLater(loadHistory);
            } else {
                loadHistory.run();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Socket setup
        try {
            chatClient.connect("26.66.135.84", 1234, currID);
            chatClient.send(userId); // gửi tên user đầu tiên để main.java.server nhận biết

            chatClient.listen(message -> Platform.runLater(() -> {
                // Chỉ xử lý các message thực sự
                if (message.startsWith("MESSAGE:")) {
                    String[] parts = message.split(":", 5);
                    if (parts.length == 5) {
                        int msgChatId = Integer.parseInt(parts[1]);
                        int senderId = Integer.parseInt(parts[2]);
                        String content = parts[4];
                        if (msgChatId == this.chatId) {
                            boolean isSender = senderId == currID;
                            String avatarUrl = getAvatarUrlByUserId(senderId);
                            // Bỏ qua tin nhắn dạng "x: x" (kỹ thuật)
                            if (content.matches("\\d+\\s*:\\s*\\d+")) {
                                return;
                            }
                            if (content.startsWith("[IMG]")) {
                                ImageView imageView = util.FileTransferHandler.receiveImage(content);
                                if (imageView != null) {
                                    HBox bubble = util.FileTransferHandler.buildImageBubble(imageView, isSender);
                                    messagesBox.getChildren().add(bubble);
                                }
                            } else if (content.startsWith("[FILE]:")) {
                                String[] fileParts = util.FileTransfer.parseFileMessage(content);
                                if (fileParts.length == 3) {
                                    String fileName = fileParts[1];
                                    byte[] fileData = util.FileTransfer.decodeBase64File(fileParts[2]);
                                    HBox download = util.FileTransfer.buildDownloadableFile(fileName, fileData, view.getScene().getWindow(), isSender);
                                    messagesBox.getChildren().add(download);
                                }
                            } else {
                                messagesBox.getChildren().add(createMessageBubble(content, isSender, avatarUrl, java.time.LocalDateTime.now()));
                            }
                            scrollToBottom();
                        }
                    }
                    return;
                } else if (message.startsWith(ChatClient.FRIEND_REQUEST_NOTIFICATION)) {
                    String[] parts = message.split(":");
                    if (parts.length == 3) {
                        int fromUserId = Integer.parseInt(parts[1]);
                        if (onFriendRequestReceived != null) {
                            onFriendRequestReceived.accept(String.valueOf(fromUserId));
                        }
                    }
                } else if (message.startsWith(ChatClient.FRIEND_ACCEPT)) {
                    String[] parts = message.split(":");
                    if (parts.length == 3) {
                        int fromUserId = Integer.parseInt(parts[1]);
                        int toUserId = Integer.parseInt(parts[2]);
                        if (onFriendRequestAccepted != null) {
                            onFriendRequestAccepted.accept(String.valueOf(fromUserId));
                        }
                    }
                } else if (message.startsWith(ChatClient.FRIEND_REJECT)) {
                    String[] parts = message.split(":");
                    if (parts.length == 3) {
                        int fromUserId = Integer.parseInt(parts[1]);
                        if (onFriendRequestRejected != null) {
                            onFriendRequestRejected.accept(String.valueOf(fromUserId));
                        }
                    }
                }
                // Các message khác (ví dụ: chỉ là số, userId, LOGIN, ...) thì bỏ qua, KHÔNG render ra UI!
            }));


        } catch (Exception e) {
            e.printStackTrace();
        }
        sendEmoji.setOnAction(e -> {
            List<String> emojis = List.of("😄", "😂", "😍", "😎", "🎉", "🔥", "❤️", "👍", "👀");
            ChoiceDialog<String> dialog = new ChoiceDialog<>(emojis.get(0), emojis);
            dialog.setTitle("Chọn Emoji");
            dialog.setHeaderText("Chèn biểu tượng cảm xúc vào tin nhắn");
            dialog.setContentText("Emoji:");

            dialog.showAndWait().ifPresent(selectedEmoji -> {
                messageField.appendText(selectedEmoji);
            });
        });


        sendFile.setOnAction(e -> {
            if (window == null) {
                window = view.getScene().getWindow();
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file để gửi");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Tất cả file", "*.*"),
                    new FileChooser.ExtensionFilter("Tài liệu", "*.pdf", "*.docx", "*.xlsx", "*.txt")
            );

            File chosenFile = fileChooser.showOpenDialog(window);
            if (chosenFile != null) {
                try {
                    String msg = util.FileTransfer.buildFileMessage(chosenFile); // gửi dạng [FILE]:...
                    chatClient.send(msg);

                    // Hiển thị xác nhận đã gửi
                    Label label = new Label("📄 Đã gửi file: " + chosenFile.getName());
                    HBox box = new HBox(label);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    messagesBox.getChildren().add(box);
                    scrollToBottom();

                    // lưu vào db
                    Message message = new Message();
                    message.setChatID(chatId);
                    message.setSenderID(currID);
                    message.setReceiverID(0);
                    message.setContent(msg);
                    message.setMessageType(Message.MessageType.FILE);
                    message.setCreateAt(java.time.LocalDateTime.now());
                    MessageDAO.saveMessage(message);

                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        sendButton.setOnAction(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty()) {
                String msgToSend = String.format("MESSAGE:%d:%d:%d:%s", chatId, currID, friendId, text);
                chatClient.send(msgToSend);
                messageField.clear();
                try {
                    Message message = new Message();
                    message.setChatID(chatId);
                    message.setSenderID(currID);
                    message.setReceiverID(friendId); // hoặc null nếu là group
                    message.setContent(text);
                    message.setMessageType(Message.MessageType.TEXT);
                    message.setCreateAt(java.time.LocalDateTime.now());
                    MessageDAO.saveMessage(message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        sendImg.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn ảnh để gửi");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            File imageFile = fileChooser.showOpenDialog(sendImg.getScene().getWindow());
            if (imageFile != null) {
                try {
                    FileInputStream fis = new FileInputStream(imageFile);
                    byte[] imageBytes = fis.readAllBytes();
                    fis.close();

                    String base64 = Base64.getEncoder().encodeToString(imageBytes);
                    String messageToSend = "[IMG]" + base64;
                    chatClient.send(messageToSend);

                    // Hiển thị ảnh tại máy gửi
                    javafx.scene.image.ImageView imgView = util.FileTransferHandler.previewImage(imageFile);
                    HBox bubble = util.FileTransferHandler.buildImageBubble(imgView, true);
                    messagesBox.getChildren().add(bubble);
                    scrollToBottom();

                    // lưu vào db
                    Message message = new Message();
                    message.setChatID(chatId);
                    message.setSenderID(currID);
                    message.setReceiverID(0);
                    message.setContent(messageToSend);
                    message.setMessageType(Message.MessageType.IMAGE);
                    message.setCreateAt(java.time.LocalDateTime.now());
                    MessageDAO.saveMessage(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
//        sendRecord.setOnAction(e -> {
//            new Thread(() -> {
//                try {
//                    LibVosk.setLogLevel(0);
//                    Model model = new Model("main.java.models/vosk-model-small-vi-0.22"); // Đường dẫn tới model tiếng Việt
//                    Recognizer recognizer = new Recognizer(model, 16000.0f);
//
//                    AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
//                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//                    TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
//                    microphone.open(format);
//                    microphone.start();
//
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//                    StringBuilder resultText = new StringBuilder();
//
//                    long end = System.currentTimeMillis() + 8000;
//                    while (System.currentTimeMillis() < end) {
//                        bytesRead = microphone.read(buffer, 0, buffer.length);
//                        if (recognizer.acceptWaveForm(buffer, bytesRead)) {
//                            String result = recognizer.getResult();
//                            // Lấy text từ JSON kết quả
//                            String text = result.replaceAll(".*\\\"text\\\":\\\"(.*?)\\\".*", "$1");
//                            resultText.append(text).append(" ");
//                        }
//                    }
//                    microphone.stop();
//                    microphone.close();
//                    recognizer.close();
//                    model.close();
//
//                    // Đưa text vào messageField
//                    Platform.runLater(() -> messageField.setText(resultText.toString().trim()));
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }).start();
//        });

        // Gửi tin nhắn

    }

    private String extractSender(String raw) {
        int start = raw.indexOf("[") + 1;
        int end = raw.indexOf("]");
        return (start >= 0 && end > start) ? raw.substring(start, end) : "";
    }

    private String extractContent(String raw) {
        int end = raw.indexOf("]");
        return (end >= 0 && raw.length() > end + 2) ? raw.substring(end + 2) : raw;
    }

    private HBox createMessageBubble(String text, boolean isSender, String avatarUrl, LocalDateTime timestamp) {
        if ("3: 3".equals(text)) {
            return new HBox(); // Không hiển thị gì cả
        }

        ImageView avatar = new ImageView();
        try {
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                if (avatarUrl.startsWith("http")) {
                    avatar.setImage(new Image(avatarUrl, true));
                } else {
                    File file = new File(avatarUrl);
                    if (file.exists()) {
                        avatar.setImage(new Image(file.toURI().toString()));
                    } else {
                        InputStream defaultAvatar = getClass().getResourceAsStream("/default_avatar.png");
                        if (defaultAvatar != null) {
                            avatar.setImage(new Image(defaultAvatar));
                        }
                    }
                }
            } else {
                InputStream defaultAvatar = getClass().getResourceAsStream("/default_avatar.png");
                if (defaultAvatar != null) {
                    avatar.setImage(new Image(defaultAvatar));
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Không thể tải avatar từ URL: " + avatarUrl);
        }

        avatar.setFitWidth(32);
        avatar.setFitHeight(32);
        avatar.setClip(new Circle(16, 16, 16));

        Label msgLabel = new Label(text);
        msgLabel.setWrapText(true);
        msgLabel.setFont(Font.font("Segoe UI Emoji", 16));
        msgLabel.setPadding(new Insets(10));
        msgLabel.setMaxWidth(300);
        msgLabel.setStyle("-fx-background-color: " + (isSender ? "#6a00f4; -fx-text-fill: white;" : "#e0e0e0;") + " -fx-background-radius: 15;");

        String time = timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        timeLabel.setPadding(new Insets(0, 5, 0, 5));

        VBox bubbleBox = new VBox(msgLabel, timeLabel);
        bubbleBox.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        HBox bubble = new HBox();
        if (isSender) {
            bubble.getChildren().addAll(bubbleBox, avatar);
            bubble.setAlignment(Pos.CENTER_RIGHT);
        } else {
            bubble.getChildren().addAll(avatar, bubbleBox);
            bubble.setAlignment(Pos.CENTER_LEFT);
        }
        bubble.setSpacing(8);
        bubble.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        return bubble;
    }


    private void scrollToBottom() {
        Platform.runLater(() -> {
            ScrollPane scrollPane = (ScrollPane) view.getCenter();
            if (scrollPane != null) {
                scrollPane.setVvalue(1.0);
                scrollPane.requestLayout();
            }
        });
    }

    public BorderPane getView() {
        return view;
    }

    // Hàm kiểm tra tin nhắn đã tồn tại trong UI chưa (dựa trên nội dung)
    private boolean isDuplicateMessage(String content) {
        for (javafx.scene.Node node : messagesBox.getChildren()) {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                for (javafx.scene.Node child : hbox.getChildren()) {
                    if (child instanceof Label) {
                        Label label = (Label) child;
                        if (label.getText() != null && label.getText().contains(content)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

    public void sendFriendRequest(int toUserId) {
        chatClient.sendFriendRequest(Integer.parseInt(userId), toUserId);
    }

    public void acceptFriendRequest(int fromUserId) {
        chatClient.acceptFriendRequest(fromUserId, Integer.parseInt(userId));
    }

    public void rejectFriendRequest(int fromUserId) {
        chatClient.rejectFriendRequest(fromUserId, Integer.parseInt(userId));
    }

    // Lấy avatar động từ DB theo userId
    private String getAvatarUrlByUserId(int userId) {
        String avatar = models.UserDAO.getPictureByUserID(userId);
        if (avatar == null || avatar.isEmpty() || avatar.equals("Default Picture")) {
            try {
                models.UserProfileDAO dao = new models.UserProfileDAO();
                models.UserProfile profile = dao.getUserProfile(userId);
                if (profile != null && profile.getAvatarPicture() != null && !profile.getAvatarPicture().isEmpty()) {
                    return profile.getAvatarPicture();
                }
            } catch (Exception e) {}
            return null;
        }
        return avatar;
    }
}
