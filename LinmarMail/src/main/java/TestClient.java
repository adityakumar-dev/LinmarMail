import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) {
        String serverIP = "localhost"; // Try with localhost first
        int port = 2025;
        
        try (Socket socket = new Socket()) {
            // Set connection timeout
            System.out.println("Attempting to connect to " + serverIP + ":" + port);
            socket.connect(new InetSocketAddress(serverIP, port), 5000); // 5 seconds timeout
            System.out.println("Connected successfully!");
            
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            
            // Start a separate thread to continuously read server responses
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Server: " + line);
                    }
                } catch (IOException e) {
                    System.out.println("Server connection closed: " + e.getMessage());
                }
            });
            readerThread.start();
            
            // Main thread handles sending commands
            System.out.println("Enter commands (type 'quit' to exit):");
            String line;
            while (!(line = scanner.nextLine()).equalsIgnoreCase("quit")) {
                System.out.println("Sending: " + line);
                writer.println(line);
                writer.flush();
                
                // Small delay to ensure we don't flood the server
                Thread.sleep(100);
            }
            
            socket.close();
            
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
} 