
package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class ChatListPane {
    private final BorderPane view;

    public ChatListPane(Consumer<Integer> onChatSelected) {
        view = new BorderPane();
        view.setPadding(new Insets(10));
        view.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1;");

        VBox container = new VBox(10);

        Label title = new Label("Recent Chats");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<String> chatList = new ListView<>();
        ObservableList<String> chats = FXCollections.observableArrayList("Group 1");
        chatList.setItems(chats);

        chatList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-padding: 10px; -fx-font-size: 14px;");
                }
            }
        });

        chatList.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() == 0) {
                onChatSelected.accept(1);
            }
        });


        container.getChildren().addAll(title, chatList);
        view.setCenter(container);
    }

    public BorderPane getView() {
        return view;
    }
}
