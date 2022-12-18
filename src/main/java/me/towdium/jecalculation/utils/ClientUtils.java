package me.towdium.jecalculation.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ClientUtils {
    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    public static void playClickSound(float pitchIn) {
        try {
            mc().getSoundHandler()
                    .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), pitchIn));
        } catch (Exception e) {
            // why may crash ??
            e.printStackTrace();
        }
    }
}
