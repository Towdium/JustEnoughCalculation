package pers.towdium.just_enough_calculation;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Author:  Towdium
 * Created: 2016/6/14.
 */

public class JECConfig {
    public static Configuration config;
    public static Object empty;

    public static void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "JustEnoughCalculation" + ".cfg"), JustEnoughCalculation.Reference.VERSION);
        config.load();
        handleFormerVersion();
        handleInit();
        config.save();
    }

    public static void handleFormerVersion() {
    }

    public static void handleInit() {
        for (EnumItems item : EnumItems.values()) {
            item.init();
        }
    }

    public static void save() {
        config.save();
    }

    public enum EnumItems {
        EnableInventoryCheck,
        EnableFluidMerge,
        ListRecipeBlackList,
        ListRecipeCategory;

        public String getComment() {
            switch (this) {
                case EnableInventoryCheck:
                    return "Set to false to disable auto inventory check";
                case EnableFluidMerge:
                    return "Set to true to enable fluid merging when quick transfer recipes";
                case ListRecipeBlackList:
                    return "Add string identifier here to disable quick transfer of certain categories of recipe\n" +
                            "Names can be found in ListRecipeCategory";
                case ListRecipeCategory:
                    return "List of recipe categories for reference, this is maintained by the mod automatically\n" +
                            "Editing the text here will have no effect";
            }
            return "";
        }

        public String getCategory() {
            switch (this) {
                case EnableInventoryCheck:
                    return EnumCategory.General.toString();
                case EnableFluidMerge:
                    return EnumCategory.General.toString();
                case ListRecipeBlackList:
                    return EnumCategory.General.toString();
                case ListRecipeCategory:
                    return EnumCategory.General.toString();
            }
            return "";
        }

        public EnumType getType() {
            switch (this) {
                case EnableInventoryCheck:
                    return EnumType.Boolean;
                case EnableFluidMerge:
                    return EnumType.Boolean;
                case ListRecipeBlackList:
                    return EnumType.ListString;
                case ListRecipeCategory:
                    return EnumType.ListString;
            }
            return EnumType.Error;
        }

        public Object getDefault() {
            switch (this) {
                case EnableInventoryCheck:
                    return true;
                case EnableFluidMerge:
                    return false;
                case ListRecipeBlackList:
                    return new String[0];
                case ListRecipeCategory:
                    return new String[]{"minecraft.crafting", "minecraft.smelting"};
            }
            return JECConfig.empty;
        }

        public Property init() {
            EnumType type = this.getType();
            if (type != null) {
                switch (this.getType()) {
                    case Boolean:
                        return config.get(this.getCategory(), this.toString(), (Boolean) this.getDefault(), this.getComment());
                    case ListString:
                        return config.get(this.getCategory(), this.toString(), (String[]) this.getDefault(), this.getComment());
                }
                config.getCategory(EnumCategory.General.toString()).get(this.toString());
            }
            return config.get(this.getCategory(), this.toString(), false, this.getComment());
        }

        public Property getProperty() {
            return config.getCategory(EnumCategory.General.toString()).get(this.toString());
        }
    }

    public enum EnumCategory {
        General;

        @Override
        public String toString() {
            switch (this) {
                case General:
                    return "general";
                default:
                    return "";
            }
        }
    }

    public enum EnumType {Boolean, ListString, Error}
}

