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
    private static String name; // Store the client's name


    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() {
        try {
            socket = new Socket(serverAddress, serverPort);
//            System.out.println("Connected to server at " + serverAddress + ":" + serverPort);

            // Initialize input and output streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());

            // Prompt for name and send it to the server
            Scanner userInput = new Scanner(System.in);
            System.out.print("Enter your name: ");
            name = userInput.nextLine();
            out.println(name);

        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }


    public void getMessage() {
        new Thread(() -> {
            while (in.hasNextLine()) {
                String serverMessage = in.nextLine();
                System.out.print("\r" + serverMessage + "\n" + name + ": ");
            }
        }).start();
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Client client = new Client("localhost", 60000);
        client.connect();
        client.getMessage();

        Scanner userInput = new Scanner(System.in);
        while (true) {
            System.out.print(name + ": ");
            String message = userInput.nextLine();
            client.sendMessage(message);

//            if (message.equalsIgnoreCase("exit")) {
//                client.close();
//                break;
//            }
        }
    }
}
