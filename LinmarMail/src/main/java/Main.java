import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class Main {
    public static void main(String[] args) {
        int port = 2025;
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv4Addresses", "true");
        
        DatabaseConnector db = new DatabaseConnector();
        db.getConnection();
        
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            InetAddress addr = InetAddress.getByName("0.0.0.0");
            serverSocket.bind(new InetSocketAddress(addr, port), 50);
            System.out.println("Server is running on port " + port);
            System.out.println("Bound to address: " + serverSocket.getInetAddress().getHostAddress());
            
            while (true) { 
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected:");
                System.out.println("  - Remote Address: " + clientSocket.getInetAddress().getHostAddress());
                System.out.println("  - Remote Port: " + clientSocket.getPort());
                System.out.println("  - Local Port: " + clientSocket.getLocalPort());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
            
        } catch (Exception e) {
            System.out.println("Server error: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}