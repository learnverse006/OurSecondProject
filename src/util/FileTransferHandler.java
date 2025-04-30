// File: utils/FileTransferHandler.java
package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class FileTransferHandler {

    public static void sendFile(File file, Socket socket) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            dos.writeUTF("[FILE]" + file.getName());
            dos.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            dos.flush();
        }
    }

    public static void receiveFile(Socket socket, String saveDir) throws IOException {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            String fileNameTag = dis.readUTF();
            String fileName = fileNameTag.startsWith("[FILE]") ? fileNameTag.substring(6) : fileNameTag;
            long fileSize = dis.readLong();

            File saveFile = new File(saveDir, fileName);
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile))) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;

                while (totalRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
                bos.flush();
            }
        }
    }

    public static File chooseFile(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tệp để gửi");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("file", "*.txt", "*.jpg", "*.jpeg", "*.gif")
        );
        return fileChooser.showOpenDialog(ownerWindow);
    }

    public static void sendImage(File imageFile, Socket socket) throws IOException {
        FileInputStream fis = new FileInputStream(imageFile);
        byte[] imageBytes = fis.readAllBytes();
        fis.close();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF("[IMG]" + base64);
        dos.flush();
    }

    public static ImageView receiveImage(String base64String) {
        if (base64String.startsWith("[IMG]")) {
            try {
                String base64 = base64String.substring(5);
                byte[] imageBytes = Base64.getDecoder().decode(base64);
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                Image image = new Image(bais);
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(250);
                imageView.setSmooth(true);
                imageView.setCache(true);
                return imageView;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static HBox buildImageBubble(ImageView imageView, boolean isSender) {
        HBox bubble = new HBox(imageView);
        bubble.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        bubble.setPadding(new Insets(5));
        return bubble;
    }


    public static ImageView previewImage(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            Image image = new Image(fis);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(250);
            imageView.setSmooth(true);
            imageView.setCache(true);
            return imageView;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}