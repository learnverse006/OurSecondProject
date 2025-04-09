package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.AuthView;
import view.ChatListView;
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Scene scene = AuthView.createScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CHAT APPLICATION");
        primaryStage.show();
//        Scene scene = ChatListView.createScene(primaryStage, 1); // giả lập userId = 1
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("CHAT LIST TEST");
//        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}