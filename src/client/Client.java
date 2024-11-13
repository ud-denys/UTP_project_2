package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    // Constructor that takes server address and port
    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    // Method to connect to the server
    public void connect() {
        try {
            // Create socket and connect to server
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server at " + serverAddress + ":" + serverPort);

            // Initialize input and output streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());

        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
        }
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    // Method to receive messages from the server (renamed to getMessage)
    public void getMessage() {
        new Thread(() -> {
            while (in.hasNextLine()) {
                String serverMessage = in.nextLine();
                System.out.println("Server: " + serverMessage);
            }
        }).start();
    }

    // Method to close the connection
    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main method for testing the client
    public static void main(String[] args) {
        Client client = new Client("localhost", 60000); // Replace "localhost" with the server address if needed
        client.connect();
        client.getMessage();

        // Send a test message (optional)
        client.sendMessage("Here it is - spam!");


//        client.close();
    }
}
