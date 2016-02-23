package pers.towdium.tudicraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
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
import pers.towdium.tudicraft.event.GuiEventHandler;
import pers.towdium.tudicraft.event.PlayerEventHandler;
import pers.towdium.tudicraft.gui.GuiHandler;
import pers.towdium.tudicraft.item.ItemCalculator;
import pers.towdium.tudicraft.network.IProxy;
import pers.towdium.tudicraft.network.packages.PackageCalculatorUpdate;
import pers.towdium.tudicraft.network.packages.PackageRecipeUpdate;
import pers.towdium.tudicraft.network.packages.PackageSlotUpdate;

/**
 * @author Towdium
 */

@Mod(modid = Tudicraft.Reference.MODID, name = Tudicraft.Reference.MODNAME, version = Tudicraft.Reference.VERSION)
public class Tudicraft {
    public static Item itemCalculator = new ItemCalculator().setUnlocalizedName("itemCalculator");
    public static SimpleNetworkWrapper networkWrapper;
    public static Logger log = LogManager.getLogger(Reference.MODID);

    @SidedProxy(clientSide = "pers.towdium.tudicraft.network.ProxyClient", serverSide = "pers.towdium.tudicraft.network.ProxyServer")
    public static IProxy proxy;

    @Mod.Instance(Tudicraft.Reference.MODID)
    public static Tudicraft instance;

    public static class Reference {
        public static final String MODID = "tudicraft";
        public static final String MODNAME = "Tudicraft";
        public static final String VERSION = "0.0.1";
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event){
        GameRegistry.registerItem(itemCalculator,itemCalculator.getUnlocalizedName().substring(5));
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        networkWrapper.registerMessage(PackageSlotUpdate.class, PackageSlotUpdate.class, 0, Side.SERVER);
        networkWrapper.registerMessage(PackageCalculatorUpdate.class, PackageCalculatorUpdate.class, 1, Side.SERVER);
        networkWrapper.registerMessage(PackageRecipeUpdate.class, PackageRecipeUpdate.class, 2, Side.SERVER);
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
