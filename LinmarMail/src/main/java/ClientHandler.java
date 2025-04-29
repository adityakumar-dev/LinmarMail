import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientHandler extends Thread {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true) // auto-flush enabled
        ) {
            String message;
            boolean authInProgress = false;
            boolean signupInProgress = false;
            String tempEmail = null;
            String tempPassword = null;
            boolean isAuthenticated = false;
            String receiver = null;
            String body = null;
            String sender = null;
            String subject = null;
            writer.println("220 Welcome to the mail server");
            while ((message = reader.readLine()) != null) {
    
                System.out.println("Received message: " + message);
                if(message.startsWith("AUTH")){
                    if(message.equals("AUTH LOGIN")){
                        authInProgress = true;
                        continue;
                    }else if(message.equals("AUTH SIGNUP")){
                        signupInProgress = true;
                        continue;
                    }
                }
                if (authInProgress) {
                    if (tempEmail == null) {
                        tempEmail = message;
                        writer.println("334 Password:");
                    } else {
                        tempPassword = message;
                        System.out.println("tempEmail: " + tempEmail);
                        System.out.println("tempPassword: " + tempPassword);
                        isAuthenticated = handleLOGIN(tempEmail, tempPassword, writer);
                        authInProgress = false;
                        tempEmail = null;
                        tempPassword = null;
                    }
                    continue;
                }

                if (signupInProgress) {
                    if (tempEmail == null) {
                        tempEmail = message;
                        writer.println("334 Password:");
                    } else {
                        tempPassword = message;
                        isAuthenticated = handleSIGNUP(tempEmail, tempPassword, writer);
                        signupInProgress = false;
                        tempEmail = null;
                        tempPassword = null;
                    }
                    continue;
                }
                if(isAuthenticated){
                if (message.startsWith("MAIL FROM:")) {
                    sender = message.substring(10).trim();
                    writer.println("250 Sender " + sender + " OK");
                } else if (message.startsWith("RCPT TO:")) {
                    receiver = message.substring(8).trim();
                    writer.println("250 Recipient " + receiver + " OK");
                } else if (message.startsWith("SUBJECT:")) {
                    subject = message.substring(8);
                    System.out.println("Subject: " + subject);
                    writer.println("250 Subject OK");
                } else if (message.equals("DATA")) {
                    writer.println("354 End data with <CR><LF>.<CR><LF>");
                    StringBuilder data = new StringBuilder();
                    while ((message = reader.readLine()) != null && !message.equals(".")) {
                        data.append(message).append("\n");
                    }
                    writer.println("250 OK");
                    handleSEND(sender, receiver, subject, data.toString(), writer);
                    subject = null;
                    receiver = null;
                    sender = null;
                } else if (message.equals("QUIT")) {
                    writer.println("221 Bye");
                    break;
                } else {
                    writer.println("500 Syntax error, command unrecognized");
                
                }
                }else{
                    writer.println("535 Authentication required");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean handleSIGNUP(String email, String password, PrintWriter writer) {
        try {
            DatabaseConnector db = new DatabaseConnector();
            Connection con = db.getConnection();
            String query = "INSERT INTO users (email, password) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ps.executeUpdate();
            con.close();
            writer.println("235 Signup successful");
            return true;
        } catch (SQLException e) {
            writer.println("535 Signup failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
private boolean handleLOGIN(String email, String password, PrintWriter writer) {
    try {
        DatabaseConnector db = new DatabaseConnector();
        try (Connection con = db.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                writer.println("235 Authentication successful");
                return true;
            } else {
                writer.println("535 Authentication failed");
                return false;
            }
        }
    } catch (SQLException e) {
        writer.println("535 Authentication error: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}
private void handleSEND(String sender, String receiver, String subject, String body, PrintWriter writer) {
    try {
        DatabaseConnector db = new DatabaseConnector();
        Connection con = db.getConnection();
        String query = "INSERT INTO emails (sender, receiver, subject, body) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, sender);
        ps.setString(2, receiver);
        ps.setString(3, subject);
        ps.setString(4, body);
        ps.executeUpdate();
        con.close();
        writer.println("250 Email sent successfully");
    } catch (SQLException e) {
        writer.println("550 Email sending failed: " + e.getMessage());
        e.printStackTrace();
    }
}
}
