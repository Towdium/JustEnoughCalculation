package me.towdium.jecalculation.compat.jei;

import dev.architectury.platform.Platform;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.compat.ModCompat;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe.IO;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiRecipe;
import me.towdium.jecalculation.utils.wrappers.Trio;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

import static me.towdium.jecalculation.compat.ModCompat.merge;

/**
 * Author: towdium
 * Date:   17-8-23.
 */
@JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JecaJEIPlugin implements IModPlugin {
    public static IJeiRuntime runtime;

    public static Class<?> FLUID_INGREDIENT_CLASS;

    public static ILabel getLabelUnderMouse() {
        var ref = new Object() {
            Object o = null;
        };
        runtime.getIngredientListOverlay().getIngredientUnderMouse().ifPresent(ingredient -> ref.o = ingredient.getIngredient());
        runtime.getBookmarkOverlay().getIngredientUnderMouse().ifPresent(ingredient -> ref.o = ingredient.getIngredient());
        return ILabel.Converter.from(ref.o);
    }

    public static boolean isRecipeScreen(Screen screen) {
        return screen instanceof IRecipesGui;
    }

    public static boolean showRecipe(ILabel l) {
        Screen s = Minecraft.getInstance().screen;
        Object rep = l.getRepresentation();
        if (rep != null)
            runtime.getRecipesGui().show(runtime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, runtime.getIngredientManager().getIngredientType(rep), rep));
        return Minecraft.getInstance().screen != s;
    }


    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(JustEnoughCalculation.MODID, "general");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new IGlobalGuiHandler() {
            @Override
            public Collection<Rect2i> getGuiExtraAreas() {
                return JustEnoughCalculation.Client.GUI_HANDLER.getGuiAreas();
            }
        });
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addUniversalRecipeTransferHandler(new JEITransferHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        ModCompat.isJEILoaded = true;
        if (Platform.isFabric())
            try {
                FLUID_INGREDIENT_CLASS = Class.forName("mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Fabric fluid ingredient class should exist!", e);
            }
    }

    public static class JEITransferHandler implements IRecipeTransferHandler<JecaGui.ContainerTransfer, Recipe> {
        @Override
        public Class<JecaGui.ContainerTransfer> getContainerClass() {
            return JecaGui.ContainerTransfer.class;
        }


        @Override
        public Class<Recipe> getRecipeClass() {
            return Recipe.class;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(JecaGui.ContainerTransfer container, Recipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
            if (doTransfer) {
                Class<?> context = runtime.getRecipeManager().createRecipeCategoryLookup().get().filter(category -> category.getRecipeType().getRecipeClass() == recipe.getClass()).findFirst().getClass();
                JecaGui gui = container.getGui();
                if (gui.root instanceof GuiRecipe) {
                    ((GuiRecipe) gui.root).transfer(convertRecipe(recipeSlots, context), context);
                } else {
                    GuiRecipe guiRecipe = new GuiRecipe();
                    JecaGui.displayGui(guiRecipe, JecaGui.getLast());
                    guiRecipe.transfer(convertRecipe(recipeSlots, context), context);
                }
            }
            return null;
        }

        protected EnumMap<IO, List<Trio<ILabel, CostList, CostList>>> convertRecipe(
                IRecipeSlotsView recipe, Class<?> context) {
            EnumMap<me.towdium.jecalculation.data.structure.Recipe.IO, List<Trio<ILabel, CostList, CostList>>> merged = new EnumMap<>(me.towdium.jecalculation.data.structure.Recipe.IO.class);  // item disamb raw
            recipe.getSlotViews().forEach(view -> merge(merged, view.getAllIngredients().map(ITypedIngredient::getIngredient).toList(), context, fromRole(view.getRole())));
            return merged;
        }
    }

    private static me.towdium.jecalculation.data.structure.Recipe.IO fromRole(RecipeIngredientRole role) {
        return switch (role) {
            case INPUT -> me.towdium.jecalculation.data.structure.Recipe.IO.INPUT;
            case OUTPUT -> me.towdium.jecalculation.data.structure.Recipe.IO.OUTPUT;
            case CATALYST -> me.towdium.jecalculation.data.structure.Recipe.IO.CATALYST;
            case RENDER_ONLY -> null;
        };
    }

}
