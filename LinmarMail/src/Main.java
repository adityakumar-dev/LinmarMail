import java.net.ServerSocket;
import java.net.Socket;
public class Main {
    public static void main(String[] args) {
        int port = 2025;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);
            while (true) { 
             Socket clientSocket = serverSocket.accept();
             System.out.println("Client connected: " + clientSocket.getInetAddress());
             ClientHandler clientHandler = new ClientHandler(clientSocket);
             clientHandler.start();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}