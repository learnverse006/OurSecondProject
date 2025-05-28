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
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import java.io.File;

public class ChatMainView {
    private static ChatClient chatClient;
    
    public static Scene createScene(Stage stage, User user) {
        int userId = user.getUserId();
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #493D9E;");
        sidebar.setPrefWidth(130);

        Label menuLabel = new Label("WHATS APP");
        menuLabel.setFont(Font.font("Roboto", FontWeight.BOLD, 15));
        menuLabel.setStyle("-fx-text-fill: white;");
        
        Button chatBtn = new Button("Chats");
        Button friendsBtn = new Button("Friend");
        Button settingsBtn = new Button("Settings");
        Button profileBtn = new Button("My Profile");
        Button socialBtn = new Button("Social");
        
        for (Button btn : new Button[]{chatBtn, friendsBtn, settingsBtn, profileBtn, socialBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-text-fill: #4B2B82; -fx-font-weight: bold;");
        }

        sidebar.getChildren().addAll(menuLabel, chatBtn, friendsBtn, settingsBtn, profileBtn, socialBtn);
        root.setLeft(sidebar);

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(20));

        HBox header = new HBox(10);
        Label welcomeLabel = new Label("Welcome, " + user.getUsername());
        welcomeLabel.setFont(Font.font(18));

        Button logoutBtn = new Button("Logout");
        Button newChatBtn = new Button("+ New Group Chat");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(welcomeLabel, spacer, newChatBtn, logoutBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        topBox.getChildren().add(header);
        root.setTop(topBox);
        BorderPane mainContent = new BorderPane();
        root.setCenter(mainContent);

        logoutBtn.setOnAction(e -> stage.setScene(AuthView.createScene(stage)));
        
        chatBtn.setOnAction(e -> {
            try {
                List<FriendInfo> friends = FriendshipDAO.getFriendsInfo(user.getUserId());
                // Tạo ListView cho danh sách bạn bè
                ListView<FriendInfo> friendListView = new ListView<>();
                friendListView.getItems().addAll(friends);
                friendListView.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(FriendInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(10);
                            // Avatar
                            ImageView avatar = new ImageView();
                            String path = item.getAvatarUser();
                            if (path != null && !path.isEmpty()) {
                                if (path.startsWith("http")) {
                                    avatar.setImage(new Image(path));
                                } else {
                                    File file = new File(path);
                                    if (file.exists()) {
                                        avatar.setImage(new Image(file.toURI().toString()));
                                    } else {
                                        avatar.setImage(new Image("/default_avatar.png"));
                                    }
                                }
                            } else {
                                avatar.setImage(new Image("/default_avatar.png"));
                            }
                            avatar.setFitWidth(40); avatar.setFitHeight(40);
                            avatar.setClip(new Circle(20, 20, 20));
                            // Tên bạn
                            Label nameLabel = new Label(item.getFullName());
                            nameLabel.setStyle("-fx-font-weight: bold;");
                            // Tin nhắn cuối và thời gian (cần truy vấn DB lấy tin nhắn cuối)
                            String lastMsg = "";
                            String lastTime = "";
                            try {
                                int chatId = ChatDAO.getOrCreateChatId(user.getUserId(), item.getUserId());
                                List<Message> messages = MessageDAO.getMessageByChatID(chatId);
                                if (!messages.isEmpty()) {
                                    Message last = messages.get(messages.size() - 1);
                                    lastMsg = last.getContent();
                                    lastTime = last.getCreateAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                                }
                            } catch (Exception ex) {
                                lastMsg = "";
                                lastTime = "";
                            }
                            Label msgLabel = new Label(lastMsg);
                            msgLabel.setStyle("-fx-text-fill: #888;");
                            Label timeLabel = new Label(lastTime);
                            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa;");
                            VBox info = new VBox(nameLabel, msgLabel);
                            info.setSpacing(2);
                            hBox.getChildren().addAll(avatar, info, timeLabel);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            setGraphic(hBox);
                        }
                    }
                });
                friendListView.setPrefWidth(260);
                friendListView.setPrefHeight(500);
                // Khi click vào bạn bè, mở khung chat tương ứng
                friendListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedFriend) -> {
                    if (selectedFriend != null) {
                        try {
                            int chatId = ChatDAO.getOrCreateChatId(user.getUserId(), selectedFriend.getUserId());
                            ChatPane chatPane = new ChatPane(chatId, user.getUserId(), selectedFriend.getUserId());
                            mainContent.setCenter(chatPane.getView());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                // Đặt ListView vào sidebar
                VBox friendSidebar = new VBox();
                friendSidebar.setPadding(new Insets(10));
                friendSidebar.getChildren().addAll(new Label("Danh sách bạn bè"), friendListView);
                mainContent.setCenter(friendSidebar);
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

        return new Scene(root, 1000, 600);
    }
}
