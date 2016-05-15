package pers.towdium.justEnoughCalculation;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pers.towdium.justEnoughCalculation.item.ItemCalculator;
import pers.towdium.justEnoughCalculation.network.IProxy;
import pers.towdium.justEnoughCalculation.network.packets.PacketCalculatorUpdate;
import pers.towdium.justEnoughCalculation.network.packets.PacketRecipeUpdate;
import pers.towdium.justEnoughCalculation.network.packets.PacketSyncRecord;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author Towdium
 */

@Mod(modid = JustEnoughCalculation.Reference.MODID, name = JustEnoughCalculation.Reference.MODNAME, version = JustEnoughCalculation.Reference.VERSION,
        dependencies = "after:NotEnoughItems")
public class JustEnoughCalculation{
    public static Item itemCalculator = new ItemCalculator().setUnlocalizedName("itemCalculator").setTextureName(Reference.MODID + ":" + "itemCalculator");
    public static SimpleNetworkWrapper networkWrapper;
    public static Logger log = LogManager.getLogger(Reference.MODID);

    @SidedProxy(clientSide = "pers.towdium.justEnoughCalculation.network.ProxyClient", serverSide = "pers.towdium.justEnoughCalculation.network.ProxyServer")
    public static IProxy proxy;

    @Mod.Instance(JustEnoughCalculation.Reference.MODID)
    public static JustEnoughCalculation instance;

    public static class Reference {
        public static final String MODID = "je_calculation";
        public static final String MODNAME = "Just Enough Calculation";
        public static final String VERSION = "0.3.3";
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event){
        JECConfig.preInit(event);
        GameRegistry.registerItem(itemCalculator,itemCalculator.getUnlocalizedName().substring(5));
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        networkWrapper.registerMessage(PacketCalculatorUpdate.class, PacketCalculatorUpdate.class, 1, Side.SERVER);
        networkWrapper.registerMessage(PacketRecipeUpdate.class, PacketRecipeUpdate.class, 2, Side.SERVER);
        networkWrapper.registerMessage(PacketSyncRecord.class, PacketSyncRecord.class, 3, Side.CLIENT);
        networkWrapper.registerMessage(PacketSyncRecord.class, PacketSyncRecord.class, 4, Side.SERVER);
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event){
        /*if(event.getSide().isClient()){
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
                    register(itemCalculator, 0, new ModelResourceLocation(Reference.MODID + ":" + itemCalculator.getUnlocalizedName().substring(5), "inventory"));
        }*/
        ShapedOreRecipe recipe = new ShapedOreRecipe(new ItemStack(itemCalculator), "SIS", "SRS", "SOS", 'S', "stone", 'I', "dyeBlack", 'R', "dustRedstone", 'O', "ingotIron"){
            {
                try {
                    Field f = ShapedOreRecipe.class.getDeclaredField("input");
                    f.setAccessible(true);
                    Object[] o = (Object[]) f.get(this);
                    if(o[1] instanceof ArrayList){
                        ArrayList<ItemStack> buffer = (ArrayList<ItemStack>) o[1];
                        ArrayList<ItemStack> a = new ArrayList<ItemStack>(buffer);
                        Item i = GameRegistry.findItem("Botania", "dye");
                        a.add(new ItemStack(i,1,15));
                        o[1] = a;
                    }
                } catch (Exception ignored) {}
            }
        };
        GameRegistry.addRecipe(recipe);
        proxy.init();

        //DefaultOverlayHandler
    }

    public static class JECConfig{
        public static boolean initialized = false;
        public static Configuration config;
        public enum EnumItems {
            EnableInventoryCheck,
            ListRecipeBlackList,
            ListRecipeCategory;

            public String getComment(){
                switch (this){
                    case EnableInventoryCheck:
                        return "Set to false to disable auto inventory check";
                    case ListRecipeBlackList:
                        return "Add string identifier here to disable quick transfer of this type recipe\n" +
                                "Names can be found in ListRecipeCategory";
                    case ListRecipeCategory:
                        return "List of categories, this is maintained by the mod automatically";
                }
                return "";
            }

            public String getName(){
                switch (this){
                    case EnableInventoryCheck:
                        return "EnableInventoryCheck";
                    case ListRecipeBlackList:
                        return "ListRecipeBlackList";
                    case ListRecipeCategory:
                        return "ListRecipeCategory";
                }
                return "";
            }

            public String getCategory(){
                switch (this){
                    case EnableInventoryCheck:
                        return EnumCategory.General.toString();
                    case ListRecipeBlackList:
                        return EnumCategory.General.toString();
                    case ListRecipeCategory:
                        return EnumCategory.General.toString();
                }
                return "";
            }

            public EnumType getType(){
                switch (this){
                    case EnableInventoryCheck:
                        return EnumType.Boolean;
                    case ListRecipeBlackList:
                        return EnumType.ListString;
                    case ListRecipeCategory:
                        return EnumType.ListString;
                }
                return EnumType.Error;
            }

            public Object getDefault(){
                switch (this){
                    case EnableInventoryCheck:
                        return true;
                    case ListRecipeBlackList:
                        return new String[0];
                    case ListRecipeCategory:
                        return new String[]{"crafting", "smelting"};
                }
                return JECConfig.empty;
            }

            public Property init(){
                EnumType type = this.getType();
                if(type != null){
                    switch (this.getType()){
                        case Boolean:
                            return config.get(this.getCategory(), this.getName(), (Boolean) this.getDefault(), this.getComment());
                        case ListString:
                            return config.get(this.getCategory(), this.getName(), (String[]) this.getDefault(), this.getComment());
                    }
                    config.getCategory(EnumCategory.General.toString()).get(this.getName());
                }
                return config.get(this.getCategory(), this.getName(), false, this.getComment());
            }

            public Property getProperty(){
                return config.getCategory(EnumCategory.General.toString()).get(this.getName());
            }
        }

        public enum EnumCategory {
            General;

            @Override
            public String toString() {
                switch (this){
                    case General:
                        return "general";
                    default:
                        return "";
                }
            }
        }

        public enum EnumType { Boolean, ListString, Error }

        public static Object empty;

        public static void preInit(FMLPreInitializationEvent event){
            config = new Configuration(new File(event.getModConfigurationDirectory(), "JustEnoughCalculation" +".cfg"), Reference.VERSION);
            config.load();
            handleFormerVersion();
            handleInit();
            config.save();
        }

        public static void handleFormerVersion(){
            config.getCategory("general").remove("RecipeTypeSupport");
        }

        public static void handleInit(){
            for(EnumItems item : EnumItems.values()){
                item.init();
            }
        }

        public static void save(){
            config.save();
        }
    }
}
