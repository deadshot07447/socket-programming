import java.io.*;
import java.net.Socket;

public class MyClient {
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);

            // Set up input and output streams for communication
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter clientWriter = new PrintWriter(socket.getOutputStream(), true);

            // Start a thread to handle client input
            Thread clientInputThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while (isRunning && (serverMessage = serverReader.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            clientInputThread.start();

            // Start a thread to handle client output
            Thread clientOutputThread = new Thread(() -> {
                try {
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    String inputLine;
                    while (isRunning) {
                        inputLine = consoleReader.readLine();
                        if (inputLine != null) {
                            clientWriter.println("Client: " + inputLine);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            clientOutputThread.start();

            // Wait for threads to finish
            clientInputThread.join();
            clientOutputThread.join();

            // Close resources
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
