package models;

import java.sql.Timestamp;

public class ForumPost {
    private int id;
    private int userId;
    private String content;
    private Timestamp createdAt;

    public ForumPost(int id, int userId, String content, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public ForumPost(int userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
