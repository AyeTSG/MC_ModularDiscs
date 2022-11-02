// Copyright AyeTSG 2022.

package io.github.ayetsg.modulardiscs;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.ayetsg.modulardiscs.item.GeneratedMusicDiscItem;

public class ModularDiscsMod implements ModInitializer {
	// setup the logger
	public static final Logger LOGGER = LoggerFactory.getLogger("tsg_modulardiscs");

	// setup the runtime resources
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("tsg_modulardiscs:runtime");
	public static final JLang FINAL_LANG = new JLang();

	@Override
	public void onInitialize() {
		// get the .minecraft folder
		Path MinecraftDir = FabricLoader.getInstance().getGameDir();

		// create a variable for the discpacks folder
		Path DiscsDir = Paths.get(MinecraftDir.toString(), "discpacks");

		// create the discpacks folder, if it doesn't already exist
		try {
			Files.createDirectory(DiscsDir);
		} catch (IOException e) {
			LOGGER.error("Couldn't make /discpacks/ directory!");
			LOGGER.error(e.toString());
		}

		// loop over all files in the discpacks folder
		File[] DiscDirListing = DiscsDir.toFile().listFiles();
		if (DiscDirListing != null) {
			for (File DiscPackFile : DiscDirListing) {
				if (DiscPackFile.isFile()) {
					// attempt to parse the zip
					LOGGER.info("Attempting to parse " + DiscPackFile.getName());

					// create a zip file
					try {
						ZipFile tempZip = new ZipFile(DiscPackFile.getAbsoluteFile());

						// attempt to get the disc info
						InputStream discInfo = tempZip.getInputStream(tempZip.getEntry("disc.json"));
						String discInfoStr = IOUtils.toString(discInfo, StandardCharsets.UTF_8);
						JsonObject discInfoJson = new JsonParser().parse(discInfoStr).getAsJsonObject();
						String discId = discInfoJson.get("id").getAsString();
						String discName = discInfoJson.get("name").getAsString();

						// create the resources - lang
						FINAL_LANG.entry("item.tsg_modulardiscs." + discId, "Music Disc");
						FINAL_LANG.entry("item.tsg_modulardiscs." + discId + ".desc", discName);

						// create the item
						final Item GENERATED_DISC = new GeneratedMusicDiscItem(0, SoundEvents.MUSIC_DISC_STAL, new FabricItemSettings().group(ItemGroup.MISC), 0);
						Registry.register(Registry.ITEM, new Identifier("tsg_modulardiscs", discId), GENERATED_DISC);
					} catch (IOException e) {
						LOGGER.error("Couldn't open ZIP!");
						LOGGER.error(e.toString());
					}
				}
			}
		}

		// register the resource pack
		RESOURCE_PACK.addLang(new Identifier("tsg_modulardiscs:en_us"), FINAL_LANG);
		RRPCallback.BEFORE_VANILLA.register(a -> a.add(RESOURCE_PACK));
	}
}
