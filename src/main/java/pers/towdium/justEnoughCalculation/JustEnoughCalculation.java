package pers.towdium.justEnoughCalculation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.towdium.justEnoughCalculation.item.ItemCalculator;
import pers.towdium.justEnoughCalculation.network.IProxy;
import pers.towdium.justEnoughCalculation.network.packets.PacketCalculatorUpdate;
import pers.towdium.justEnoughCalculation.network.packets.PacketRecipeUpdate;
import pers.towdium.justEnoughCalculation.network.packets.PacketSlotUpdate;
import pers.towdium.justEnoughCalculation.network.packets.PacketSyncRecord;

/**
 * @author Towdium
 */

@Mod(modid = JustEnoughCalculation.Reference.MODID, name = JustEnoughCalculation.Reference.MODNAME, version = JustEnoughCalculation.Reference.VERSION)
public class JustEnoughCalculation {
    public static Item itemCalculator = new ItemCalculator().setUnlocalizedName("itemCalculator");
    public static SimpleNetworkWrapper networkWrapper;
    public static Logger log = LogManager.getLogger(Reference.MODID);

    @SidedProxy(clientSide = "pers.towdium.justEnoughCalculation.network.ProxyClient", serverSide = "pers.towdium.justEnoughCalculation.network.ProxyServer")
    public static IProxy proxy;

    @Mod.Instance(JustEnoughCalculation.Reference.MODID)
    public static JustEnoughCalculation instance;

    public static class Reference {
        public static final String MODID = "je_calculation";
        public static final String MODNAME = "JustEnoughCalculation";
        public static final String VERSION = "0.0.1";
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event){
        GameRegistry.registerItem(itemCalculator,itemCalculator.getUnlocalizedName().substring(5));
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        networkWrapper.registerMessage(PacketSlotUpdate.class, PacketSlotUpdate.class, 0, Side.SERVER);
        networkWrapper.registerMessage(PacketCalculatorUpdate.class, PacketCalculatorUpdate.class, 1, Side.SERVER);
        networkWrapper.registerMessage(PacketRecipeUpdate.class, PacketRecipeUpdate.class, 2, Side.SERVER);
        networkWrapper.registerMessage(PacketSyncRecord.class, PacketSyncRecord.class, 3, Side.CLIENT);
        networkWrapper.registerMessage(PacketSyncRecord.class, PacketSyncRecord.class, 4, Side.SERVER);
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event){
        if(event.getSide().isClient()){
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
                    register(itemCalculator, 0, new ModelResourceLocation(Reference.MODID + ":" + itemCalculator.getUnlocalizedName().substring(5), "inventory"));
        }
        proxy.init();
    }
}
