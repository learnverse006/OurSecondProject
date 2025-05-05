package controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
public class ProfileEditController {
    @FXML
    private ImageView avatarImageView;
    @FXML
    private ImageView coverImageView;
    @FXML
    private Button chooseAvatarBtn;
    @FXML
    public void initialize() {

//        Circle clip = new Circle(120, 120, 120);
//        avatarImageView.setClip(clip);
//        chooseAvatarBtn.setOnAction(e -> chooseAvatar());
//        avatarImageView.setPreserveRatio(true);
//        avatarImageView.setFitWidth(120);
//        avatarImageView.setFitHeight(120);
//        avatarImageView.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
//            double radius = Math.min(newVal.getWidth(), newVal.getHeight()) / 2;
//            Circle clip = new Circle(radius, radius, radius);
//            avatarImageView.setClip(clip);
//        });

//        avatarImageView.setFitWidth(120);
//        avatarImageView.setFitHeight(120);
//        avatarImageView.setPreserveRatio(false); // Đảm bảo ảnh fill hết vùng
//
//        avatarImageView.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
//            double radius = Math.min(newVal.getWidth(), newVal.getHeight()) / 2;
//            Circle clip = new Circle(radius, radius, radius);
//            avatarImageView.setClip(clip);
//        });
//        chooseAvatarBtn.setOnAction(e -> chooseAvatar());
        avatarImageView.setFitWidth(120);
        avatarImageView.setFitHeight(120);
        avatarImageView.setPreserveRatio(false);

        double radius = 60;
        Circle clip = new Circle(radius, radius, radius);
        System.out.println("Set clip for avatarImageView: " + avatarImageView);
        avatarImageView.setClip(clip);

        chooseAvatarBtn.setOnAction(e -> chooseAvatar());
    }

    public void chooseAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (file != null) {
            avatarImageView.setImage(new Image(file.toURI().toString()));
            // Set lại clip sau khi load ảnh
            double radius = 60;
            Circle clip = new Circle(radius, radius, radius);
            avatarImageView.setClip(clip);
        }
    }


}
