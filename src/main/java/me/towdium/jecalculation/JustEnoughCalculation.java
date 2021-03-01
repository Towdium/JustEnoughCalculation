package me.towdium.jecalculation;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import me.towdium.jecalculation.network.IProxy;
import me.towdium.jecalculation.network.packets.PacketCalculatorUpdate;
import me.towdium.jecalculation.network.packets.PacketRecipeUpdate;
import me.towdium.jecalculation.network.packets.PacketSyncRecord;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Towdium
 */

@Mod(modid = JustEnoughCalculation.Reference.MODID,
     name = JustEnoughCalculation.Reference.MODNAME,
     version = JustEnoughCalculation.Reference.VERSION,
     dependencies = "required-after:NotEnoughItems")
public class JustEnoughCalculation {
    @Mod.Instance(Reference.MODID)
    public static JustEnoughCalculation instance;

    @SidedProxy(modId = Reference.MODID,
                clientSide = "me.towdium.jecalculation.network.ProxyClient",
                serverSide = "me.towdium.jecalculation.network.ProxyServer")
    public static IProxy proxy;

    public static SimpleNetworkWrapper network;
    public static Logger logger = LogManager.getLogger(Reference.MODID);

    @NetworkCheckHandler
    public static boolean networkCheck(Map<String, String> mods, Side s) {
        return s == Side.SERVER || mods.containsKey(Reference.MODID);
    }

    @Mod.EventHandler
    public static void initPre(FMLPreInitializationEvent event) {
        JecaConfig.preInit(event);
        GameRegistry.registerItem(JecaItem.INSTANCE, JecaItem.NAME);
        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        network.registerMessage(PacketCalculatorUpdate.class, PacketCalculatorUpdate.class, 1, Side.SERVER);
        network.registerMessage(PacketRecipeUpdate.class, PacketRecipeUpdate.class, 2, Side.SERVER);
        network.registerMessage(PacketSyncRecord.class, PacketSyncRecord.class, 3, Side.CLIENT);
        network.registerMessage(PacketSyncRecord.class, PacketSyncRecord.class, 4, Side.SERVER);
    }

    @Mod.EventHandler
    public static void iniFt(FMLInitializationEvent event) {
        /*if(event.getSide().isClient()){
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
                    register(itemCalculator, 0, new ModelResourceLocation(Reference.MODID + ":" + itemCalculator.getUnlocalizedName().substring(5), "inventory"));
        }*/
        ShapedOreRecipe recipe = new ShapedOreRecipe(new ItemStack(JecaItem.INSTANCE), "SIS", "SRS", "SOS", 'S',
                                                     "stone", 'I', "dyeBlack", 'R', "dustRedstone", 'O', "ingotIron") {
            {
                try {
                    Field f = ShapedOreRecipe.class.getDeclaredField("input");
                    f.setAccessible(true);
                    Object[] o = (Object[]) f.get(this);
                    if (o[1] instanceof ArrayList) {
                        ArrayList<ItemStack> buffer = (ArrayList<ItemStack>) o[1];
                        ArrayList<ItemStack> a = new ArrayList<ItemStack>(buffer);
                        Item i = GameRegistry.findItem("Botania", "dye");
                        if (i != null)
                            a.add(new ItemStack(i, 1, 15));
                        o[1] = a;
                    }
                } catch (Exception ignored) {
                }
            }
        };
        GameRegistry.addRecipe(recipe);
        proxy.init();

        //DefaultOverlayHandler
    }

    public static class Reference {
        public static final String MODID = "jecalculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "@VERSION@";
    }
}
