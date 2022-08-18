package me.towdium.jecalculation.forge;

import net.minecraftforge.common.ForgeConfigSpec;

public class JecaConfig {
    public static final String PATH = "jecalculation/config.toml";
    public static ForgeConfigSpec common;
    public static ForgeConfigSpec.BooleanValue clientMode;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("General");
        clientMode = b.define("clientMode", false);
        b.pop();
        common = b.build();
    }
}
