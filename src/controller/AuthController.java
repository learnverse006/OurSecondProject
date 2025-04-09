package controller;

import models.UserDAO;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import util.AESUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import static models.UserDAO.REMEMBER_FILE;

public class AuthController {

    private static final String SECRET_KEY = "1234567890123456";

    public static boolean login(String username, String password) {
        User user = UserDAO.findByUsername(username);
        return user != null && BCrypt.checkpw(password, user.getPasswordHash());
    }
    public static User LoginUser(String username, String password) {
        return UserDAO.findByUsername(username);
    }
    public static boolean register(String name, String email, String password) {
        if (UserDAO.findByUsername(name) != null) return false;
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(name, email, hashed);
        return UserDAO.insertUser(newUser);
    }

    public static void saveRememberedUser(String username, String password) {
        try {
            String encrypted = AESUtil.encrypt(username + ":" + password, SECRET_KEY);
            Files.writeString(REMEMBER_FILE, encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Optional<String[]> getRememberedUser() {
        try {
            if (Files.exists(REMEMBER_FILE)) {
                String encrypted = Files.readString(REMEMBER_FILE).trim();
                String decrypted = AESUtil.decrypt(encrypted, SECRET_KEY);
                String[] parts = decrypted.split(":", 2);
                if (parts.length == 2) {
                    return Optional.of(parts);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void clearRememberedUser() {
        try {
            Files.deleteIfExists(REMEMBER_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
