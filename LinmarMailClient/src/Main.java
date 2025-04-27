
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        String serverIP = "localhost"; // later your server's IP
        int port = 2025;

        try (Socket socket = new Socket(serverIP, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to LinmarMail Server");

            // Send commands
            writer.println("HELO linmarmail.com");
            writer.println("AUTH LOGIN user@example.com password123");
            writer.println("MAIL FROM: user@example.com");
            writer.println("RCPT TO: receiver@example.com");
            writer.println("DATA");
            writer.println("Subject: Hello from LinmarMail!");
            writer.println("This is my first email.");
            writer.println(".");
            writer.println("QUIT");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
