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

public class RegisterForm extends HBox {
    public RegisterForm() {
        Font robotoRegular = Font.font("Roboto", FontWeight.NORMAL, 16);
        Font robotoBold = Font.font("Roboto", FontWeight.BOLD, 24);

        // Left branding
        VBox leftPane = new VBox(15);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(40));
        leftPane.setStyle("-fx-background-color: linear-gradient(to bottom, #004e92, #000428);");
        leftPane.setPrefWidth(320);

        Text welcomeText = new Text("Welcome to");
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

        Label description = new Label("Create a new account to start chatting instantly.");
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
        rightPane.setStyle("-fx-background-color: #ffffff;");

        Label formTitle = new Label("Create your account");
        formTitle.setFont(robotoBold);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setFont(robotoRegular);
        nameField.setMaxWidth(Double.MAX_VALUE);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setFont(robotoRegular);
        emailField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setFont(robotoRegular);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        CheckBox termsCheck = new CheckBox("By signing up, I agree to the Terms & Conditions");
        termsCheck.setFont(Font.font("Roboto", 12));
        termsCheck.setWrapText(true);
        termsCheck.setMaxWidth(300);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        Button registerBtn = new Button("Sign Up");
        Button signInBtn = new Button("Sign In");

        registerBtn.setFont(robotoRegular);
        registerBtn.setStyle("-fx-background-color: linear-gradient(to right, #004e92, #000428); -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 120;");

        signInBtn.setFont(robotoRegular);
        signInBtn.setStyle("-fx-background-color: lightgray; -fx-font-weight: bold; -fx-pref-width: 120;");

        buttons.getChildren().addAll(registerBtn, signInBtn);

        VBox formBox = new VBox(12);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setFillWidth(true);
        formBox.getChildren().addAll(
                formTitle,
                new Label("Name"), nameField,
                new Label("Email Address"), emailField,
                new Label("Password"), passwordField,
                termsCheck,
                buttons);

        for (javafx.scene.Node node : formBox.getChildren()) {
            if (node instanceof Label label) {
                label.setFont(robotoRegular);
            }
        }

        rightPane.getChildren().add(formBox);

        this.getChildren().addAll(leftPane, rightPane);
        this.setPrefSize(900, 600);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
    }
}
