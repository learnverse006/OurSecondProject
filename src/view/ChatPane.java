// File: view/ChatPane.java
package view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import socket.ChatClient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatPane {
    private final BorderPane view;
    private final VBox messagesBox = new VBox(10);
    private final ChatClient chatClient = new ChatClient();
    private final String userId = "User" + System.currentTimeMillis();

    public ChatPane(int chatId) {
        view = new BorderPane();
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
        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: #eeeeee;");

        TextField messageField = new TextField();
        messageField.setPromptText("Nhập tin nhắn...");
        messageField.setPrefWidth(600);

        Button sendButton = new Button("Gửi");
        inputBox.getChildren().addAll(messageField, sendButton);
        view.setBottom(inputBox);

        // Socket setup
        try {
            chatClient.connect("26.66.135.84", 1234);
            chatClient.send(userId); // gửi tên user đầu tiên để server nhận biết

            chatClient.listen(message -> Platform.runLater(() -> {
                String sender = extractSender(message);
                String content = extractContent(message);
                boolean isSender = sender.equals(userId);
                if (!isSender) {
                    messagesBox.getChildren().add(createMessageBubble(content, false));
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gửi tin nhắn
        sendButton.setOnAction(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty()) {
                chatClient.send(text);
                messagesBox.getChildren().add(createMessageBubble(text, true));
                messageField.clear();
            }
        });
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
