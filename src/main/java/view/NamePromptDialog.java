package view;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.UserProfile;
import models.UserProfileDAO;

public class NamePromptDialog {
    public static void display(int userId) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Nhập Tên");

        Label label = new Label("✨ Vui lòng nhập tên của bạn để tiếp tục:");
        label.setFont(Font.font("Arial", 16));
        label.setTextFill(Color.web("#333"));

        TextField nameField = new TextField();
        nameField.setPromptText("Nhập tên ở đây...");
        nameField.setMaxWidth(250);

        // Cảnh báo lỗi (ẩn mặc định)
        ImageView warningIcon = new ImageView(
                new Image("https://cdn-icons-png.flaticon.com/512/564/564619.png", 20, 20, true, true)
        );
        Label warningText = new Label();
        warningText.setFont(Font.font("Arial", 13));
        warningText.setTextFill(Color.web("#D8000C"));
        warningText.setVisible(false);
        warningIcon.setVisible(false);

        HBox warningBox = new HBox(6);
        warningBox.setAlignment(Pos.CENTER_LEFT);
        warningBox.getChildren().addAll(warningIcon, warningText);
        warningBox.setOpacity(0); // Bắt đầu ẩn

        // Hiệu ứng hiện cảnh báo
        FadeTransition fadeWarning = new FadeTransition(Duration.millis(300), warningBox);
        fadeWarning.setFromValue(0);
        fadeWarning.setToValue(1);

        // Nút xác nhận
        Button confirmButton = new Button("✅ Xác nhận");
        confirmButton.setFont(Font.font("Arial", 14));
        confirmButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 8 20;"
        );

        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle(
                "-fx-background-color: #45A049;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 8 20;"
        ));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 8 20;"
        ));

        confirmButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                try {
                    UserProfileDAO dao = new UserProfileDAO();
                    UserProfile profile = dao.getUserProfile(userId);
                    if (profile == null) {
                        profile = new UserProfile();
                        profile.setUserId(userId);
                        profile.setFullName(name);
                    } else {
                        profile.setFullName(name);
                    }
                    dao.updateProfile(profile);
                    window.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    warningText.setText("Đã xảy ra lỗi. Vui lòng thử lại!");
                    warningIcon.setVisible(true);
                    warningText.setVisible(true);
                    fadeWarning.playFromStart();
                }
            } else {
                warningText.setText("Tên không được để trống. Vui lòng nhập tên!");
                warningIcon.setVisible(true);
                warningText.setVisible(true);
                fadeWarning.playFromStart();
            }
        });

//        // Ẩn cảnh báo khi người dùng gõ lại
//        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
//            warningText.setVisible(false);
//            warningIcon.setVisible(false);
//            warningBox.setOpacity(0);
//        });

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 10;");
        layout.getChildren().addAll(label, nameField, warningBox, confirmButton);

        Scene scene = new Scene(layout, 400, 230);
        window.setScene(scene);
        window.showAndWait();
    }
}
