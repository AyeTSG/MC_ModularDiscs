package io.github.ayetsg.modulardiscs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.ResourcePackProfile.InsertionPosition;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModularDiscsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("tsg_modulardiscs");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// get the .minecraft folder
		Path MinecraftDir = FabricLoader.getInstance().getGameDir();

		// create a variable for the .discs/ folder
		Path DiscsDir = Paths.get(MinecraftDir.toString(), ".discs");

		// create the .discs/ folder, if it doesn't already exist
		try {
			Files.createDirectory(DiscsDir);
		} catch (IOException e) {
			LOGGER.error("Couldn't make .discs/ directory!");
			LOGGER.error(e.toString());
		}
	}
}
