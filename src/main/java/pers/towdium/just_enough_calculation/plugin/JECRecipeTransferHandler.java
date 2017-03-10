package pers.towdium.just_enough_calculation.plugin;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import pers.towdium.just_enough_calculation.JECConfig;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.gui.guis.GuiEditor;
import pers.towdium.just_enough_calculation.gui.guis.GuiPickerOreDict;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.function.TriConsumer;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.helpers.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Towdium
 */
public class JECRecipeTransferHandler implements IRecipeTransferHandler {
    static EnumMap<Recipe.EnumStackIOType, List<Singleton<Object>>> tempList;
    String recipeUID;
    Class<? extends Container> container;

    public JECRecipeTransferHandler(String recipeUID, Class<? extends Container> container) {
        this.recipeUID = recipeUID;
        this.container = container;
    }

    @SuppressWarnings("unchecked")
    static void checkTemp(GuiEditor editor) {
        Singleton<Boolean> flag = new Singleton<>(false);
        for (Recipe.EnumStackIOType type : Recipe.EnumStackIOType.values()) {
            tempList.get(type).stream().filter(o -> o.value instanceof List).forEach(o -> {
                flag.value = true;
                ItemStack buffer = PlayerRecordHelper.getOreDictPref((List<ItemStack>) o.value);
                if (buffer == null) {
                    Utilities.openGui(new GuiPickerOreDict(editor, (List<ItemStack>) o.value, itemStack -> {
                        o.value = itemStack;
                        PlayerRecordHelper.addOreDictPref(itemStack);
                        checkTemp(editor);
                    }));
                } else {
                    o.value = buffer;
                    checkTemp(editor);
                }
            });
        }
        if (!flag.value) {
            BiConsumer<List<ItemStack>, ItemStack> merger = (itemStacks, stack) -> {
                Singleton<Boolean> flag1 = new Singleton<>(false);
                itemStacks.forEach(itemStack ->
                        flag1.value = flag1.value || ItemStackHelper.mergeStack(itemStack, stack, true, false, JECConfig.EnumItems.EnableFluidMerge.getProperty().getBoolean()) != null
                );
                if (!flag1.value)
                    itemStacks.add(stack);
            };

            TriConsumer<Integer, Integer, List<Singleton<Object>>> arranger = (start, end, stacks) -> {
                List<ItemStack> buffer = new ArrayList<>(stacks.size());
                stacks.forEach(stack -> merger.accept(buffer, ((ItemStack) stack.value)));
                for (int i = start; i <= end; i++) {
                    ItemStack stack = buffer.size() > i - start ? buffer.get(i - start) : null;
                    editor.onItemStackSet(i, editor.inventorySlots.getSlot(i).getStack());
                    editor.inventorySlots.getSlot(i).putStack(stack);

                }
            };

            Utilities.openGui(editor);
            arranger.accept(0, 3, tempList.get(Recipe.EnumStackIOType.OUTPUT));
            arranger.accept(8, 19, tempList.get(Recipe.EnumStackIOType.INPUT));
            arranger.accept(4, 7, tempList.get(Recipe.EnumStackIOType.CATALYST));
        }
    }

    @Override
    @Nonnull
    public Class<? extends Container> getContainerClass() {
        return container;
    }

    @Override
    @Nonnull
    public String getRecipeCategoryUid() {
        return recipeUID;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(@Nonnull Container container, @Nonnull IRecipeLayout iRecipeLayout, @Nonnull EntityPlayer entityPlayer, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            // reset temp
            tempList = new EnumMap<>(Recipe.EnumStackIOType.class);
            for (Recipe.EnumStackIOType type : Recipe.EnumStackIOType.values()) {
                tempList.put(type, new ArrayList<>(type.getLength()));
            }
            // store into temp
            Consumer<Map<Integer, ? extends IGuiIngredient>> mover = (ing) ->
                    ing.values().forEach(ingredient -> {
                        if (ingredient.getAllIngredients().size() != 0) {
                            List<ItemStack> buffer = new ArrayList<>();
                            //noinspection unchecked
                            ingredient.getAllIngredients().forEach(stack -> {
                                if (stack != null) {
                                    if (stack instanceof ItemStack) {
                                        //noinspection ConstantConditions
                                        buffer.add(ItemStackHelper.toItemStackJEC(((ItemStack) stack).copy()));
                                    } else if (stack instanceof FluidStack) {
                                        buffer.add(ItemStackHelper.toItemStackJEC(((FluidStack) stack).copy()));
                                    }
                                }
                            });
                            tempList.get(ingredient.isInput() ? Recipe.EnumStackIOType.INPUT : Recipe.EnumStackIOType.OUTPUT)
                                    .add(buffer.size() == 1 ? new Singleton<>(buffer.get(0)) : new Singleton<>(buffer));
                        }
                    });
            mover.accept(iRecipeLayout.getItemStacks().getGuiIngredients());
            mover.accept(iRecipeLayout.getFluidStacks().getGuiIngredients());
            List<ItemStack> buffer = new ArrayList<>();
            JEIPlugin.recipeRegistry.getCraftingItems(
                    JEIPlugin.recipeRegistry.getRecipeCategories(Collections.singletonList(recipeUID)).get(0),
                    JEIPlugin.recipeRegistry.createFocus(IFocus.Mode.NONE, null)
            ).forEach(itemStack -> buffer.add(ItemStackHelper.toItemStackJEC(itemStack.copy())));
            tempList.get(Recipe.EnumStackIOType.CATALYST).
                    add(buffer.size() == 1 ? new Singleton<>(buffer.get(0)) : new Singleton<>(buffer));
            GuiScreen parent = JECGuiContainer.lastGui;
            GuiEditor editor = parent instanceof GuiEditor ? (GuiEditor) parent : new GuiEditor(parent, null);
            checkTemp(editor);
        }
        return null;
    }
}
