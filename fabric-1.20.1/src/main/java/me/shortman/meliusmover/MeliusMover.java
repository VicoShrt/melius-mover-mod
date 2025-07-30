package me.shortman.meliusmover;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class MeliusMover implements ModInitializer {
	public static final String MOD_ID = "melius-mover";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		String internalPath = "/assets/melius-mover/data/melius_command/command.json";
		Path configDir = Path.of(System.getProperty("user.dir"), "config", "melius-commands", "commands");
		Path outputFile = configDir.resolve("deano_command.json");

		if (Files.notExists(outputFile)) {
			try {
				Files.createDirectories(configDir);

				try (InputStream in = MeliusMover.class.getResourceAsStream(internalPath)) {
					if (in != null) {
						Files.copy(in, outputFile, StandardCopyOption.REPLACE_EXISTING);
						System.out.println("Copied my_commands.json to config folder.");
					} else {
						System.err.println("Could not find internal JSON: " + internalPath);
					}
				}

			} catch (IOException e) {
				LOGGER.error(e.toString());
			}
		}
	}
}