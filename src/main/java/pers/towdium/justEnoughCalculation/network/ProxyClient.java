package pers.towdium.justEnoughCalculation.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.event.MouseEventHandler;
import pers.towdium.justEnoughCalculation.gui.GuiHandler;

/**
 * @author Towdium
 */

@SideOnly(Side.CLIENT)
public class ProxyClient implements IProxy {
    @Override
    public void preInit() {
            ModelLoader.setCustomModelResourceLocation(JustEnoughCalculation.itemCalculator, 0, new ModelResourceLocation(JustEnoughCalculation.Reference.MODID + ":" + JustEnoughCalculation.itemCalculator.getUnlocalizedName().substring(5), "inventory"));
        }

    @Override
    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(JustEnoughCalculation.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new MouseEventHandler());
    }
}
