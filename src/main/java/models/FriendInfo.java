package models;

public class FriendInfo {
    private int userId;
    private String fullName;
    private String avatarUser;

    public FriendInfo(int userId, String fullName, String avatarUser) {
        this.userId = userId;
        this.fullName = fullName;
        this.avatarUser = avatarUser;
    }

    public FriendInfo() {}

    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getAvatarUser() { return avatarUser; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setAvatarUser(String avatarUser) { this.avatarUser = avatarUser; }
} 