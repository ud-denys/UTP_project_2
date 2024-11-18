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

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new Scanner(clientSocket.getInputStream());

            if (in.hasNextLine()) {
                clientName = in.nextLine();
            }

            server.broadcastMessage(clientName + " entered the chat." , this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            String message = in.nextLine();
            System.out.println("Server received from " + clientName + ": " + message);
            server.broadcastMessage(clientName + ": " + message, this); // Broadcast to others
        }
        closeConnection();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void closeConnection() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.removeClient(this);
        server.broadcastMessage(clientName + " left.", this);
    }

    public String getClientName() {
        return clientName;
    }
}
