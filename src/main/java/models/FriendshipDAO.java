package models;
import models.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAO {
    private static final Connection conn = DatabaseConnection.getConnection();

    // Gửi lời mời kết bạn (A gửi cho B)
    public static boolean sendFriendRequest(int userId, int friendId) throws SQLException {
        String checkSql = "SELECT * FROM friendship WHERE user_id=? AND friend_id=? AND status='pending'";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, friendId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                String acceptSql = "UPDATE friendship SET status='accepted' WHERE user_id=? AND friend_id=?";
                try (PreparedStatement acceptStmt = conn.prepareStatement(acceptSql)) {
                    acceptStmt.setInt(1, friendId);
                    acceptStmt.setInt(2, userId);
                    return acceptStmt.executeUpdate() > 0;
                }
            } else {
                String insertSql = "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, 'pending') " +
                                   "ON DUPLICATE KEY UPDATE status='pending'";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, friendId);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        }
    }

    // Chấp nhận lời mời kết bạn (B đồng ý lời mời của A)
    public static boolean acceptFriendRequest(int fromUserId, int toUserId) throws SQLException {
        String sql = "UPDATE friendship SET status='accepted' WHERE user_id=? AND friend_id=? AND status='pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fromUserId);
            stmt.setInt(2, toUserId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Từ chối lời mời (xóa dòng)
    public static boolean rejectFriendRequest(int fromUserId, int toUserId) throws SQLException {
        String sql = "DELETE FROM friendship WHERE user_id=? AND friend_id=? AND status='pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fromUserId);
            stmt.setInt(2, toUserId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Block bạn bè
    public static boolean blockFriend(int userId, int friendId) throws SQLException {
        String sql = "UPDATE friendship SET status='blocked' WHERE (user_id=? AND friend_id=?) OR (user_id=? AND friend_id=?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Lấy danh sách bạn bè (status = accepted, hai chiều)
    public static List<Integer> getFriends(int userId) throws SQLException {
        List<Integer> friends = new ArrayList<>();
        String sql = "SELECT user_id, friend_id FROM friendship WHERE (user_id=? OR friend_id=?) AND status='accepted'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int uid = rs.getInt("user_id");
                int fid = rs.getInt("friend_id");
                if (uid == userId) {
                    friends.add(fid);
                } else {
                    friends.add(uid);
                }
            }
        }
        return friends;
    }

    // Lấy danh sách bạn bè (avatar + họ tên)
    public static List<FriendInfo> getFriendsInfo(int userId) throws SQLException {
        List<FriendInfo> friends = new ArrayList<>();
        String sql = "SELECT u.user_id, up.full_name, up.avatar_user " +
                     "FROM friendship f " +
                     "JOIN user u ON (u.user_id = f.user_id OR u.user_id = f.friend_id) " +
                     "JOIN user_profile up ON u.user_id = up.user_id " +
                     "WHERE (f.user_id=? OR f.friend_id=?) AND f.status='accepted' AND u.user_id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friends.add(new FriendInfo(
                    rs.getInt("user_id"),
                    rs.getString("full_name"),
                    rs.getString("avatar_user")
                ));
            }
        }
        return friends;
    }

    // Lấy danh sách lời mời kết bạn (avatar + họ tên)
    public static List<FriendInfo> getPendingRequestsInfo(int userId) throws SQLException {
        List<FriendInfo> requests = new ArrayList<>();
        String sql = "SELECT u.user_id, up.full_name, up.avatar_user " +
                     "FROM friendship f " +
                     "JOIN user u ON u.user_id = f.user_id " +
                     "JOIN user_profile up ON u.user_id = up.user_id " +
                     "WHERE f.friend_id=? AND f.status='pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(new FriendInfo(
                    rs.getInt("user_id"),
                    rs.getString("full_name"),
                    rs.getString("avatar_user")
                ));
            }
        }
        return requests;
    }

    // Lấy trạng thái kết bạn giữa hai user
    public static String getFriendshipStatus(int userId, int friendId) throws SQLException {
        String sql = "SELECT status FROM friendship WHERE (user_id=? AND friend_id=?) OR (user_id=? AND friend_id=?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        }
        return null;
    }

    // Helper: map ResultSet sang Friendship
    private static Friendship mapResultSetToFriendship(ResultSet rs) throws SQLException {
        return new Friendship(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("friend_id"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    public static List<FriendInfo> searchUsers(String searchTerm, int currentUserId) {
        List<FriendInfo> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT u.user_id, up.full_name, up.avatar_user " +
                          "FROM user u " +
                          "JOIN user_profile up ON u.user_id = up.user_id " +
                          "WHERE (up.full_name LIKE ? OR u.username LIKE ?) AND u.user_id != ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, "%" + searchTerm + "%");
                stmt.setString(2, "%" + searchTerm + "%");
                stmt.setInt(3, currentUserId);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    FriendInfo user = new FriendInfo(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("avatar_user")
                    );
                    results.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
