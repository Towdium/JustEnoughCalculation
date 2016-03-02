package pers.towdium.justEnoughCalculation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import pers.towdium.justEnoughCalculation.network.packets.PacketSyncRecord;

/**
 * @author Towdium
 */

@Mod(modid = JustEnoughCalculation.Reference.MODID, name = JustEnoughCalculation.Reference.MODNAME, version = JustEnoughCalculation.Reference.VERSION,
        dependencies = "required-after:JEI@[1.8.9-2.28.2.166,)")
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
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "0.2.0";
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event){
        GameRegistry.registerItem(itemCalculator,itemCalculator.getUnlocalizedName().substring(5));
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
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
        GameRegistry.addRecipe(new ItemStack(itemCalculator), "SIS", "SRS", "SRS", 'S', new ItemStack(Blocks.stone, 1, 0), 'I', new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()), 'R', Items.redstone);
        proxy.init();
    }
}
