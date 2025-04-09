package models;

import java.sql.*;

public class MessageStatus {
    private int messageId;
    private int userId;
    private String status;
    private Timestamp updatedAt;

    public MessageStatus(int messageId, int userId, String status, Timestamp updatedAt) {
        this.messageId = messageId;
        this.userId = userId;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean save(Connection conn) throws SQLException {
        String query = "INSERT INTO messagestatus (message_id, user_id, status, updated_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.messageId);
            stmt.setInt(2, this.userId);
            stmt.setString(3, this.status);
            stmt.setTimestamp(4, this.updatedAt);
            return stmt.executeUpdate() > 0;
        }
    }

    // UPDATE - Cập nhật trạng thái tin nhắn
    public boolean update(Connection conn) throws SQLException {
        String query = "UPDATE messagestatus SET status = ?, updated_at = ? WHERE message_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.status);
            stmt.setTimestamp(2, this.updatedAt);
            stmt.setInt(3, this.messageId);
            stmt.setInt(4, this.userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE - Xóa trạng thái tin nhắn
    public boolean delete(Connection conn) throws SQLException {
        String query = "DELETE FROM messagestatus WHERE message_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.messageId);
            stmt.setInt(2, this.userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // READ - Tìm trạng thái tin nhắn theo messageId và userId
    public static MessageStatus findById(Connection conn, int messageId, int userId) throws SQLException {
        String query = "SELECT * FROM messagestatus WHERE message_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MessageStatus(
                            rs.getInt("message_id"),
                            rs.getInt("user_id"),
                            rs.getString("status"),
                            rs.getTimestamp("updated_at")
                    );
                }
                return null;
            }
        }
    }
    // CRUD MODEL
}
