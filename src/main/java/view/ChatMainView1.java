// File: main.java.view/ChatMainView.java
package view;

import controller.ProfileEditController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.*;
import javafx.fxml.*;
import javafx.scene.Parent;
import socket.ChatClient;

import java.sql.SQLException;
import java.util.List;


public class ChatMainView1 {
    private static ChatClient chatClient;
    private static boolean darkMode = false;
    
    public static Scene createScene(Stage stage, User user) {
        int userId = user.getUserId();
        
        BorderPane root = new BorderPane();
//        root.setStyle("-fx-background-color: #ffffff;");

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #493D9E;");
        sidebar.setPrefWidth(130);

        Label menuLabel = new Label("WHATS APP");
        menuLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 15));
        menuLabel.setStyle("-fx-text-fill: white;");

        Button forumBtn = new Button("Forum");
        Button chatBtn = new Button("Chats");
        Button friendsBtn = new Button("Friend");
        Button settingsBtn = new Button("Settings");
        Button profileBtn = new Button("My Profile");
        Button socialBtn = new Button("Social");
        
        for (Button btn : new Button[]{forumBtn, chatBtn, friendsBtn, settingsBtn, profileBtn, socialBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-text-fill: #4B2B82; -fx-font-weight: bold;");
        }

        sidebar.getChildren().addAll(menuLabel, forumBtn, chatBtn, friendsBtn, settingsBtn, profileBtn, socialBtn);
        root.setLeft(sidebar);

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));

        HBox header = new HBox(10);
        HBox welcomeHeader = new HBox();
        welcomeHeader.setPadding(new Insets(10));
        welcomeHeader.setStyle("-fx-background-color: #EDEBFF;"); // tím sáng cố định

        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 16));
        welcomeLabel.setStyle("-fx-text-fill: #532db2;"); // chữ tím đậm cố định

        welcomeHeader.getChildren().add(welcomeLabel);

        Button logoutBtn = new Button("Logout");
        Button newChatBtn = new Button("+ New Group Chat");
        Region spacer = new Region();
        Button toggleThemeButton = new Button("🌙");

        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(welcomeHeader, spacer, newChatBtn, logoutBtn, toggleThemeButton);
        header.setAlignment(Pos.CENTER_LEFT);

        topBox.getChildren().add(header);
        root.setTop(topBox);
        BorderPane mainContent = new BorderPane();
        root.setCenter(mainContent);

        logoutBtn.setOnAction(e -> stage.setScene(AuthView.createScene(stage)));
        
        chatBtn.setOnAction(e -> {
            try {
                List<FriendInfo> friends = FriendshipDAO.getFriendsInfo(user.getUserId());
                HBox chatContainer = new HBox();
                chatContainer.setPadding(new Insets(10));
                chatContainer.setSpacing(10);

                BorderPane chatHolder = new BorderPane();
                chatHolder.setPrefWidth(700);

                ChatListPane chatListPane = new ChatListPane(friends, friendInfo -> {
                    try {
                        int chatId = ChatDAO.getOrCreateChatId(user.getUserId(), friendInfo.getUserId());
                        ChatPane chatPane = new ChatPane(chatId, user.getUserId(), friendInfo.getUserId());
                        chatHolder.setCenter(chatPane.getView());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                chatContainer.getChildren().addAll(chatListPane.getView(), chatHolder);
                mainContent.setCenter(chatContainer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        friendsBtn.setOnAction(e -> {
            try {
                if (chatClient == null) {
                    chatClient = new ChatClient();
                    chatClient.connect("localhost", 1234, user.getUserId());
                    chatClient.send("LOGIN:" + user.getUserId());
                    chatClient.listen(message -> {
                        // Handle incoming messages
                    });
                }
                FriendView friendView = new FriendView(user.getUserId(), chatClient);
                mainContent.setCenter(friendView.getView());
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Cannot connect to chat server");
                alert.setContentText("Please make sure the chat server is running on localhost:5000");
                alert.showAndWait();
            }
        });

        settingsBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(ChatMainView.class.getResource("/fxml/ProfileEditPane.fxml"));
                Parent profileEditPane = loader.load();
                ProfileEditController controller = loader.getController();
                controller.setUserId(user.getUserId());
                mainContent.setCenter(profileEditPane);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //forum
        forumBtn.setOnAction(e -> {
            ForumFeedPane forumPane = null;
            try {
                forumPane = new ForumFeedPane(user.getUserId());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            mainContent.setCenter(forumPane);
        });

        Scene scene = new Scene(root, 1000, 600);
        // light and dark
        scene.getStylesheets().add(ChatMainView.class.getResource("/css/light-theme.css").toExternalForm());

        toggleThemeButton.setOnAction(e -> {
            scene.getStylesheets().clear();
            if (darkMode) {
                scene.getStylesheets().add(ChatMainView.class.getResource("/css/light-theme.css").toExternalForm());
                toggleThemeButton.setText("🌙");
            } else {
                scene.getStylesheets().add(ChatMainView.class.getResource("/css/dark-theme.css").toExternalForm());
                toggleThemeButton.setText("☀️");
            }
            darkMode = !darkMode;
        });
        return scene;
    }
}
