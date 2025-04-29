import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        String serverIP = "mail.vmsbutu.it.com";

        try (Socket socket = new Socket(serverIP, 2025);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            System.out.println("Connected to LinmarMail Server");
            
            // Read welcome message
            System.out.println("Server: " + input.readLine());

            // LOGIN
            writer.println("AUTH LOGIN");
            System.out.println("Sent: AUTH LOGIN");
            System.out.println("Server: " + input.readLine());

            writer.println("testuser@mail.com");
            System.out.println("Sent: testuser@mail.com");
            System.out.println("Server: " + input.readLine());

            writer.println("testpassword");
            System.out.println("Sent: testpassword");
            System.out.println("Server: " + input.readLine());

            writer.println("MAIL FROM: test@mail.com");
            System.out.println("Sent: MAIL FROM: test@mail.com");

            writer.println("RCPT TO: test@mail.com");
            System.out.println("Sent: RCPT TO: test@mail.com");

            writer.println("SUBJECT: test message.");
            System.out.println("Sent: SUBJECT: test message.");

            writer.println("DATA");
            System.out.println("Sent: DATA");
            System.out.println("Server: " + input.readLine());

            writer.println("test message. this is a test message.");
            writer.println(".");
            System.out.println("Sent: message body and end marker");
            System.out.println("Server: " + input.readLine());

            writer.println("QUIT");
            System.out.println("Sent: QUIT");
            System.out.println("Server: " + input.readLine());


        } catch (Exception e) {
            e.printStackTrace();
        }
        TestClient.main(args);
    }
}