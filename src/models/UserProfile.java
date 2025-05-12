package models;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserProfile {
    private int userID;
    private String fullName;
    private String jobTitle;
    private String bio;
    private String location;
    private String exp;
    private String website;
    private String facebookLink;
    private String discordLink;
    private String portfolioLink; /// ho so nang luc
    private String coverPicture; // hinh anh dai dien

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
