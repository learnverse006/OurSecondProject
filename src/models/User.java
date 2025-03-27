package models;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String profilePicture;
    private String status; // ENUM: ONLINE, OFFLINE, BUSY, etc.
    private java.sql.Timestamp lastSeen;

    public User() {}

    public User(int userId, String username, String email, String passwordHash, String profilePicture, String status, java.sql.Timestamp lastSeen) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.profilePicture = profilePicture;
        this.status = status;
        this.lastSeen = lastSeen;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.sql.Timestamp getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(java.sql.Timestamp lastSeen) {
        this.lastSeen = lastSeen;
    }

    // Utility method
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", lastSeen=" + lastSeen +
                '}';
    }
}
