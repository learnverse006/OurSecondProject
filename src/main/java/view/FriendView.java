package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import models.FriendInfo;
import models.FriendshipDAO;
import socket.ChatClient;
import java.util.List;
import java.util.function.Consumer;

public class FriendView {
    private final BorderPane view;
    private final VBox friendsList;
    private final VBox pendingRequestsList;
    private final VBox searchResultsList;
    private final ChatClient chatClient;
    private final int currentUserId;

    public FriendView(int currentUserId, ChatClient chatClient) {
        this.currentUserId = currentUserId;
        this.chatClient = chatClient;
        
        view = new BorderPane();
        view.setStyle("-fx-background-color: #ffffff;");

        // Header
        Label headerLabel = new Label("Bạn bè");
        headerLabel.setFont(Font.font(18));
        headerLabel.setPadding(new Insets(10));
        BorderPane.setAlignment(headerLabel, Pos.CENTER_LEFT);
        view.setTop(headerLabel);

        // Search box
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm người dùng...");
        searchField.setPrefWidth(300);
        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #4D55CC; -fx-text-fill: white;");
        searchBox.getChildren().addAll(searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Search results
        VBox searchResultsBox = new VBox(10);
        searchResultsBox.setPadding(new Insets(10));
        Label searchLabel = new Label("Kết quả tìm kiếm");
        searchLabel.setFont(Font.font(14));
        searchResultsList = new VBox(5);
        ScrollPane searchScroll = new ScrollPane(searchResultsList);
        searchScroll.setFitToWidth(true);
        searchResultsBox.getChildren().addAll(searchLabel, searchScroll);
        searchResultsBox.setVisible(false);

        // Main content
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);

        // Friends list
        VBox friendsBox = new VBox(10);
        friendsBox.setPadding(new Insets(10));
        Label friendsLabel = new Label("Danh sách bạn bè");
        friendsLabel.setFont(Font.font(14));
        friendsList = new VBox(5);
        ScrollPane friendsScroll = new ScrollPane(friendsList);
        friendsScroll.setFitToWidth(true);
        friendsBox.getChildren().addAll(friendsLabel, friendsScroll);

        // Pending requests
        VBox requestsBox = new VBox(10);
        requestsBox.setPadding(new Insets(10));
        Label requestsLabel = new Label("Lời mời kết bạn");
        requestsLabel.setFont(Font.font(14));
        pendingRequestsList = new VBox(5);
        ScrollPane requestsScroll = new ScrollPane(pendingRequestsList);
        requestsScroll.setFitToWidth(true);
        requestsBox.getChildren().addAll(requestsLabel, requestsScroll);

        splitPane.getItems().addAll(friendsBox, requestsBox);

        // Layout
        VBox mainContent = new VBox(10);
        mainContent.getChildren().addAll(searchBox, searchResultsBox, splitPane);
        view.setCenter(mainContent);

        // Search functionality
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<FriendInfo> results = FriendshipDAO.searchUsers(searchTerm, currentUserId);
                searchResultsList.getChildren().clear();
                
                if (results.isEmpty()) {
                    Label noResults = new Label("Không tìm thấy kết quả");
                    searchResultsList.getChildren().add(noResults);
                } else {
                    for (FriendInfo user : results) {
                        HBox resultItem = createSearchResultItem(user);
                        searchResultsList.getChildren().add(resultItem);
                    }
                }
                searchResultsBox.setVisible(true);
            }
        });

        // Load initial data
        loadFriendsList();
        loadPendingRequests();

        // Setup friend request notifications
        setupFriendRequestHandlers();
    }

    private void loadFriendsList() {
        try {
            List<FriendInfo> friends = FriendshipDAO.getFriendsInfo(currentUserId);
            friendsList.getChildren().clear();
            
            for (FriendInfo friend : friends) {
                HBox friendItem = createFriendItem(friend);
                friendsList.getChildren().add(friendItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPendingRequests() {
        try {
            List<FriendInfo> requests = FriendshipDAO.getPendingRequestsInfo(currentUserId);
            pendingRequestsList.getChildren().clear();
            
            for (FriendInfo request : requests) {
                HBox requestItem = createRequestItem(request);
                pendingRequestsList.getChildren().add(requestItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageView createAvatarImageView(String avatarPath) {
        ImageView avatar = new ImageView();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                String url;
                if (avatarPath.startsWith("file:")) {
                    url = avatarPath;
                } else if (avatarPath.matches("^[A-Za-z]:.*") || avatarPath.startsWith("/")) {
                    // Absolute path (Windows or Unix)
                    url = "file:/" + avatarPath.replace("\\", "/");
                } else {
                    // Relative path or just filename
                    url = "file:src/main/resources/images/" + avatarPath;
                }
                avatar.setImage(new Image(url));
            } catch (Exception e) {
                avatar.setImage(new Image("file:src/main/resources/images/default-avatar.png"));
            }
        } else {
            avatar.setImage(new Image("file:src/main/resources/images/default-avatar.png"));
        }
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);
        return avatar;
    }

    private HBox createFriendItem(FriendInfo friend) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(5));
        item.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        // Avatar
        ImageView avatar = createAvatarImageView(friend.getAvatarUser());

        // Name
        Label nameLabel = new Label(friend.getFullName());
        nameLabel.setFont(Font.font(14));

        // Chat button
        Button chatButton = new Button("Nhắn tin");
        chatButton.setStyle("-fx-background-color: #4D55CC; -fx-text-fill: white;");

        // Xem hồ sơ
        Button viewProfileBtn = new Button("Xem hồ sơ");
        viewProfileBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewProfileBtn.setOnAction(e -> showUserProfileDialog(friend.getUserId()));

        item.getChildren().addAll(avatar, nameLabel, chatButton, viewProfileBtn);
        item.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        return item;
    }

    private HBox createRequestItem(FriendInfo request) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(5));
        item.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        // Avatar
        ImageView avatar = createAvatarImageView(request.getAvatarUser());

        // Name
        Label nameLabel = new Label(request.getFullName());
        nameLabel.setFont(Font.font(14));

        // Accept button
        Button acceptButton = new Button("Chấp nhận");
        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        acceptButton.setOnAction(e -> {
            chatClient.acceptFriendRequest(request.getUserId(), currentUserId);
            loadFriendsList();
            loadPendingRequests();
        });

        // Reject button
        Button rejectButton = new Button("Từ chối");
        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        rejectButton.setOnAction(e -> {
            chatClient.rejectFriendRequest(request.getUserId(), currentUserId);
            loadPendingRequests();
        });

        // Xem hồ sơ
        Button viewProfileBtn = new Button("Xem hồ sơ");
        viewProfileBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewProfileBtn.setOnAction(e -> showUserProfileDialog(request.getUserId()));

        item.getChildren().addAll(avatar, nameLabel, acceptButton, rejectButton, viewProfileBtn);
        item.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        return item;
    }

    private HBox createSearchResultItem(FriendInfo user) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(5));
        item.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        // Avatar
        ImageView avatar = createAvatarImageView(user.getAvatarUser());

        // Name
        Label nameLabel = new Label(user.getFullName());
        nameLabel.setFont(Font.font(14));

        // Xem hồ sơ button
        Button viewProfileBtn = new Button("Xem hồ sơ");
        viewProfileBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewProfileBtn.setOnAction(e -> showUserProfileDialog(user.getUserId()));

        // Trạng thái kết bạn
        String status = null;
        try {
            status = FriendshipDAO.getFriendshipStatus(currentUserId, user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ("accepted".equals(status)) {
            Label friendLabel = new Label("Bạn bè");
            friendLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 4 8 4 8; -fx-background-radius: 5;");
            item.getChildren().addAll(avatar, nameLabel, friendLabel, viewProfileBtn);
        } else if ("pending".equals(status)) {
            Label pendingLabel = new Label("Đã gửi lời mời");
            pendingLabel.setStyle("-fx-background-color: #bdbdbd; -fx-text-fill: #333; -fx-padding: 4 8 4 8; -fx-background-radius: 5;");
            item.getChildren().addAll(avatar, nameLabel, pendingLabel, viewProfileBtn);
        } else {
            // Add friend button
            Button addButton = new Button("Kết bạn");
            addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            addButton.setOnAction(e -> {
                chatClient.sendFriendRequest(currentUserId, user.getUserId());
                addButton.setDisable(true);
                addButton.setText("Đã gửi lời mời");
            });
            item.getChildren().addAll(avatar, nameLabel, addButton, viewProfileBtn);
        }

        item.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        return item;
    }

    private void showUserProfileDialog(int userId) {
        try {
            models.UserProfileDAO dao = new models.UserProfileDAO();
            models.UserProfile profile = dao.getUserProfile(userId);

            if (profile == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Không tìm thấy thông tin người dùng.");
                alert.showAndWait();
                return;
            }

            Stage dialog = new Stage();
            dialog.setTitle("Thông tin tài khoản");
            dialog.setResizable(false);

            VBox root = new VBox(10);
            root.setPadding(new Insets(0, 0, 20, 0));
            root.setStyle("-fx-background-color: #fff; -fx-border-radius: 10; -fx-background-radius: 10;");

            // Cover
            ImageView cover = new ImageView();
            if (profile.getCoverPicture() != null && !profile.getCoverPicture().isEmpty()) {
                try {
                    String url = profile.getCoverPicture().startsWith("file:") ? profile.getCoverPicture() : "file:/" + profile.getCoverPicture().replace("\\", "/");
                    cover.setImage(new Image(url));
                } catch (Exception e) {}
            }
            cover.setFitHeight(120);
            cover.setFitWidth(420);
            cover.setPreserveRatio(false);

            // Avatar
            ImageView avatar = new ImageView();
            if (profile.getAvatarPicture() != null && !profile.getAvatarPicture().isEmpty()) {
                try {
                    String url = profile.getAvatarPicture().startsWith("file:") ? profile.getAvatarPicture() : "file:/" + profile.getAvatarPicture().replace("\\", "/");
                    avatar.setImage(new Image(url));
                } catch (Exception e) {}
            }
            avatar.setFitWidth(80);
            avatar.setFitHeight(80);
            avatar.setStyle("-fx-background-radius: 40; -fx-border-radius: 40; -fx-effect: dropshadow(gaussian, #888, 8, 0.5, 0, 0);");
            StackPane avatarPane = new StackPane(avatar);
            avatarPane.setPadding(new Insets(-40, 0, 0, 20));
            avatarPane.setTranslateY(-40);

            // Tên
            Label nameLabel = new Label(profile.getFullName());
            nameLabel.setFont(Font.font(18));
            nameLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 20;");

            // Nút
            HBox buttonBox = new HBox(10);
            buttonBox.setPadding(new Insets(0, 0, 0, 20));
            Button callBtn = new Button("Gọi điện");
            Button messageBtn = new Button("Nhắn tin");
            callBtn.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #333; -fx-font-weight: bold;");
            messageBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
            buttonBox.getChildren().addAll(callBtn, messageBtn);

            // Thông tin cá nhân
            VBox infoBox = new VBox(5);
            infoBox.setPadding(new Insets(10, 0, 0, 20));
            infoBox.getChildren().addAll(
                new Label("Nghề nghiệp: " + (profile.getJobTitle() != null ? profile.getJobTitle() : "")),
                new Label("Địa chỉ: " + (profile.getLocation() != null ? profile.getLocation() : "")),
                new Label("Facebook: " + (profile.getFacebookLink() != null ? profile.getFacebookLink() : "")),
                new Label("Kinh nghiệm: " + (profile.getExp() != null ? profile.getExp() : "")),
                new Label("Bio: " + (profile.getBio() != null ? profile.getBio() : ""))
            );

            root.getChildren().addAll(cover, avatarPane, nameLabel, buttonBox, new Separator(), infoBox);

            Scene scene = new Scene(root, 420, 400);
            dialog.setScene(scene);
            dialog.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi lấy thông tin hồ sơ: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void setupFriendRequestHandlers() {
        // Handle new friend request
        chatClient.setOnFriendRequestReceived(fromUserId -> {
            loadPendingRequests();
        });

        // Handle friend request accepted
        chatClient.setOnFriendRequestAccepted(fromUserId -> {
            loadFriendsList();
        });

        // Handle friend request rejected
        chatClient.setOnFriendRequestRejected(fromUserId -> {
            // No need to update UI as the request is already removed
        });
    }

    public BorderPane getView() {
        return view;
    }
}
