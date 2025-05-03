// File: view/ChatPane.java
package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import socket.ChatClient;
import models.Message;
import models.MessageDAO;
import models.Message.MessageType;

import java.time.LocalTime;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

public class ChatPane {
    private final BorderPane view;
    private final VBox messagesBox = new VBox(10);
    private final ChatClient chatClient = new ChatClient();
    private final String userId;
    // chat pane:
    public ChatPane(int chatId, int currID) {
        view = new BorderPane();
        this.userId = String.valueOf(currID);
        view.setStyle("-fx-background-color: #ffffff;");

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

//        view.setBottom(toolBar);

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
        try {
            List<Message> oldMessages = MessageDAO.getMessageByChatID(chatId);
            for (Message msg : oldMessages) {
                boolean isSender = msg.getSenderID() == Integer.parseInt(userId);

                if (msg.getMst() == Message.MessageType.IMAGE && msg.getContent().startsWith("[IMG]")) {
                    ImageView img = util.FileTransferHandler.receiveImage(msg.getContent());
                    if (img != null) {
                        HBox bubble = util.FileTransferHandler.buildImageBubble(img, isSender);
                        messagesBox.getChildren().add(bubble);
                    }
                } else if (msg.getMst() == Message.MessageType.FILE && msg.getContent().startsWith("[FILE]:")) {
                    String[] parts = util.FileTransfer.parseFileMessage(msg.getContent());
                    if (parts.length == 3) {
                        byte[] fileData = util.FileTransfer.decodeBase64File(parts[2]);
                        HBox fileBubble = util.FileTransfer.buildDownloadableFile(parts[1], fileData, view.getScene().getWindow(), isSender);
                        messagesBox.getChildren().add(fileBubble);
                    }
                } else {
                    messagesBox.getChildren().add(createMessageBubble(msg.getContent(), isSender));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Socket setup
        try {
            chatClient.connect("26.66.135.84", 1234);
            chatClient.send(userId); // gửi tên user đầu tiên để server nhận biết

            chatClient.listen(message -> Platform.runLater(() -> {

                String sender = extractSender(message);
                String content = extractContent(message);
                boolean isSender = sender.equals(userId);

                if (content.startsWith("[FILE]:")) {
                    String[] parts = util.FileTransfer.parseFileMessage(content);
                    if (parts.length == 3) {
                        String fileName = parts[1];
                        byte[] fileData = util.FileTransfer.decodeBase64File(parts[2]);

//                        boolean isSender = sender.equals(userId);
                        HBox download = util.FileTransfer.buildDownloadableFile(fileName, fileData, view.getScene().getWindow(), isSender);
                        messagesBox.getChildren().add(download);
                    }
                    return;

                }

                if (content.startsWith("[IMG]")) {
                    ImageView imageView = util.FileTransferHandler.receiveImage(content);  // <-- xử lý Base64 sang ảnh
                    if (imageView != null) {
                        HBox bubble = util.FileTransferHandler.buildImageBubble(imageView, isSender);
                        messagesBox.getChildren().add(bubble);
                    }
                    return; // Dừng lại, không xử lý như text nữa
                }

                // Nếu không phải ảnh thì xử lý như text
                messagesBox.getChildren().add(createMessageBubble(content, isSender));
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
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file để gửi");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Tất cả file", "*.*"),
                    new FileChooser.ExtensionFilter("Tài liệu", "*.pdf", "*.docx", "*.xlsx", "*.txt")
            );

            File chosenFile = fileChooser.showOpenDialog(view.getScene().getWindow());
            if (chosenFile != null) {
                try {
                    String msg = util.FileTransfer.buildFileMessage(chosenFile); // gửi dạng [FILE]:...
                    chatClient.send(msg);

                    // Hiển thị xác nhận đã gửi
                    Label label = new Label("📄 Đã gửi file: " + chosenFile.getName());
                    HBox box = new HBox(label);
                    box.setAlignment(Pos.CENTER_RIGHT);
                    messagesBox.getChildren().add(box);

                    // lưu vào db
                    Message message = new Message();
                    message.setChatID(chatId);
                    message.setSenderID(currID);
                    message.setReceiverID(0);
                    message.setContent(msg);
                    message.setMessageType(MessageType.FILE);
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
//            try {
//                String crypt = AESUtil.encrypt(text, "1234567890123456");
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }

            if (!text.isEmpty()) {
                chatClient.send(text);
                messagesBox.getChildren().add(createMessageBubble(text, true));
                messageField.clear();

                try {
                    Message message = new Message();
                    message.setChatID(chatId);
                    message.setSenderID(currID);
                    message.setReceiverID(0);
//                    message.setContent(AESUtil.encrypt(text, "1234123412341234"));
                    message.setContent(text);
                    message.setMessageType(MessageType.TEXT);
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

                    // lưu vào db
                    Message message = new Message();
                    message.setChatID(chatId);
                    message.setSenderID(currID);
                    message.setReceiverID(0);
                    message.setContent(messageToSend);
                    message.setMessageType(MessageType.IMAGE);
                    message.setCreateAt(java.time.LocalDateTime.now());
                    MessageDAO.saveMessage(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


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

    private HBox createMessageBubble(String text, boolean isSender) {
        VBox bubbleBox = new VBox(5);
        bubbleBox.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        Label msgLabel = new Label(text);
        msgLabel.setWrapText(true);
        msgLabel.setFont(Font.font("Segoe UI Emoji", 16));
        msgLabel.setPadding(new Insets(10));
        msgLabel.setMaxWidth(300);
        msgLabel.setStyle("-fx-background-color: " + (isSender ? "#6a00f4; -fx-text-fill: white;" : "#e0e0e0;") + " -fx-background-radius: 10;");

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        timeLabel.setPadding(new Insets(0, 5, 0, 5));

        bubbleBox.getChildren().addAll(msgLabel, timeLabel);

        HBox bubble = new HBox(bubbleBox);
        bubble.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        return bubble;
    }

    public BorderPane getView() {
        return view;
    }
}
