package myliberary.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// 데이터 베이스 연결 하는 역할
public class DataBaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/library?serverTimezone=Asia/Seoul";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public Connection databaseUtil(){


        // 데이터 베이스 연결
        try (Connection connection = DriverManager.getConnection(URL, DB_USER, PASSWORD)) {

          return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
