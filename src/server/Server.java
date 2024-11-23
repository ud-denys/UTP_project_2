package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Server {
    private final int port;
    private final String name;
    private final Set<ClientHandler> clients = new HashSet<>();

    public Server() {
        ServerConfig config = ServerConfig.getInstance();
        this.port = config.getPort();
        this.name = config.getName();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(name + " started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void broadcastMessageWithExclusions(String sender, String message, List<String> exclusions) {
        String exclusionInfo = "is whispering (" + String.join(", ", exclusions) + " did not get the message):";
        for (ClientHandler client : clients) {
            if (!exclusions.contains(client.getClientName()) && client != getClientByName(sender)) {
                client.sendMessage(sender + " " + exclusionInfo + " " + message);
            }
        }
    }

    public ClientHandler getClientByName(String username) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equalsIgnoreCase(username)) {
                return client;
            }
        }
        return null;
    }

    public void sendMessageToSpecificClients(String sender, String message, List<String> recipients) {
        String recipientInfo = "is whispering (only " + String.join(", ", recipients) + " gets the message):";
        for (String recipient : recipients) {
            ClientHandler client = getClientByName(recipient);
            if (client != null) {
                client.sendMessage(sender + " " + recipientInfo + " " + message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public List<String> getBannedPhrases() {
        return ServerConfig.getInstance().getBannedPhrases();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
