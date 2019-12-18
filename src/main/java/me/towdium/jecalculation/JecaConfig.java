package me.towdium.jecalculation;

import net.minecraftforge.common.ForgeConfigSpec;

public class JecaConfig {
    public static final String PATH = "jecalculation/config.toml";
    public static ForgeConfigSpec common;
    public static ForgeConfigSpec.BooleanValue bClientMode;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("General");
        bClientMode = b.define("bClientMode", false);
        b.pop();
        common = b.build();
    }
}
