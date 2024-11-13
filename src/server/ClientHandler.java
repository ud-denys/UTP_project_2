package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private PrintWriter out;
    private Scanner in;
    private String clientName;

    // Constructor to initialize with client socket and server reference
    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        try {

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new Scanner(clientSocket.getInputStream());


            clientName = clientSocket.getRemoteSocketAddress().toString();
            server.broadcastMessage("New client connected: " + clientName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            String message = in.nextLine();

            if (containsBannedPhrase(message)) {
                out.println("Your message contains banned content and was not sent.");
            } else {
                System.out.println("Received from " + clientName + ": " + message);
                server.broadcastMessage(clientName + ": " + message);
            }
        }
        closeConnection();
    }


    // Method to check if a message contains any banned phrases
    private boolean containsBannedPhrase(String message) {
        for (String bannedPhrase : server.getBannedPhrases()) {
            if (message.toLowerCase().contains(bannedPhrase.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Send a message to this client
    public void sendMessage(String message) {
        out.println(message);
    }

    // Close client connection
    private void closeConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.removeClient(this); // Remove client from the server’s list
        server.broadcastMessage(clientName + " has disconnected.");
    }

    public String getClientName() {
        return clientName;
    }
}
