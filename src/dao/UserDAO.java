package dao;

import models.DatabaseConnection;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserDAO {
    private static final Connection conn = DatabaseConnection.getConnection();
    public static final Path REMEMBER_FILE = Paths.get("src", "resources", "remember_me.txt");

    public static boolean insertUser(User user) {
        String sql = "INSERT INTO user (username, email, password_hash, profile_picture, status, last_seen) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getProfilePicture());
            stmt.setString(5, user.getStatus());
            stmt.setTimestamp(6, user.getLastSeen());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xảy ra khi đăng ký người dùng: " + e.getMessage());
            return false;
        }
    }

    public static User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("profile_picture"),
                        rs.getString("status"),
                        rs.getTimestamp("last_seen")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng: " + e.getMessage());
        }
        return null;
    }

    public static User findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("profile_picture"),
                        rs.getString("status"),
                        rs.getTimestamp("last_seen")
                );
            }
        } catch(SQLException e) {
            System.err.println("Lỗi khi tìm người dùng: " + e.getMessage());
        }
        return null;
    }

    public static boolean existsByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e){
            System.err.println("Tài khoản này đã tồn tại, vui lòng thử lại! " + e.getMessage());
        }
        return false;
    }

    public static boolean existsByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e){
            System.err.println("Email trên đã được đăng ký! " + e.getMessage());
        }
        return false;
    }
    //
//     Insert user by default : thêm người dùng với 3 trường dữ liệu chính (user/name, email, password)
    //  Có thể dùng over loading method cũng được
    public static boolean insertUserByDefault(User user){
        String sql = "INSERT INTO user(username, email, password_hash) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e){
            System.err.println("Lỗi xảy ra khi đăng ký người dùng, vui lòng thử lại! " + e.getMessage());
            return false;
        }
    }

    public static boolean checkPassword(String username, String plainPassword) {
        User user = findByUsername(username);
        if (user == null) return false;
        return BCrypt.checkpw(plainPassword, user.getPasswordHash());
    }

    public static String getUsernameById(int userId) {
        String sql = "SELECT username FROM user WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public static String getPictureByUserID(int userID) {
        String sql = "SELECT profile_picture FROM user WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_picture");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Default Picture";
    }


//    public static int findUserIDByUserName() {
//
//    }
}
