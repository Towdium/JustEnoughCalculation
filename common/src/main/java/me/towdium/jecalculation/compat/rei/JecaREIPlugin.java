package me.towdium.jecalculation.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.math.impl.PointHelper;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.overlay.OverlayListWidget;
import me.shedaniel.rei.api.client.overlay.ScreenOverlay;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.compat.ModCompat;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiRecipe;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.towdium.jecalculation.compat.ModCompat.merge;

public class JecaREIPlugin implements REIClientPlugin {

    public static boolean isRecipeScreen(Screen screen) {
        return screen instanceof DisplayScreen;
    }

    public static boolean showRecipe(ILabel l) {
        AtomicBoolean opened = new AtomicBoolean(false);
        Object rep = l.getRepresentation();
        if (rep != null) {
            EntryTypeRegistry.getInstance().values().stream()
                    .filter(definition -> rep.getClass() == definition.getValueType())
                    .findAny()
                    .or(() -> EntryTypeRegistry.getInstance().values().stream()
                            .filter(definition -> definition.getValueType().isInstance(rep.getClass()))
                            .findAny())
                    .ifPresent(definition -> opened.set(ViewSearchBuilder.builder().addRecipesFor(EntryStack.of(definition.cast(), rep)).open()));
        }
        return opened.get();
    }

    public static ILabel getLabelUnderMouse() {
        var ref = new Object() {
            Object o;
        };
        REIRuntime.getInstance().getOverlay()
                .map(ScreenOverlay::getEntryList)
                .map(OverlayListWidget::getFocusedStack)
                .ifPresent(stack -> {
                    if (!stack.isEmpty())
                        ref.o = stack.getValue();
                });
        REIRuntime.getInstance().getOverlay()
                .flatMap(ScreenOverlay::getFavoritesList)
                .map(OverlayListWidget::getFocusedStack)
                .ifPresent(stack -> {
                    if (!stack.isEmpty())
                        ref.o = stack.getValue();
                });
        Optional.ofNullable(ScreenRegistry.getInstance().getFocusedStack(Minecraft.getInstance().screen, PointHelper.ofMouse()))
                .ifPresent(stack -> {
                    if (!stack.isEmpty())
                        ref.o = stack.getValue();
                });
        return ILabel.Converter.from(ref.o);
    }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        ModCompat.isREILoaded = true;
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(AbstractContainerScreen.class, screen -> JustEnoughCalculation.GUI_HANDLER.getGuiAreas().parallelStream()
                .map(rect2i -> new Rectangle(rect2i.getX(), rect2i.getY(), rect2i.getWidth(), rect2i.getHeight())).toList());
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new REITransferHandler());
    }

    public static class REITransferHandler implements TransferHandler {

        @Override
        public Result handle(Context context) {
            if (context.getContainerScreen() instanceof JecaGui gui) {
                if (context.isActuallyCrafting()) {
                    if (gui.root instanceof GuiRecipe guiRecipe) {
                        guiRecipe.transfer(convertRecipe(context), context.getClass());
                        context.getMinecraft().setScreen(gui);
                    } else {
                        GuiRecipe guiRecipe = new GuiRecipe();
                        JecaGui.displayGui(guiRecipe, JecaGui.getLast());
                        guiRecipe.transfer(convertRecipe(context), context.getClass());
                        if (JecaGui.override != null)
                            context.getMinecraft().setScreen(JecaGui.override);
                    }
                }
                return Result.createSuccessful();
            }
            return Result.createNotApplicable();
        }

        protected EnumMap<Recipe.IO, List<Trio<ILabel, CostList, CostList>>> convertRecipe(TransferHandler.Context context) {
            EnumMap<Recipe.IO, List<Trio<ILabel, CostList, CostList>>> merged = new EnumMap<>(Recipe.IO.class);
            context.getDisplay().getInputEntries().forEach(ingredient -> merge(merged, ingredient.stream().map(EntryStack::getValue).toList(), context.getClass(), Recipe.IO.INPUT));
            context.getDisplay().getOutputEntries().forEach(ingredient -> merge(merged, ingredient.stream().map(EntryStack::getValue).toList(), context.getClass(), Recipe.IO.OUTPUT));
            return merged;
        }
    }
}
