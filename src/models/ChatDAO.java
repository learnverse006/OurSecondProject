package models;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.nio.file.Path;
import java.nio.file.Paths;
public class ChatDAO {
    private static final Connection conn = DatabaseConnection.getConnection();
    //public static final Path REMEMBER_FILE = Paths.get("src", "resources", "remember_me.txt");


}
