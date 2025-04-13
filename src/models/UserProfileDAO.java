package models;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserProfileDAO {
    private static final Connection conn = DatabaseConnection.getConnection();

}
