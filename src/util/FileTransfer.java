package util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.stage.Window;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class FileTransfer {

    public static String encodeFileToBase64(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decodeBase64File(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static HBox buildDownloadableFile(String fileName, byte[] fileBytes, Window ownerWindow, boolean isSender) {
        Button downloadBtn = new Button("\uD83D\uDCC4 " + fileName + " (Tải về)");
        downloadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu file: " + fileName);
            fileChooser.setInitialFileName(fileName);
            File dest = fileChooser.showSaveDialog(ownerWindow);
            if (dest != null) {
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    fos.write(fileBytes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Label timeLabel = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        VBox fileBox = new VBox(5, downloadBtn, timeLabel);

        HBox container = new HBox(fileBox);
        container.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.setPadding(new Insets(10));
        return container;
    }

//    public static HBox buildImageBubble(ImageView imageView, boolean isSender) {
//        HBox bubble = new HBox(imageView);
//        bubble.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
//        bubble.setPadding(new Insets(5));
//        return bubble;
//    }

    public static HBox sentFileConfirmation(String fileName) {
        Label label = new Label("📄 Đã gửi file: " + fileName);
        HBox box = new HBox(label);
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    public static String[] parseFileMessage(String content) {
        return content.split(":", 3);
    }

    public static String buildFileMessage(File file) throws IOException {
        String base64 = encodeFileToBase64(file);
        return "[FILE]:" + file.getName() + ":" + base64;
    }

    public static boolean isTextFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".txt") || name.endsWith(".md") || name.endsWith(".csv");
    }
}
