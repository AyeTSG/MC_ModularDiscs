package io.github.ayetsg.modulardiscs.mixin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.ayetsg.modulardiscs.ModularDiscsMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.ResourcePackProfile.InsertionPosition;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class AddDiscPackData {
    @Inject(at = @At("HEAD"), method = "init()V", allow = 1)
    private void init(CallbackInfo info)
    {
        Path MinecraftDir = FabricLoader.getInstance().getGameDir();
        Path DiscsDir = Paths.get(MinecraftDir.toString(), ".discs");

        // loop over all files in .discs/
		File[] DiscPacks = DiscsDir.toFile().listFiles();
		for (int i = 0; i < DiscPacks.length; i++) {
			if (DiscPacks[i].isFile()) {
				// create a resource pack from it, and mount it
				try (ZipResourcePack DiscResourcePack = new ZipResourcePack(DiscPacks[i])) {
					PackResourceMetadata DiscResourcePackMeta = DiscResourcePack.parseMetadata(PackResourceMetadata.READER);

					ResourcePackProfile a = new ResourcePackProfile("GeneratedDiscResource", Text.literal("Generated Disc Resource"), true, () -> DiscResourcePack, DiscResourcePackMeta, ResourceType.SERVER_DATA, InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_NONE);
				} catch (IOException e) {
					ModularDiscsMod.LOGGER.error("Couldn't create resource pack from file!");
					ModularDiscsMod.LOGGER.error(e.toString());
				};
			}
		}

        MinecraftClient.getInstance().reloadResourcesConcurrently();
    }
}
