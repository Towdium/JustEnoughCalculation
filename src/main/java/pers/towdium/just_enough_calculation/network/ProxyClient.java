package pers.towdium.just_enough_calculation.network;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.event.DataEventHandler;
import pers.towdium.just_enough_calculation.event.InputEventHandler;
import pers.towdium.just_enough_calculation.event.ModelEventHandler;
import pers.towdium.just_enough_calculation.event.TooltipEventHandler;
import pers.towdium.just_enough_calculation.gui.JECGuiHandler;

/**
 * @author Towdium
 */

@SideOnly(Side.CLIENT)
public class ProxyClient implements IProxy {
    static PlayerHandlerSP playerHandler = new PlayerHandlerSP();

    static void setModelLocation(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(JustEnoughCalculation.Reference.MODID + ":" +
                        id, "inventory"));
    }

    static void setModelLocation(Item item, int meta) {
        setModelLocation(item, meta, item.getUnlocalizedName().substring(5));
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new InputEventHandler());
        MinecraftForge.EVENT_BUS.register(new ModelEventHandler());
        MinecraftForge.EVENT_BUS.register(new DataEventHandler());
        MinecraftForge.EVENT_BUS.register(new TooltipEventHandler());
        NetworkRegistry.INSTANCE.registerGuiHandler(JustEnoughCalculation.instance, new JECGuiHandler());
    }

    @Override
    public void preInit() {
        setModelLocation(JustEnoughCalculation.itemCalculator, 0);
        setModelLocation(JustEnoughCalculation.itemFluidContainer, 0);
        setModelLocation(JustEnoughCalculation.itemCalculator, 1, "itemMathCalculator");
    }

    @Override
    public IPlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
