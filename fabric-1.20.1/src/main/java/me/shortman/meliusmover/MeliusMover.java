package me.shortman.meliusmover;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;

public class MeliusMover implements ModInitializer {
	public static final String MOD_ID = "melius-mover";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		String internalFolderPath = "/assets/melius-mover/data/melius_commands/";
		Path configDir = Path.of(System.getProperty("user.dir"), "config", "melius-commands", "commands");

		try {
			Files.createDirectories(configDir);

			// Get the JAR file that contains this class
			URI jarUri = MeliusMover.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			Path jarPath = Paths.get(jarUri);

			// Create a file system for the JAR
			try (FileSystem jarFileSystem = FileSystems.newFileSystem(jarPath, (ClassLoader) null)) {
				Path internalFolder = jarFileSystem.getPath(internalFolderPath);

				if (Files.exists(internalFolder) && Files.isDirectory(internalFolder)) {
					// Walk through all files in the internal folder
					Files.walk(internalFolder)
							.filter(Files::isRegularFile)
							.forEach(sourceFile -> {
								try {
									// Get the relative path from the internal folder
									Path relativePath = internalFolder.relativize(sourceFile);
									Path targetFile = configDir.resolve(relativePath.toString());

									// Create parent directories if they don't exist
									Files.createDirectories(targetFile.getParent());

									// Copy the file, replacing if it exists
									try (InputStream in = Files.newInputStream(sourceFile)) {
										Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
										System.out.println("Copied " + relativePath.getFileName() + " to config folder.");
									}
								} catch (IOException e) {
									LOGGER.error("Failed to copy file: " + sourceFile.getFileName(), e);
								}
							});

					System.out.println("Successfully copied all files from " + internalFolderPath);
				} else {
					System.err.println("Internal folder not found: " + internalFolderPath);
				}
			}

		} catch (IOException | URISyntaxException e) {
			LOGGER.error("Error copying files from jar", e);
		}
	}
}
