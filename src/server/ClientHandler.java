package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
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

            server.broadcastMessage("New client connected: " + clientName, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (in.hasNextLine()) {
            String message = in.nextLine();

            if (message.startsWith("@")) {
                handleDirectMessage(message);
            } else if (message.startsWith("!")) {
                handleExclusionMessage(message);
            } else if (message.equalsIgnoreCase("/banned")) {
                sendBannedPhrases();
            } else if (message.equalsIgnoreCase("/help")) {
                sendHelpMessage();
            } else if (containsBannedPhrase(message)) {
                notifyBannedMessage();
            } else {
                server.broadcastMessage(clientName + ": " + message, this);
            }
        }
        closeConnection();
    }

    private void handleDirectMessage(String message) {
        int spaceIndex = message.indexOf(" ");
        if (spaceIndex != -1) {
            String recipients = message.substring(1, spaceIndex);
            String actualMessage = message.substring(spaceIndex + 1);
            List<String> recipientList = List.of(recipients.split(","));
            server.sendMessageToSpecificClients(clientName, actualMessage, recipientList);
        } else {
            out.println("Invalid direct message format. Use: @username1,username2 Message");
        }
    }

    private void handleExclusionMessage(String message) {
        int spaceIndex = message.indexOf(" ");
        if (spaceIndex != -1) {
            String exclusions = message.substring(1, spaceIndex);
            String actualMessage = message.substring(spaceIndex + 1);
            server.broadcastMessageWithExclusions(clientName, actualMessage, List.of(exclusions.split(",")));
        } else {
            out.println("Invalid exclusion message format. Use: !username1,username2 Message");
        }
    }

    private void sendBannedPhrases() {
        List<String> bannedPhrases = server.getBannedPhrases();
        out.println("Banned phrases: " + String.join(", ", bannedPhrases));
    }

    private void sendHelpMessage() {
        String helpMessage = """
                Available commands:
                1. @username1,username2 Message - send a private message to specified users.
                2. !username1,username2 Message - send a message to everyone except specified users.
                3. /banned - list of banned phrases.
                4. /help - display this help message.
                5. Any general message will be broadcasted to all connected clients.
                6. /exit - disconnect the client from server.
                """;
        out.println(helpMessage);
    }

    private boolean containsBannedPhrase(String message) {
        List<String> bannedPhrases = server.getBannedPhrases();
        for (String phrase : bannedPhrases) {
            if (message.toLowerCase().contains(phrase.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void notifyBannedMessage() {
        out.println("Your message contains banned content and was not accepted.");
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
        server.broadcastMessage(clientName + " has disconnected.", this);
    }

    public String getClientName() {
        return clientName;
    }
}
