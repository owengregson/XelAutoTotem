package me.vexmc.xelautototem.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static final File CONFIG_FILE = new File("config/xelautototem.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LoggerFactory.getLogger("XelAutoTotem");

    private static ConfigData config = new ConfigData();

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ConfigData.class);
                LOGGER.info("Configuration loaded successfully.");
            } catch (IOException e) {
                LOGGER.error("Failed to load configuration", e);
            }
        } else {
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            LOGGER.info("Configuration saved successfully.");
        } catch (IOException e) {
            LOGGER.error("Failed to save configuration", e);
        }
    }

    public static ConfigData getConfig() {
        return config;
    }

    public static void updateConfig(ConfigData newConfig) {
        config = newConfig;
        saveConfig();
    }

    public static class ConfigData {
        public boolean legitSwap = true;
        public boolean enabled = true;
        public boolean checkPotionEffects = true;
        public int delayInMilliseconds = 0;
        public boolean addRandomDelay = true;
        public int maxRandomDelay = 500;
    }
}
