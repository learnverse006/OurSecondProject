package models;

import java.sql.*;

public class Attachment {
    private int attachmentId;
    private int messageId;
    private String fileUrl;
    private String fileType;
    private int fileSize;

    public Attachment(int attachmentId, int messageId, String fileUrl, String fileType, int fileSize) {
        this.attachmentId = attachmentId;
        this.messageId = messageId;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    // CREATE - Lưu tệp đính kèm vào cơ sở dữ liệu
    public boolean save(Connection conn) throws SQLException {
        String query = "INSERT INTO attachment (message_id, file_url, file_type, file_size) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.messageId);
            stmt.setString(2, this.fileUrl);
            stmt.setString(3, this.fileType);
            stmt.setInt(4, this.fileSize);
            return stmt.executeUpdate() > 0;
        }
    }

    // UPDATE - Cập nhật tệp đính kèm
    public boolean update(Connection conn) throws SQLException {
        String query = "UPDATE attachment SET file_url = ?, file_type = ?, file_size = ? WHERE attachment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.fileUrl);
            stmt.setString(2, this.fileType);
            stmt.setInt(3, this.fileSize);
            stmt.setInt(4, this.attachmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE - Xóa tệp đính kèm
    public boolean delete(Connection conn) throws SQLException {
        String query = "DELETE FROM attachment WHERE attachment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.attachmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    // READ - Tìm tệp đính kèm theo ID6
    public static Attachment findById(Connection conn, int attachmentId) throws SQLException {
        String query = "SELECT * FROM attachment WHERE attachment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, attachmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Attachment(
                            rs.getInt("attachment_id"),
                            rs.getInt("message_id"),
                            rs.getString("file_url"),
                            rs.getString("file_type"),
                            rs.getInt("file_size")
                    );
                }
                return null;
            }
        }
    }
}
