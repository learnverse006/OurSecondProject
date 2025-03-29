package util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;

public class SceneSwitcher {
    public static void switchTo(Stage stage, Supplier<Scene> sceneSupplier) {
        stage.setScene(sceneSupplier.get());
    }
}
