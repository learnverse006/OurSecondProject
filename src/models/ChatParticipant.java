package models;

import java.sql.*;

public class ChatParticipant {
    private int chatId;
    private int userId;
    private String role;
    private Timestamp joinedAt;


    public ChatParticipant(int chatId, int userId, String role, Timestamp joinedAt) {
        this.chatId = chatId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    // CREATE - Thêm người tham gia vào phòng chat
    public boolean save(Connection conn) throws SQLException {
        String query = "INSERT INTO chatparticipant (chat_id, user_id, role, joined_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.chatId);
            stmt.setInt(2, this.userId);
            stmt.setString(3, this.role);
            stmt.setTimestamp(4, this.joinedAt);
            return stmt.executeUpdate() > 0;
        }
    }

    // UPDATE - Cập nhật vai trò hoặc thời gian tham gia
    public boolean update(Connection conn) throws SQLException {
        String query = "UPDATE chatparticipant SET role = ?, joined_at = ? WHERE chat_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.role);
            stmt.setTimestamp(2, this.joinedAt);
            stmt.setInt(3, this.chatId);
            stmt.setInt(4, this.userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE - Xóa người tham gia khỏi phòng chat
    public boolean delete(Connection conn) throws SQLException {
        String query = "DELETE FROM chatparticipant WHERE chat_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.chatId);
            stmt.setInt(2, this.userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // READ - Tìm người tham gia phòng chat theo chatId và userId
    public static ChatParticipant findById(Connection conn, int chatId, int userId) throws SQLException {
        String query = "SELECT * FROM chatparticipant WHERE chat_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, chatId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ChatParticipant(
                            rs.getInt("chat_id"),
                            rs.getInt("user_id"),
                            rs.getString("role"),
                            rs.getTimestamp("joined_at")
                    );
                }
                return null;
            }
        }
    }
}
