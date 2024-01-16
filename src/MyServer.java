import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server is waiting for a client to connect...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Set up input and output streams for communication
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            // Start a thread to handle server input
            Thread serverInputThread = new Thread(() -> {
                try {
                    String clientMessage;
                    while (isRunning && (clientMessage = clientReader.readLine()) != null) {
                        System.out.println("Client: " + clientMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverInputThread.start();

            // Start a thread to handle server output
            Thread serverOutputThread = new Thread(() -> {
                try {
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    String inputLine;
                    while (isRunning) {
                        inputLine = consoleReader.readLine();
                        if (inputLine != null) {
                            serverWriter.println("Server: " + inputLine);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverOutputThread.start();

            // Wait for threads to finish
            serverInputThread.join();
            serverOutputThread.join();

            // Close resources
            serverSocket.close();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
