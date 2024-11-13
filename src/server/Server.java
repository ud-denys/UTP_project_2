package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private int port;
    private String name;
    private ServerSocket serverSocket;
    private Set<ClientHandler> clients = new HashSet<>();
    private List<String> bannedPhrases; // Add banned phrases list

    // Constructor that initializes the server using ServerConfig
    public Server(String configFilePath) {
        try {
            // Load configuration
            ServerConfig config = new ServerConfig(configFilePath);
            this.port = config.getPort();
            this.name = config.getName();
            this.bannedPhrases = config.getBannedPhrases();

            // Initialize the server socket
            serverSocket = new ServerSocket(port);
            System.out.println(name + " started on port " + port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to start accepting client connections
    public void start() {
        System.out.println("Waiting for clients to connect...");
        try {
            while (true) {
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

                // Create a ClientHandler for the client
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);

                // Start a new thread for the client handler
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to broadcast a message to all connected clients
    public void broadcastMessage(String message) {
        System.out.println("Broadcasting: " + message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    // Remove a client from the set of connected clients
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    // Getter for banned phrases
    public List<String> getBannedPhrases() {
        return bannedPhrases;
    }

    // Main method for testing
    public static void main(String[] args) {
        Server server = new Server("server_config.txt");
        server.start(); // Start listening for clients
    }
}
