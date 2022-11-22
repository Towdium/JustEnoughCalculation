package me.towdium.jecalculation.forge;

import me.shedaniel.rei.forge.REIPluginClient;
import me.towdium.jecalculation.compat.rei.JecaREIPlugin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@REIPluginClient
public class JecaREIPluginForge extends JecaREIPlugin {
}