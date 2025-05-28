package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.FriendInfo;
import java.util.List;
import java.util.function.Consumer;

public class ChatListPane {
    private final BorderPane view;

    public ChatListPane(List<FriendInfo> friends, Consumer<FriendInfo> onChatSelected) {
        view = new BorderPane();
        view.setPadding(new Insets(10));
        view.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1;");

        VBox container = new VBox(10);

        Label title = new Label("Danh sách bạn bè");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<FriendInfo> chatList = new ListView<>();
        ObservableList<FriendInfo> chats = FXCollections.observableArrayList(friends);
        chatList.setItems(chats);

        chatList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(FriendInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getFullName());
                    setStyle("-fx-padding: 10px; -fx-font-size: 14px;");
                }
            }
        });

        chatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                onChatSelected.accept(newVal);
            }
        });

        container.getChildren().addAll(title, chatList);
        view.setCenter(container);
    }

    public BorderPane getView() {
        return view;
    }
}
