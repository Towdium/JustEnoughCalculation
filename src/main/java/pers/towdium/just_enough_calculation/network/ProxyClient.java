package pers.towdium.just_enough_calculation.network;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.event.MouseEventHandler;

/**
 * @author Towdium
 */

@SideOnly(Side.CLIENT)
public class ProxyClient implements IProxy {
    @Override
    public void preInit() {
        ModelLoader.setCustomModelResourceLocation(JustEnoughCalculation.itemCalculator, 0,
                new ModelResourceLocation(JustEnoughCalculation.Reference.MODID + ":" +
                        JustEnoughCalculation.itemCalculator.getUnlocalizedName().substring(5), "inventory"));
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new MouseEventHandler());
    }
}
