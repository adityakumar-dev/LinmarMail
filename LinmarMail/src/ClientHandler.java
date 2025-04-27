
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    @Override
    public  void run(){
        try( BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
           ) {

                String message;
                while((message = reader.readLine()) != null){
                System.out.println("Received message: " + message);
                writer.println("Message received: " + message);
                writer.flush();

                if(message.startsWith("HELO")){
                    writer.println("Hello, client! "+message.substring(5) + " " + clientSocket.getInetAddress());
                    writer.flush();
                }else if(message.startsWith("AUTH LOGIN")){
                    writer.println("235 Authentication successful");
                    writer.flush();
                }else if(message.startsWith("MAIL FROM:")){
                    writer.println("250 OK");
                    writer.flush();
                }else if(message.startsWith("RCPT TO:")){
                    writer.println("250 OK");
                    writer.flush();
                }else if(message.startsWith("DATA")){
                    writer.println("354 End data with <CR><LF>.<CR><LF>");
                    StringBuilder data = new StringBuilder();
                    while ((message = reader.readLine()) != null && !message.equals(".")){
                        data.append(message);
                    }
                    data.append("\n");
                    writer.println("250 OK");
                    writer.flush();
                    System.out.println("Received email: " + data.toString());
                }else if(message.startsWith("QUIT")){
                    writer.println("221  Bye");
                    writer.flush();
                    break;
                }else{
                    writer.println("500 Syntax error, command unrecognized");
                    writer.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}