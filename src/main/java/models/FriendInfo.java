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

    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getAvatarUser() { return avatarUser; }
} 