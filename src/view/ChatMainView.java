// File: view/ChatMainView.java
package view;

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
        sidebar.setPrefWidth(130);

        Label menuLabel = new Label("WHATS APP");
        menuLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 15));
        menuLabel.setStyle("-fx-text-fill: white;");

        Button chatBtn = new Button("Chats");
        Button friendsBtn = new Button("Add Friend");
        Button settingsBtn = new Button("Settings");

        for (Button btn : new Button[]{chatBtn, friendsBtn, settingsBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-text-fill: #4B2B82; -fx-font-weight: bold;");
        }

        sidebar.getChildren().addAll(menuLabel, chatBtn, friendsBtn, settingsBtn);
        root.setLeft(sidebar);

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));

        HBox header = new HBox(10);
        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setFont(Font.font(18));

        Button logoutBtn = new Button("Logout");
        Button newChatBtn = new Button("+ New Chat");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(welcomeLabel, spacer, newChatBtn, logoutBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        topBox.getChildren().add(header);
        root.setTop(topBox);

        // Main content with chat list on the left and chat view on the right
        HBox mainContent = new HBox();
        mainContent.setPadding(new Insets(10));
        mainContent.setSpacing(10);

        ChatListPane chatListPane = new ChatListPane(chatId -> {
            ChatPane chatPane = new ChatPane(chatId);
            if (mainContent.getChildren().size() > 1) {
                mainContent.getChildren().set(1, chatPane.getView());
            } else {
                mainContent.getChildren().add(chatPane.getView());
            }
        });

        mainContent.getChildren().add(chatListPane.getView());
        root.setCenter(mainContent);

        return new Scene(root, 1000, 600);
    }
}
