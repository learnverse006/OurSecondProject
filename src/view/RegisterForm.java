// File: view/RegisterForm.java
package view;

import controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterForm {
    public static Scene createScene(Stage stage) {
        Font font = Font.font("Roboto", FontWeight.NORMAL, 16);
        Font titleFont = Font.font("Roboto", FontWeight.BOLD, 24);

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(30));
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-background-radius: 20;");
        formBox.setEffect(new DropShadow(100, Color.rgb(0, 0, 0, 0.3)));

        Label title = new Label("Register");
        title.setFont(titleFont);
        title.setTextFill(Color.WHITE);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setFont(font);
        nameField.setStyle("-fx-background-radius: 30; -fx-padding: 10; -fx-background-color: white;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setFont(font);
        emailField.setStyle("-fx-background-radius: 30; -fx-padding: 10; -fx-background-color: white;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(font);
        passwordField.setStyle("-fx-background-radius: 30; -fx-padding: 10; -fx-background-color: white;");

        Button registerBtn = new Button("Register");
        registerBtn.setFont(font);
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle("-fx-background-radius: 30; -fx-background-color: white; -fx-text-fill: #6a00f4; -fx-font-weight: bold; -fx-padding: 10;");

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setTextFill(Color.WHITE);

        formBox.getChildren().addAll(title, nameField, emailField, passwordField, registerBtn, loginLink);
        formBox.setMaxWidth(350);
        formBox.setMaxHeight(400);

        Image bgImage = new Image(RegisterForm.class.getResource("/resources/BGLogin.jpg").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(false);
        bgView.setSmooth(true);
        bgView.setCache(true);

        StackPane root = new StackPane(bgView, formBox);
        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());

        bgView.fitWidthProperty().bind(scene.widthProperty());
        bgView.fitHeightProperty().bind(scene.heightProperty());

        formBox.maxWidthProperty().bind(scene.widthProperty().multiply(0.35));
        formBox.maxHeightProperty().bind(scene.heightProperty().multiply(0.6));

        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
                return;
            }

            boolean success = AuthController.register(name, email, password);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registration successful. Please log in.");
                stage.setScene(AuthView.createScene(stage));
            } else {
                showAlert(Alert.AlertType.ERROR, "Username already exists.");
            }
        });

        loginLink.setOnAction(e -> stage.setScene(AuthView.createScene(stage)));

        return scene;
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Register");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
