// File: view/ChatMainView.java
package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.User;

public class ChatMainView {
    public static Scene createScene(Stage stage, User user) {
        int userId = user.getUserId();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #4B2B82;");
        sidebar.setPrefWidth(250);

        Label menuLabel = new Label("Menu");
        menuLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
        menuLabel.setStyle("-fx-text-fill: white;");

        Button chatBtn = new Button("Chats");
        Button friendsBtn = new Button("Add Friend");
        Button settingsBtn = new Button("Settings");

        for (Button btn : new Button[]{chatBtn, friendsBtn, settingsBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-text-fill: #4B2B82; -fx-font-weight: bold;");
        }

        sidebar.getChildren().addAll(menuLabel, chatBtn, friendsBtn, settingsBtn);

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(20));

        HBox header = new HBox(10);
        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setFont(Font.font(18));

        Button logoutBtn = new Button("Logout");
        Button newChatBtn = new Button("+ New Chat");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(welcomeLabel, spacer, newChatBtn, logoutBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        ListView<HBox> chatListView = new ListView<>();
        ObservableList<HBox> chatItems = FXCollections.observableArrayList();

        // Placeholder: sample chat items
//        for (int i = 1; i <= 5; i++) {
//            HBox chatItem = new HBox(10);
//            chatItem.setPadding(new Insets(10));
//            chatItem.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
//
//            VBox textContent = new VBox(5);
//            Label chatName = new Label("Chat Group " + i);
//            chatName.setFont(Font.font(16));
//            Label lastMessage = new Label("Last message preview for chat " + i);
//            lastMessage.setStyle("-fx-text-fill: gray;");
//            textContent.getChildren().addAll(chatName, lastMessage);
//
//            chatItem.getChildren().addAll(textContent);
//            chatItems.add(chatItem);
//        }

        chatListView.setItems(chatItems);
        chatListView.setPrefHeight(500);

        contentBox.getChildren().addAll(header, chatListView);

        root.setLeft(sidebar);
        root.setCenter(contentBox);

        return new Scene(root, 1000, 600);
    }
}