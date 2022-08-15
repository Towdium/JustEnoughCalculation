package me.towdium.jecalculation;

import net.minecraftforge.common.ForgeConfigSpec;

public class JecaConfig {
    public static final String PATH = "jecalculation/config.toml";
    public static ForgeConfigSpec common;
    public static ForgeConfigSpec.BooleanValue clientMode;
    public static ForgeConfigSpec.BooleanValue useOldLabelButtons;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("General");
        clientMode = b.define("clientMode", false);
        useOldLabelButtons = b.define("useOldLabelButtons", false);
        b.pop();
        common = b.build();
    }
}
