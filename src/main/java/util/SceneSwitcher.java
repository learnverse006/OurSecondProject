// File: main.java.view/SceneSwitcher.java
package util;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SceneSwitcher {
    private static Stage primaryStage;
    private static final Map<String, Scene> sceneMap = new HashMap<>();

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void register(String key, Scene scene) {
        sceneMap.put(key, scene);
    }

    public static void switchTo(String key) {
        if (primaryStage != null && sceneMap.containsKey(key)) {
            primaryStage.setScene(sceneMap.get(key));
        }
    }

    public static void clear() {
        sceneMap.clear();
    }
}
