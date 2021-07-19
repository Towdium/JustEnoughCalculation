package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class GregTech implements IAdapter {
    @Override
    public Set<String> getAllOverlayIdentifier() {
        return reflectGetRecipeMapNEIName("gregtech.api.util.GT_Recipe$GT_Recipe_Map", "sMappings");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Set<String> reflectGetRecipeMapNEIName(String clz, String staticField) {
        try {
            Class<?> gtRecipeMapClz = Class.forName(clz);
            Field sMappingsField = gtRecipeMapClz.getDeclaredField(staticField);
            Field mNEINameField = gtRecipeMapClz.getField("mNEIName");
            Collection sMappings = (Collection) sMappingsField.get(null);

            return (Set<String>) sMappings.stream().map(sMapping -> {
                try {
                    return mNEINameField.get(sMapping);
                } catch (IllegalAccessException e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toSet());
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    private static final Class<?> gtDefault, gtAssLine;

    static {
        Class<?> gtDf = null;
        Class<?> gtAL = null;
        try {
            gtDf = Class.forName("gregtech.nei.GT_NEI_DefaultHandler");
            gtAL = Class.forName("gregtech.nei.GT_NEI_AssLineHandler");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        gtDefault = gtDf;
        gtAssLine = gtAL;
    }

    @Override
    public void handleRecipe(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        if (gtDefault.isInstance(recipe) || gtAssLine.isInstance(recipe)) {
            handleDefault(recipe, index, inputs, outputs);
        }
    }

    protected void handleDefault(IRecipeHandler recipe, int index, List<Object[]> inputs, List<Object[]> outputs) {
        for (int i = 0; i < inputs.size(); i++) {
            Object[] objects = Arrays.stream(inputs.get(i)).map(o -> GregTech.convertFluid((ItemStack) o)).toArray();
            inputs.set(i, objects);
        }
        List<PositionedStack> otherStacks = recipe.getOtherStacks(index);
        outputs.addAll(otherStacks.stream()
                                  .map(positionedStack -> positionedStack.items)
                                  .map(itemStacks -> Arrays.stream(itemStacks).map(GregTech::convertFluid).toArray())
                                  .collect(Collectors.toList()));
    }

    public static Object convertFluid(ItemStack itemStack) {
        FluidStack fluidStack = getFluidFromDisplayStack(itemStack);
        return fluidStack == null ? itemStack : fluidStack;
    }

    /**
     * For resolving version compatibility issues.
     * Copied from GTNewHorizons/GT5-Unofficial.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static FluidStack getFluidFromDisplayStack(ItemStack aDisplayStack) {
        try {
            Class itemListClz = Class.forName("gregtech.api.enums.ItemList");
            Enum display_fluid = Enum.valueOf(itemListClz, "Display_Fluid");
            Method getItem = itemListClz.getMethod("getItem");
            Item displayFluidItem = (Item) getItem.invoke(display_fluid);
            if (!isStackValid(aDisplayStack) || aDisplayStack.getItem() != displayFluidItem ||
                !aDisplayStack.hasTagCompound())
                return null;
            Fluid tFluid = FluidRegistry.getFluid(displayFluidItem.getDamage(aDisplayStack));
            return new FluidStack(tFluid, (int) aDisplayStack.getTagCompound().getLong("mFluidDisplayAmount"));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isStackValid(Object aStack) {
        return (aStack instanceof ItemStack) && ((ItemStack) aStack).getItem() != null &&
               ((ItemStack) aStack).stackSize >= 0;
    }
}
