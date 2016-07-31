package pers.towdium.just_enough_calculation.plugin;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import pers.towdium.just_enough_calculation.JECConfig;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.guis.GuiEditor;
import pers.towdium.just_enough_calculation.gui.guis.GuiPickerOreDict;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.function.TriConsumer;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.BiConsumer;

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
                    Minecraft.getMinecraft().displayGuiScreen(new GuiPickerOreDict(editor, (List<ItemStack>) o.value, itemStack -> {
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
                    editor.inventorySlots.getSlot(i).putStack(stack);
                    editor.onItemStackSet(i);
                }
            };

            Minecraft.getMinecraft().displayGuiScreen(editor);
            arranger.accept(0, 3, tempList.get(Recipe.EnumStackIOType.OUTPUT));
            arranger.accept(8, 19, tempList.get(Recipe.EnumStackIOType.INPUT));
            arranger.accept(4, 7, tempList.get(Recipe.EnumStackIOType.CATALYST));
        }
    }

    @Override
    public Class<? extends Container> getContainerClass() {
        return container;
    }

    @Override
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
            iRecipeLayout.getItemStacks().getGuiIngredients().values().forEach(ingredient -> {
                if (ingredient.getAllIngredients().size() != 0) {
                    List<ItemStack> buffer = new ArrayList<>();
                    ingredient.getAllIngredients().forEach(itemStack -> buffer.add(ItemStackHelper.toItemStackJEC(itemStack.copy())));
                    tempList.get(ingredient.isInput() ? Recipe.EnumStackIOType.INPUT : Recipe.EnumStackIOType.OUTPUT)
                            .add(buffer.size() == 1 ? new Singleton<>(buffer.get(0)) : new Singleton<>(buffer));
                }
            });
            iRecipeLayout.getFluidStacks().getGuiIngredients().values().forEach(ingredient -> {
                if (ingredient.getAllIngredients().size() != 0) {
                    List<ItemStack> buffer = new ArrayList<>();
                    ingredient.getAllIngredients().forEach(fluidStack -> buffer.add(ItemStackHelper.toItemStackJEC(fluidStack.copy())));
                    tempList.get(ingredient.isInput() ? Recipe.EnumStackIOType.INPUT : Recipe.EnumStackIOType.OUTPUT)
                            .add(buffer.size() == 1 ? new Singleton<>(buffer.get(0)) : new Singleton<>(buffer));
                }
            });
            List<ItemStack> buffer = new ArrayList<>();
            JEIPlugin.recipeRegistry.getCraftingItems(
                    JEIPlugin.recipeRegistry.getRecipeCategories(Collections.singletonList(recipeUID)).get(0)
            ).forEach(itemStack -> buffer.add(ItemStackHelper.toItemStackJEC(itemStack.copy())));
            tempList.get(Recipe.EnumStackIOType.CATALYST).
                    add(buffer.size() == 1 ? new Singleton<>(buffer.get(0)) : new Singleton<>(buffer));
            // check temp data
            Minecraft mc = Minecraft.getMinecraft();
            RecipesGui gui = (RecipesGui) mc.currentScreen;
            if (gui == null) {
                return null;
            }
            GuiScreen parent = gui.getParentScreen();
            GuiEditor editor = parent instanceof GuiEditor ? (GuiEditor) parent : new GuiEditor(parent, null);
            checkTemp(editor);
        }
        return null;
    }
}
