package me.vexmc.xelautototem.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vexmc.xelautototem.client.XelAutoTotem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VersionChecker {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/owengregson/XelAutoTotem/releases/latest";
    private static final Logger LOGGER = LoggerFactory.getLogger("XelAutoTotem");
    private static final String version = XelAutoTotem.VERSION;

    public static void checkForUpdate() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GITHUB_API_URL))
                    .header("User-Agent", "XelAutoTotem")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                String latestVersion = jsonResponse.get("tag_name").getAsString();

                LOGGER.info("Current version --> {}", version);
                LOGGER.info("Latest version --> {}", latestVersion);

                if (!version.equals(latestVersion)) {
                    LOGGER.warn("A new version of XelAutoTotem is available! Please download it from https://github.com/owengregson/XelAutoTotem");
                } else {
                    LOGGER.info("You are using the latest version of XelAutoTotem.");
                }
            } else {
                LOGGER.error("Failed to check for updates. HTTP Response Code --> {}", response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error while checking for updates --> ", e);
            Thread.currentThread().interrupt();
        }
    }
}
