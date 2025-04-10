package view;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ChatPane {
    private final BorderPane view;

    public ChatPane(int chatId) {
        view = new BorderPane();
        view.setStyle("-fx-background-color: #ffffff;");

        // Header
        Label headerLabel = new Label("Group 1");
        headerLabel.setFont(Font.font(18));
        headerLabel.setPadding(new Insets(10));
        BorderPane.setAlignment(headerLabel, Pos.CENTER_LEFT);
        view.setTop(headerLabel);

        // Message area (center)
        VBox messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(10));
        messagesBox.setStyle("-fx-background-color: #f9f9f9;");

        ScrollPane scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        view.setCenter(scrollPane);

        // Example mock messages


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

        // Gửi tin nhắn
        sendButton.setOnAction(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty()) {
                messagesBox.getChildren().add(createMessageBubble(text, true));
                messageField.clear();
            }
        });
    }

    private HBox createMessageBubble(String text, boolean isSender) {
        Label msgLabel = new Label(text);
        msgLabel.setWrapText(true);
        msgLabel.setPadding(new Insets(10));
        msgLabel.setMaxWidth(300);
        msgLabel.setStyle("-fx-background-color: " + (isSender ? "#6a00f4; -fx-text-fill: white;" : "#e0e0e0;") + " -fx-background-radius: 10;");

        HBox bubble = new HBox(msgLabel);
        bubble.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.setNodeOrientation(isSender ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
        return bubble;
    }

    public BorderPane getView() {
        return view;
    }
}
