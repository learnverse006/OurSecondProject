// File: view/AuthView.java
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
import models.User;

public class AuthView {
    public static Scene createScene(Stage primaryStage) {
        Font font = Font.font("Roboto", FontWeight.NORMAL, 16);
        Font titleFont = Font.font("Roboto", FontWeight.BOLD, 24);

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(30));
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-background-radius: 20;");
        formBox.setEffect(new DropShadow(100, Color.rgb(0, 0, 0, 0.3)));

        Label title = new Label("Login");
        title.setFont(titleFont);
        title.setTextFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setFont(font);
        usernameField.setMaxWidth(Double.MAX_VALUE);
        usernameField.setStyle("-fx-background-radius: 30; -fx-padding: 10; -fx-background-color: white;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(font);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-background-radius: 30; -fx-padding: 10; -fx-background-color: white;");

        HBox options = new HBox(10);
        CheckBox rememberMe = new CheckBox("Remember me");
        Hyperlink forgotPassword = new Hyperlink("Forgot password?");
        rememberMe.setTextFill(Color.WHITE);
        forgotPassword.setTextFill(Color.WHITE);
        options.getChildren().addAll(rememberMe, forgotPassword);
        options.setAlignment(Pos.CENTER);

        Button loginBtn = new Button("Login");
        loginBtn.setFont(font);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-radius: 30; -fx-background-color: white; -fx-text-fill: #6a00f4; -fx-font-weight: bold; -fx-padding: 10;");

        Hyperlink registerLink = new Hyperlink("Don't have an account? Register");
        registerLink.setTextFill(Color.WHITE);

        formBox.getChildren().addAll(title, usernameField, passwordField, options, loginBtn, registerLink);
        formBox.setMaxWidth(350);
        formBox.setMaxHeight(300);

        Image bgImage = new Image(AuthView.class.getResource("/resources/BGLogin.jpg").toExternalForm());
        ImageView bgView = new ImageView(bgImage);
        bgView.setPreserveRatio(false);
        bgView.setSmooth(true);
        bgView.setCache(true);

        StackPane root = new StackPane(bgView, formBox);
        Scene scene = new Scene(root, 800, 500);

        bgView.fitWidthProperty().bind(scene.widthProperty());
        bgView.fitHeightProperty().bind(scene.heightProperty());

        formBox.maxWidthProperty().bind(scene.widthProperty().multiply(0.35));
        formBox.maxHeightProperty().bind(scene.heightProperty().multiply(0.5));

        AuthController.getRememberedUser().ifPresent(data -> {
            usernameField.setText(data[0]);
            passwordField.setText(data[1]);
            rememberMe.setSelected(true);
        });

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Please enter both username and password.");
                return;
            }
            User user = AuthController.LoginUser(username, password);
            boolean success = AuthController.login(username, password);
            if (success) {
                if (rememberMe.isSelected()) AuthController.saveRememberedUser(username, password);
                else AuthController.clearRememberedUser();

                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                Scene mainScene = ChatMainView.createScene(primaryStage, user);
                primaryStage.setScene(mainScene);

                // TODO: load main view
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
            }
        });

        registerLink.setOnAction(e -> {
            try {
                Scene registerScene = RegisterForm.createScene(primaryStage);
                primaryStage.setScene(registerScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        forgotPassword.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Password recovery not implemented yet."));

        return scene;
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Authentication");
        alert.setContentText(message);
        alert.showAndWait();
    }
}