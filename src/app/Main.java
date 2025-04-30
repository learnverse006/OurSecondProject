package app;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.AuthView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Scene scene = AuthView.createScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CHAT APPLICATION");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}