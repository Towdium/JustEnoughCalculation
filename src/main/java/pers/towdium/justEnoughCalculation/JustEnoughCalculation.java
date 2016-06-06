package pers.towdium.justEnoughCalculation;

import net.minecraft.client.Minecraft;
import  net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.towdium.justEnoughCalculation.item.ItemCalculator;

import java.io.File;

/**
 * @author Towdium
 */

@Mod(modid = JustEnoughCalculation.Reference.MODID, name = JustEnoughCalculation.Reference.MODNAME, version = JustEnoughCalculation.Reference.VERSION,
        dependencies = "required-after:JEI")
public class JustEnoughCalculation {
    public static Item itemCalculator = new ItemCalculator().setUnlocalizedName("itemCalculator").setRegistryName("itemCalculator");
    public static SimpleNetworkWrapper networkWrapper;
    public static Logger log = LogManager.getLogger(Reference.MODID);

    @Mod.Instance(JustEnoughCalculation.Reference.MODID)
    public static JustEnoughCalculation instance;

    public static class Reference {
        public static final String MODID = "je_calculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "1.9.4-0.0.1";
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        GameRegistry.register(itemCalculator);
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        if (event.getSide().isClient()) {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
                    register(itemCalculator, 0, new ModelResourceLocation(Reference.MODID + ":" + itemCalculator.getUnlocalizedName().substring(5), "inventory"));
        }
    }
}
