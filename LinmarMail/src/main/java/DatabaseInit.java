
import java.sql.*;

public class DatabaseInit {

    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "admin";
    private static final String PASSWORD = "123456";
    private static final String DATABASE_NAME = "linmar_mail";

    DatabaseInit() {
        try {
            Connection conn = DriverManager.getConnection(ROOT_URL, USER, PASSWORD);
            Statement statement = conn.createStatement();

            System.out.println("Creating database" + DATABASE_NAME);
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            System.out.println("Database created");

            Connection ConDb = DriverManager.getConnection(ROOT_URL + DATABASE_NAME, USER, PASSWORD);
            System.out.println("Connected to database" + DATABASE_NAME);
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "email VARCHAR(100) NOT NULL UNIQUE,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            statement.executeUpdate(createUsersTable);
            System.out.println("Table created");
            conn.close();
            ConDb.close();

        } catch (SQLException e) {
            System.out.println("Database Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
