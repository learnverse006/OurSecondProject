package models;

import models.Message;
import models.Message.MessageType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private static final Connection conn = DatabaseConnection.getConnection();

    public static void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO message(chat_id, sender_id, receiver_id, content, message_type, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message.getChatID());
            stmt.setInt(2, message.getSenderID());
            if (message.getReceiverID() != null) {
                stmt.setInt(3, message.getReceiverID());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, message.getContent());
            stmt.setString(5, message.getMst().name());
            stmt.setTimestamp(6, Timestamp.valueOf(message.getCreateAt()));
            System.out.println("[DB] Đang lưu tin nhắn: " + message.getContent());
            stmt.executeUpdate();
        }
    }

    public static List<Message> getMessageByChatID(int chatID) throws SQLException {
        String sql = "SELECT * FROM message WHERE chat_id = ? ORDER BY created_at ASC";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer receiverId = rs.getObject("receiver_id") != null ? rs.getInt("receiver_id") : null;
                Message msg = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("chat_id"),
                        rs.getInt("sender_id"),
                        receiverId,
                        rs.getString("content"),
                        MessageType.valueOf(rs.getString("message_type")),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                messages.add(msg);
            }
        }
        return messages;
    }

    public static List<Message> pairChatHis(int userID1, int userID2) throws SQLException {
        String sql = "SELECT * FROM message WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY created_at ASC";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID1);
            stmt.setInt(2, userID2);
            stmt.setInt(3, userID2);
            stmt.setInt(4, userID1);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Integer receiverId = rs.getObject("receiver_id") != null ? rs.getInt("receiver_id") : null;
                Message msg = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("chat_id"),
                        rs.getInt("sender_id"),
                        receiverId,
                        rs.getString("content"),
                        MessageType.valueOf(rs.getString("message_type")),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                messages.add(msg);
            }
        }
        return messages;
    }
}