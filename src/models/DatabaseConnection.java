package models;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://26.66.135.84/chat_app";
    private static final String USER = "host_app";
    private static final String PASSWORD = "nhatminhtanphat123";
    private static Connection connection;

    public static Connection getConnection(){
        if(connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Kết nối thành công CSDL");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("❌ Không thể kết nối CSDL: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if(connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối tới Database");
            }
            catch (SQLException e) {
                System.err.println("❌ Không thể đóng kết nối: " + e.getMessage());
            }
        }
    }

}
