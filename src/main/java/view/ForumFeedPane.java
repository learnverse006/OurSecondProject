package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import models.*;

import java.sql.SQLException;
import java.util.List;

public class ForumFeedPane extends BorderPane {
    private VBox postList;
    private UserProfileDAO userProfileDAO = new UserProfileDAO();
    public ForumFeedPane(int userId) throws SQLException {
        TextArea postInput = new TextArea();
        postInput.setPromptText("Bạn đang nghĩ gì");
        postInput.setWrapText(true);
        postInput.setPrefRowCount(3);

        Button postButton = new Button("Đăng");
        postButton.setOnAction(e->{
            String content = postInput.getText();
            if(!content.isEmpty()){
                ForumPost post = new ForumPost(userId, content);
                ForumPostDAO.insertPost(post);
                postInput.clear();
                try {
                    loadPosts();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        HBox inputBox = new HBox(10, postInput,  postButton);
        inputBox.setPadding(new Insets(10));
        inputBox.setPrefHeight(120);
        postButton.setMinHeight(60);
        inputBox.setStyle("-fx-background-color: #f0f0ff; -fx-background-radius: 10;");

        //list post
        postList = new VBox(10);
        postList.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(postList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));
        scrollPane.setStyle("-fx-background-color: transparent;");

        this.setTop(inputBox);
        this.setCenter(scrollPane);

        loadPosts();
    }

    private void loadPosts() throws SQLException {
        postList.getChildren().clear();
        List<ForumPost> forumPosts = ForumPostDAO.getAllPosts();
        for (ForumPost post : forumPosts) {
            VBox box = new VBox(5);
            box.setPadding(new Insets(10));
            box.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");

            UserProfile user = userProfileDAO.getUserProfile(post.getUserId());
            String displayName = (user != null) ? user.getFullName() : "Unknown User";
            Label userLabel = new Label("👤 " + displayName);

            userLabel.setFont(Font.font("Arial", 13));
            userLabel.setStyle("-fx-text-fill: #333;");

            Label contentLabel = new Label(post.getContent());
            contentLabel.setWrapText(true);
            contentLabel.setFont(Font.font("Arial", 14));

            Label timeLabel = new Label("🕒 " + post.getCreatedAt().toString());
            timeLabel.setFont(Font.font("Arial", 11));
            timeLabel.setStyle("-fx-text-fill: #666;");

            box.getChildren().addAll(userLabel, contentLabel, timeLabel);
            postList.getChildren().add(box);
        }
    }
}
