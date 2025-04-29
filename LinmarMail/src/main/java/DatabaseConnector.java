
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
    private  final String URL = "jdbc:mysql://localhost:3306/linmar_mail";
    private  final String USER = "admin";
    private  final String PASSWORD = "123456";

   static{
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
        System.out.println("Driver not found");
        System.exit(1);
    }
   }

    public DatabaseConnector() {
      Connection ConDb;
        try {
            ConDb = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = ConDb.createStatement();
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "password VARCHAR(255) NOT NULL,"
                    + "email VARCHAR(100) NOT NULL UNIQUE PRIMARY KEY,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            statement.executeUpdate(createUsersTable);
            System.out.println("Users Table Done");
            String createEmailsTable = "CREATE TABLE IF NOT EXISTS emails ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "sender VARCHAR(50) NOT NULL,"
                    + "receiver VARCHAR(50) NOT NULL,"
                    + "subject VARCHAR(255),"
                    + "body TEXT,"
                    + "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            statement.executeUpdate(createEmailsTable);
            System.out.println("Emails Table Done");
            ConDb.close();
    
        } catch (SQLException ex) {
        }
      }
    
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
