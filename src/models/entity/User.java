package models.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String username;
    private String email;
    private String passwordHash;
    private String profilePicture;
    private String status; // ENUM: ONLINE, OFFLINE, BUSY, etc.
    private java.sql.Timestamp lastSeen;


    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
//
//    // Utility method
//    @Override
//    public String toString() {
//        return "User{" +
//                "userId=" + userId +
//                ", username='" + username + '\'' +
//                ", email='" + email + '\'' +
//                ", status=" + status +
//                ", lastSeen=" + lastSeen +
//                '}';
//    }
}
