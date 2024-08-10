package me.vexmc.xelautototem.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import me.vexmc.xelautototem.config.ConfigManager;
import me.vexmc.xelautototem.utils.VersionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class XelAutoTotem implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("XelAutoTotem");
	public static final String VERSION = "1.0";

	@Override
	public void onInitializeClient() {
		ConfigManager.loadConfig();

		// Async version check
		CompletableFuture.runAsync(VersionChecker::checkForUpdate)
				.thenRun(() -> LOGGER.info("Version check completed"))
				.exceptionally(e -> {
					LOGGER.error("Version check failed", e);
					return null;
				});

		LOGGER.info("XelAutoTotem initialized!");
	}
}
