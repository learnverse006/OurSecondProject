package models;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class UserDAO {
    private final Connection conn;
    public UserDAO() {
        this.conn = DatabaseConnection.getConnection();
    }
    // Register user
    public boolean insertUser(User user) {
        String sql = "INSERT INTO user (username, email, password_hash, profile_picture, status, last_seen) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashPassword(user.getPasswordHash()));
            stmt.setString(4, user.getProfilePicture());
            stmt.setString(5, user.getStatus());
            stmt.setTimestamp(6, user.getLastSeen());
            return stmt.executeUpdate() > 0;
        }
        catch (SQLException e) {
            System.err.println("Lỗi xảy ra khi đăng ký người dùng: " + e.getMessage());
            return false;
        }
    }

    // find user by user name
    public User findByUserName(String username) {
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
            System.err.println("Lỗi khi tìm người dùng" + e.getMessage());
        }
        return null;
    }

    // find user by email

    public User findByUserEmail(String email){
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
            System.err.println("Lỗi khi tìm người dùng" + e.getMessage());
        }
        return null;
    }

    public boolean checkPassword(String username, String plainPassword) {
        User user = findByUserName(username);
        if (user == null) return false;
        return BCrypt.checkpw(plainPassword, user.getPasswordHash());
    }

    // check if the username already exits in Database
    public boolean exitsByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e){
            System.err.println("Tài khoản này đã tồn tại, vui lòng thử lại!" + e.getMessage());
        }
        return false;
    }

    // check if user email already exits in Database
    public boolean exitsByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e){
            System.err.println("Email trên đã được đăng ký!" + e.getMessage());
        }
        return false;
    }

    public boolean insertUserByDefault(User user){
        String sql = "INSERT INTO user(username, email, password_hash) VALUES (? , ? , ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashPassword(user.getPasswordHash()));
//            ResultSet rs = stmt.executeUpdate();
            return stmt.executeUpdate() > 0;
        } catch (SQLException e){
            System.err.println("Lỗi xảy ra khi đăng ký người dùng, vui lòng thử lại!" + e.getMessage());
            return false;
        }
//        return false;
    }

//    public boolean checkPasswordHash(String username, String plainPassword) {
//        User user = findByUsername(username);
//        if (user == null) return false;
//        return BCrypt.checkpw(plainPassword, user.getPasswordHash());
//    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
