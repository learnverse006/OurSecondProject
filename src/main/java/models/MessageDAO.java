package models;

import models.*;

import java.sql.*;
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
            String typeChar = switch (message.getMst()) {
                case TEXT -> "T";
                case IMAGE -> "I";
                case EMOJI -> "E";
                case FILE -> "F";
            };
            stmt.setString(5, typeChar);
            stmt.setTimestamp(6, Timestamp.valueOf(message.getCreateAt()));
            System.out.println("[DB] Đang lưu tin nhắn: " + message.getContent());
            stmt.executeUpdate();
        }
    }

    public static void deleteMessage(Message message) throws SQLException {
        String sql = "DELETE FROM message WHERE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message.getMessageID());
            ResultSet rs = stmt.executeQuery();
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
                String typeStr = rs.getString("message_type");
                Message.MessageType type;
                if (typeStr == null || typeStr.equals("T")) {
                    type = Message.MessageType.TEXT;
                } else if (typeStr.equals("I")) {
                    type = Message.MessageType.IMAGE;
                } else if (typeStr.equals("E")) {
                    type = Message.MessageType.EMOJI;
                } else if (typeStr.equals("F")) {
                    type = Message.MessageType.FILE;
                } else {
                    type = Message.MessageType.TEXT;
                }
                Message msg = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("chat_id"),
                        rs.getInt("sender_id"),
                        receiverId,
                        rs.getString("content"),
                        type,
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                messages.add(msg);
            }
        }
        System.out.println("[MessageDAO] getMessageByChatID(" + chatID + ") => " + messages.size() + " messages");
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
                String typeStr = rs.getString("message_type");
                Message.MessageType type;
                if (typeStr == null || typeStr.equals("T")) {
                    type = Message.MessageType.TEXT;
                } else if (typeStr.equals("I")) {
                    type = Message.MessageType.IMAGE;
                } else if (typeStr.equals("E")) {
                    type = Message.MessageType.EMOJI;
                } else if (typeStr.equals("F")) {
                    type = Message.MessageType.FILE;
                } else {
                    type = Message.MessageType.TEXT;
                }
                Message msg = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("chat_id"),
                        rs.getInt("sender_id"),
                        receiverId,
                        rs.getString("content"),
                        type,
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                messages.add(msg);
            }
        }
        return messages;
    }
}