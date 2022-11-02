// Copyright AyeTSG 2022.

package io.github.ayetsg.modulardiscs.item;

import java.io.InputStream;

import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

public class GeneratedMusicDiscItem extends MusicDiscItem {

    public Text GENERATED_DISC_NAME = Text.literal("Generated Music Disc");
    public InputStream GENERATED_DISC_OGG;

    public GeneratedMusicDiscItem(String discName, InputStream discOgg, int comparatorOutput, SoundEvent sound, Settings settings, int lengthInSeconds) {
        super(comparatorOutput, sound, settings, lengthInSeconds);

        GENERATED_DISC_NAME = Text.literal(discName);
        GENERATED_DISC_OGG = discOgg;
    }
    
}
