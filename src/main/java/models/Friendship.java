package models;
import models.User;
import models.UserDAO;
import models.UserProfileDAO;
import models.UserProfile;
import java.time.LocalDateTime;

public class Friendship {
    private int id;
    private int userId;
    private int friendId;
    private String status;
    private LocalDateTime createAt;

    public Friendship() {

    }
    public Friendship(int id, int userId, int friendId, String status, LocalDateTime createAt) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
        this.createAt = createAt;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getUserId() {
        return userId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

}
