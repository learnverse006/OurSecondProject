package controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import models.UserProfile;
import models.UserProfileDAO;
import models.*;
import java.io.File;
import java.sql.SQLException;

public class ProfileEditController {
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField facebookField;
    @FXML
    private TextField jobField;
    @FXML
    private TextField expField;
    @FXML
    private TextField locationField;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private ImageView coverImageView;
    @FXML
    private Button chooseAvatarBtn;
    @FXML
    private Button chooseCoverBtn;
    @FXML
    private Button saveBtn;

    private String avatarPath;
    private String coverPath;
    private int currentUserId;
    public void setUserId(int currentUserId) {
        this.currentUserId = currentUserId;
        loadProfile(currentUserId);
    }

    @FXML
    public void initialize() {
        coverImageView.setImage(new Image(getClass().getResource("/Image/activeSlide.png").toExternalForm()));
        avatarImageView.setImage(new Image(getClass().getResource("/Image/DefaultAvatar.jpg").toExternalForm()));
        avatarImageView.setFitWidth(120);
        avatarImageView.setFitHeight(120);
        avatarImageView.setPreserveRatio(false);

        double radius = 60;
        Circle clip = new Circle(radius, radius, radius);
        System.out.println("Set clip for avatarImageView: " + avatarImageView);
        avatarImageView.setClip(clip);
        chooseAvatarBtn.setOnAction(e -> chooseAvatar());
        chooseCoverBtn.setOnAction(e -> chooseCoverPicture());
        saveBtn.setOnAction(e -> handleSave());
        if(checkInvalidUserId(currentUserId) == true) loadProfile(currentUserId);
    }

    @FXML
    public void inti() {

    }

    public boolean checkInvalidUserId(int userId) {
        return (userId > 0);
    }

    private void chooseAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(avatarImageView.getScene().getWindow());
        if (file != null) {
            avatarImageView.setImage(new Image(file.toURI().toString()));
            avatarPath = file.getAbsolutePath();
            double radius = 60;
            Circle clip = new Circle(radius, radius, radius);
            avatarImageView.setClip(clip);
        }
    }

    private void chooseCoverPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh bìa");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(coverImageView.getScene().getWindow());
        if (file != null) {
            coverImageView.setImage(new Image(file.toURI().toString()));
            coverPath = file.getAbsolutePath();
        }
    }

    private void handleSave() {
        UserProfile profile = new UserProfile();
        profile.setUserId(currentUserId);
        profile.setFullName(fullNameField.getText());
        profile.setFacebookLink(facebookField.getText());
        profile.setJobTitle(jobField.getText());
        profile.setExp(expField.getText());
        profile.setLocation(locationField.getText());
        if (coverPath == null || coverPath.isEmpty()) {
            try {
                UserProfileDAO dao = new UserProfileDAO();
                UserProfile oldProfile = dao.getUserProfile(currentUserId);
                if (oldProfile != null) {
                    coverPath = oldProfile.getCoverPicture();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        profile.setCoverPicture(coverPath);

        try {
            UserProfileDAO dao = new UserProfileDAO();
            boolean success = dao.updateProfile(profile);
            if (success) {
                showAlert("Thành công", "Đã lưu thông tin cá nhân!");
            } else {
                showAlert("Lỗi", "Không thể lưu thông tin cá nhân!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi lưu thông tin cá nhân!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void loadProfile(int userId) {
        try {
            UserProfileDAO userProfileDAO = new UserProfileDAO();
            UserProfile profile = userProfileDAO.getUserProfile(userId);
            if(profile != null) {
                fullNameField.setText(profile.getFullName());
                jobField.setText(profile.getJobTitle());
                locationField.setText(profile.getLocation());
                facebookField.setText(profile.getFacebookLink());
                expField.setText(profile.getExp());
                if(profile.getCoverPicture() != null && !profile.getCoverPicture().isEmpty()) {
                    File file = new File(profile.getCoverPicture());
                    if (file.exists()) {
                        coverImageView.setImage(new Image(file.toURI().toString()));
                    }
                    coverPath = profile.getCoverPicture();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
