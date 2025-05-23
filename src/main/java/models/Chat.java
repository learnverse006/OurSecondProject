package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private int chatId;
    private String chatType;
    private String name;
    private Timestamp createdAt;


    public Chat(int chatId, String chatType, String name, Timestamp createdAt) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.name = name;
        this.createdAt = createdAt;
    }
    // Create - Lưu phòng chat vào cơ sở dữ liệu
    public boolean save(Connection conn) throws SQLException {
        String query = "INSERT INTO chat (chat_type, name, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.chatType);
            stmt.setString(2, this.name);
            stmt.setTimestamp(3, this.createdAt);
            return stmt.executeUpdate() > 0;
        }
    }

    // Update - Cập nhật phòng chat
    public boolean update(Connection conn) throws SQLException {
        String query = "UPDATE chat SET chat_type = ?, name = ?, created_at = ? WHERE chat_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.chatType);
            stmt.setString(2, this.name);
            stmt.setTimestamp(3, this.createdAt);
            stmt.setInt(4, this.chatId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Delete - Xóa phòng chat
    public boolean delete(Connection conn) throws SQLException {
        String query = "DELETE FROM chat WHERE chat_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.chatId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Read - Tìm phòng chat theo ID
    public static Chat findById(Connection conn, int chatId) throws SQLException {
        String query = "SELECT * FROM chat WHERE chat_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, chatId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Chat(
                            rs.getInt("chat_id"),
                            rs.getString("chat_type"),
                            rs.getString("name"),
                            rs.getTimestamp("created_at")
                    );
                }
                return null;
            }
        }
    }

    // Read all - Lấy danh sách tất cả các phòng chat
    public static List<Chat> getAllChats(Connection conn) throws SQLException {
        String query = "SELECT * FROM chat";
        List<Chat> chats = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                chats.add(new Chat(
                        rs.getInt("chat_id"),
                        rs.getString("chat_type"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return chats;
    }
}
