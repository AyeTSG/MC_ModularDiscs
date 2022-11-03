// Copyright AyeTSG 2022.

package io.github.ayetsg.modulardiscs;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.models.JTextures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.ayetsg.modulardiscs.item.GeneratedMusicDiscItem;

public class ModularDiscsMod implements ModInitializer {
	// setup the logger
	public static final Logger LOGGER = LoggerFactory.getLogger("tsg_modulardiscs");

	// setup the internal tab render block
	public static final Block TAB_ICON_BLOCK = new Block(FabricBlockSettings.of(Material.WOOD));

	// setup the item tab
	public static final ItemGroup GENERATED_DISC_GROUP = FabricItemGroupBuilder.build(
		new Identifier("tsg_modulardiscs", "generated_discs"),
		() -> new ItemStack(TAB_ICON_BLOCK));

	// setup the runtime resources
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create("tsg_modulardiscs:runtime");
	public static final JLang FINAL_LANG = new JLang();
	public static final JsonObject FINAL_SOUND_JSON = new JsonObject();

	@Override
	public void onInitialize() {
		// register the internal tab render block
		Registry.register(Registry.BLOCK, new Identifier("tsg_modulardiscs", "tab_icon_block"), TAB_ICON_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("tsg_modulardiscs", "tab_icon_block"), new BlockItem(TAB_ICON_BLOCK, new FabricItemSettings()));

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

						// create the resources - models
						ArrayList<String> fileNames = new ArrayList<>();
						for (ZipEntry entry : Collections.list(tempZip.entries())) {
							fileNames.add(entry.getName());
						}
						if (fileNames.contains("disc.png")) {
							// add the texture
							InputStream discTex = tempZip.getInputStream(tempZip.getEntry("disc.png"));

							RESOURCE_PACK.addAsset(new Identifier("tsg_modulardiscs", "textures/" + discId + ".png"), discTex.readAllBytes());
							RESOURCE_PACK.addModel(new JModel().parent("minecraft:item/generated").textures(new JTextures().layer0("tsg_modulardiscs:" + discId)), new Identifier("tsg_modulardiscs", "item/" + discId));
						} else {
							// if we don't have a disc.png, use stal's disc texture
							RESOURCE_PACK.addModel(new JModel().parent("minecraft:item/generated").textures(new JTextures().layer0("minecraft:item/music_disc_stal")), new Identifier("tsg_modulardiscs", "item/" + discId));
						}

						// create the resources - sound
						RESOURCE_PACK.addAsset(new Identifier("tsg_modulardiscs", "sounds/" + discId + ".ogg"), tempZip.getInputStream(tempZip.getEntry("disc.ogg")).readAllBytes());

						// create the resources - sound event
						JsonObject soundData = new JsonObject();
						JsonArray soundsList = new JsonArray();
						JsonObject soundPointer = new JsonObject();
						soundPointer.addProperty("name", "tsg_modulardiscs:" + discId);
						soundPointer.addProperty("stream", true);
						soundsList.add(soundPointer);
						soundData.add("sounds", soundsList);
						FINAL_SOUND_JSON.add(discId, soundData);

						// register the sound event with fabric
						final Identifier GENERATED_SOUND_EVENT_ID = new Identifier("tsg_modulardiscs", discId);
						final SoundEvent GENERATED_SOUND_EVENT = new SoundEvent(GENERATED_SOUND_EVENT_ID);

						// create the item
						final Item GENERATED_DISC = new GeneratedMusicDiscItem(0, GENERATED_SOUND_EVENT, new FabricItemSettings().group(GENERATED_DISC_GROUP).rarity(Rarity.RARE).maxCount(1), 0);
						Registry.register(Registry.ITEM, new Identifier("tsg_modulardiscs", discId), GENERATED_DISC);
					} catch (IOException e) {
						LOGGER.error("Couldn't open ZIP!");
						LOGGER.error(e.toString());
					}
				}
			}
		}

		// LOGGER.info(FINAL_SOUND_JSON.toString());

		// register the resource pack
		RESOURCE_PACK.addLang(new Identifier("tsg_modulardiscs:en_us"), FINAL_LANG);

		try {
			RESOURCE_PACK.addAsset(new Identifier("tsg_modulardiscs", "sounds.json"), FINAL_SOUND_JSON.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Unsupported encoding!");
			LOGGER.error(e.toString());
		}

		// RESOURCE_PACK.dumpDirect(Paths.get(MinecraftDir.toString(), "DEBUG"));

		RRPCallback.AFTER_VANILLA.register(a -> a.add(RESOURCE_PACK));
	}

}
