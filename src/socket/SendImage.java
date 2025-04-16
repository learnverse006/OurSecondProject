package socket;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
public class SendImage {
    public static File chooseFile(Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tệp để gửi");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
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
                return imageView;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
