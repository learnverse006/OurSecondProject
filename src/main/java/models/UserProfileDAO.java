package models;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

//import main.java.models.UserProfile;
public class UserProfileDAO {
    // If userID đã tồn tại -> Update, xử lí try-catch
    public boolean updateProfile(UserProfile userProfile) throws SQLException{
        String sql = "INSERT INTO user_profile (user_id, full_name, job_title, bio, location, exp, website, " +
                "facebook_link, discord_link, portfolio_link, cover_picture, avatar_user) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE full_name=?, job_title=?, bio=?, location=?, exp=?, website=?, " +
                "facebook_link=?, discord_link=?, portfolio_link=?, cover_picture=?, avatar_user=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userProfile.getUserId());
            stmt.setString(2, userProfile.getFullName());
            stmt.setString(3, userProfile.getJobTitle());
            stmt.setString(4, userProfile.getBio());
            stmt.setString(5, userProfile.getLocation());
            stmt.setString(6, userProfile.getExp());
            stmt.setString(7, userProfile.getWebsite());
            stmt.setString(8, userProfile.getFacebookLink());
            stmt.setString(9, userProfile.getDiscordLink());
            stmt.setString(10, userProfile.getPortfolioLink());
            stmt.setString(11, userProfile.getCoverPicture());
            stmt.setString(12, userProfile.getAvatarPicture());

            // If Have user ID in Database
            stmt.setString(13, userProfile.getFullName());
            stmt.setString(14, userProfile.getJobTitle());
            stmt.setString(15, userProfile.getBio());
            stmt.setString(16, userProfile.getLocation());
            stmt.setString(17, userProfile.getExp());
            stmt.setString(18, userProfile.getWebsite());
            stmt.setString(19, userProfile.getFacebookLink());
            stmt.setString(20, userProfile.getDiscordLink());
            stmt.setString(21, userProfile.getPortfolioLink());
            stmt.setString(22, userProfile.getCoverPicture());
            stmt.setString(23, userProfile.getAvatarPicture());

            return stmt.executeUpdate() > 0;
        }
    }

//    public UserProfile getUserPicture(int userID) throws SQLException {
//
//    }

    public UserProfile getUserProfile(int userID) throws SQLException {
        String sql = "SELECT * FROM user_profile WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return new UserProfile(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("job_title"),
                        rs.getString("bio"),
                        rs.getString("location"),
                        rs.getString("exp"),
                        rs.getString("website"),
                        rs.getString("facebook_link"),
                        rs.getString("discord_link"),
                        rs.getString("portfolio_link"),
                        rs.getString("cover_picture"),
                        rs.getString("avatar_user")
                );
            }
        }
        return null;
    }
}
