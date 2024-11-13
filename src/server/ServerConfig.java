package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ServerConfig {
    public static void main(String[] args) {
        try {
            ServerConfig config = new ServerConfig("server_config.txt");
            System.out.println("Port: " + config.getPort());
            System.out.println("Name: " + config.getName());
            System.out.println("Banned Phrases: " + config.getBannedPhrases());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int port;
    private String name;
    private List<String> bannedPhrases;

    // Constructor that reads the configuration from a file
    public ServerConfig(String configFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInput = new FileInputStream(configFilePath)) {
            properties.load(fileInput);

            // Parse properties
            port = Integer.parseInt(properties.getProperty("port"));
            name = properties.getProperty("name");
            bannedPhrases = Arrays.asList(properties.getProperty("banned_phrases").split(","));
        }
    }

    // Getters for configuration properties
    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public List<String> getBannedPhrases() {
        return bannedPhrases;
    }
}
