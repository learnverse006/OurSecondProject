package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {
    private static final Connection conn = DatabaseConnection.getConnection();
    //public static final Path REMEMBER_FILE = Paths.get("src", "main.java.resources", "remember_me.txt");

    // Lấy hoặc tạo chat_id cho 2 user (chat 1-1)
    public static int getOrCreateChatId(int user1Id, int user2Id) throws SQLException {
        int chatId = -1;
        String selectSql = "SELECT cm.chat_id " +
            "FROM chat_member cm " +
            "JOIN chat c ON cm.chat_id = c.chat_id " +
            "WHERE c.chat_type = 'private' AND (cm.user_id = ? OR cm.user_id = ?) " +
            "GROUP BY cm.chat_id " +
            "HAVING COUNT(DISTINCT cm.user_id) = 2";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                chatId = rs.getInt("chat_id");
            }
        }
        if (chatId != -1) return chatId;
        // Nếu chưa có, tạo mới
        String insertChat = "INSERT INTO chat (chat_type) VALUES ('private')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertChat, Statement.RETURN_GENERATED_KEYS)) {
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                chatId = rs.getInt(1);
            }
        }
        // Thêm 2 thành viên vào chat_member
        String insertMember = "INSERT INTO chat_member (chat_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertMember)) {
            stmt.setInt(1, chatId);
            stmt.setInt(2, user1Id);
            stmt.executeUpdate();
            stmt.setInt(1, chatId);
            stmt.setInt(2, user2Id);
            stmt.executeUpdate();
        }
        return chatId;
    }

    // Lấy tất cả các chat mà user tham gia
    public static List<Integer> getAllChatIdsForUser(int userId) throws SQLException {
        List<Integer> chatIds = new ArrayList<>();
        String sql = "SELECT chat_id FROM chat WHERE user1_id = ? OR user2_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                chatIds.add(rs.getInt("chat_id"));
            }
        }
        return chatIds;
    }
}
