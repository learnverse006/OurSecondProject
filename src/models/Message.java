package models;

import java.sql.*;

public class Message {
    private int messageId;
    private int chatId;
    private int senderId;
    private String content;
    private String messageType;
    private Timestamp createdAt;

    // Constructor, getters, setters
    public Message(int messageId, int chatId, int senderId, String content, String messageType, Timestamp createdAt) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // CREATE - Lưu tin nhắn vào cơ sở dữ liệu
    public boolean save(Connection conn) throws SQLException {
        String query = "INSERT INTO message (chat_id, sender_id, content, message_type, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.chatId);
            stmt.setInt(2, this.senderId);
            stmt.setString(3, this.content);
            stmt.setString(4, this.messageType);
            stmt.setTimestamp(5, this.createdAt);
            return stmt.executeUpdate() > 0;
        }
    }

    // UPDATE - Cập nhật tin nhắn
    public boolean update(Connection conn) throws SQLException {
        String query = "UPDATE message SET content = ?, message_type = ?, created_at = ? WHERE message_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.content);
            stmt.setString(2, this.messageType);
            stmt.setTimestamp(3, this.createdAt);
            stmt.setInt(4, this.messageId);
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE - Xóa tin nhắn
    public boolean delete(Connection conn) throws SQLException {
        String query = "DELETE FROM message WHERE message_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.messageId);
            return stmt.executeUpdate() > 0;
        }
    }

    // READ - Tìm tin nhắn theo ID
    public static Message findById(Connection conn, int messageId) throws SQLException {
        String query = "SELECT * FROM message WHERE message_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getInt("message_id"),
                            rs.getInt("chat_id"),
                            rs.getInt("sender_id"),
                            rs.getString("content"),
                            rs.getString("message_type"),
                            rs.getTimestamp("created_at")
                    );
                }
                return null;
            }
        }
    }
}
