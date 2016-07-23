package pers.towdium.just_enough_calculation.network;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.event.ModelBakeEventHandler;
import pers.towdium.just_enough_calculation.event.MouseEventHandler;

/**
 * @author Towdium
 */

@SideOnly(Side.CLIENT)
public class ProxyClient implements IProxy {

    static void setModelLocation(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(JustEnoughCalculation.Reference.MODID + ":" +
                        item.getUnlocalizedName().substring(5), "inventory"));
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new MouseEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModelBakeEventHandler());
    }

    @Override
    public void preInit() {
        setModelLocation(JustEnoughCalculation.itemCalculator);
        setModelLocation(JustEnoughCalculation.itemFluidContainer);
    }
}
