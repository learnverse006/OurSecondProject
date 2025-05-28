package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ForumPostDAO {

    public static void insertPost(ForumPost post) {
        String sql = "INSERT INTO forum_posts (user_id, content) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<ForumPost> getAllPosts() {
        List<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT * FROM forum_posts ORDER BY created_at DESC";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                ForumPost post = new ForumPost(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                );
                posts.add(post);
            }
        }catch (Exception e){ e.printStackTrace(); }
        return posts;
    }
}
