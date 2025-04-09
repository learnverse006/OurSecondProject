package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.w3c.dom.NodeList;

public class LoginForm extends HBox {
    public LoginForm() {
        Font robotoRegular = Font.font("Roboto", FontWeight.NORMAL, 16);
        Font robotoBold = Font.font("Roboto", FontWeight.BOLD, 24);

        // Left branding
        VBox leftPane = new VBox(15);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(40));
        leftPane.setStyle("-fx-background-color: linear-gradient(to bottom, #004e92, #000428);");
        leftPane.setPrefWidth(320);

        Text welcomeText = new Text("Welcome back");
        welcomeText.setFill(Color.WHITE);
        welcomeText.setFont(robotoRegular);

        Text appName = new Text("Spacer");
        appName.setFill(Color.WHITE);
        appName.setFont(Font.font("Roboto", FontWeight.BOLD, 36));

        ImageView logoView = new ImageView();
        try {
            Image logo = new Image(getClass().getResource("/resources/download.png").toExternalForm());
            logoView.setImage(logo);
            logoView.setPreserveRatio(true);
            logoView.setFitWidth(120);
        } catch (Exception e) {
            System.out.println("⚠️ Logo not loaded.");
        }

        Label description = new Label("Welcome to the messaging system. Please sign in to continue.");
        description.setTextFill(Color.LIGHTGRAY);
        description.setWrapText(true);
        description.setFont(robotoRegular);
        description.setMaxWidth(240);
        description.setAlignment(Pos.CENTER);

        leftPane.getChildren().addAll(welcomeText, logoView, appName, description);

        // Right form
        VBox rightPane = new VBox(20);
        rightPane.setPadding(new Insets(40));
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setStyle("-fx-background-color: #ffffff;");

        Label formTitle = new Label("Sign in to your account");
        formTitle.setFont(robotoBold);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setFont(robotoRegular);
        usernameField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setFont(robotoRegular);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        Button loginBtn = new Button("Sign In");
        Button registerBtn = new Button("Register");

        loginBtn.setFont(robotoRegular);
        loginBtn.setStyle("-fx-background-color: linear-gradient(to right, #004e92, #000428); -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120;");

        registerBtn.setFont(robotoRegular);
        registerBtn.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-pref-width: 120;");

        buttons.getChildren().addAll(loginBtn, registerBtn);

        VBox formBox = new VBox(12);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setFillWidth(true);
        formBox.getChildren().addAll(
                formTitle,
                new Label("Username"), usernameField,
                new Label("Password"), passwordField,
                buttons);

        for (javafx.scene.Node node : formBox.getChildren()) {
            if (node instanceof Label label) {
                label.setFont(robotoRegular);
            }
        }

        rightPane.getChildren().add(formBox);

        // Combine
        this.getChildren().addAll(leftPane, rightPane);
        this.setPrefSize(900, 600);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
    }
}