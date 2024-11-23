package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServerConfig {
    private static final ServerConfig instance = new ServerConfig(); // Singleton instance
    private final List<String> bannedPhrases = new ArrayList<>();
    private int port;
    private String name;


    private ServerConfig() {
        try (FileInputStream fis = new FileInputStream("server_config.txt")) {
            Properties properties = new Properties();
            properties.load(fis);


            this.port = Integer.parseInt(properties.getProperty("port"));
            this.name = properties.getProperty("name");
            String banned = properties.getProperty("banned_phrases", "");
            for (String phrase : banned.split(",")) {
                bannedPhrases.add(phrase.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading server config: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid port value in server_config.txt. Using default port 60000.");
            this.port = 60000;
        }
    }

    public static ServerConfig getInstance() {
        return instance;
    }

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
