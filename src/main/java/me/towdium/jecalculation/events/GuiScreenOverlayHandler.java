package me.towdium.jecalculation.events;

import com.google.common.collect.Comparators;
import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.Gui;
import me.towdium.jecalculation.gui.guis.GuiCraftMini;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.IWidget;
import me.towdium.jecalculation.gui.widgets.WContainer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class GuiScreenOverlayHandler extends WContainer implements IGui {

    protected PlayerInventory inventory;

    public GuiScreenOverlayHandler(PlayerInventory inventory) {
        this.inventory = inventory;
        setupOverlay(inventory);
    }

    public void setupOverlay(PlayerInventory inventory) {

        // Sort widgets by depth
        List<GuiCraftMini> widgets = inventoryToWidgets(inventory).stream()
            .sorted(Comparator.comparingInt(GuiCraftMini::getDepth))
            .collect(Collectors.toList());

        // Normalize depth
        for (int i = 0; i < widgets.size(); i++) {
            GuiCraftMini widget = widgets.get(i);
            widget.setDepth(i, true);
            widget.setOnFocusListener((w) -> {
                Optional<GuiCraftMini> topWidget = widgets.stream().max(Comparator.comparingInt(GuiCraftMini::getDepth));
                if (topWidget.isPresent() && w != topWidget.get()) {
                    w.setDepth(topWidget.get().getDepth() + 1);
                }
            });
            add(widget);
        }
    }

    public List<GuiCraftMini> inventoryToWidgets(PlayerInventory inventory) {
        List<GuiCraftMini> results = new ArrayList<>();
        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ItemStack itemStack = inventory.mainInventory.get(i);
            if (itemStack.getItem() == JecaItem.CRAFT) {
                GuiCraftMini widget = new GuiCraftMini(itemStack, i);
                if (widget.record.overlayOpen) {
                    results.add(widget);
                }
            }
        }
        return results;
    }

    @Override
    public boolean onDraw(JecaGui gui, int mouseX, int mouseY) {
        List<GuiCraftMini> windows = getWidgets().stream()
            .filter((w) -> w instanceof GuiCraftMini)
            .map(GuiCraftMini.class::cast)
            .collect(Collectors.toList());

        // Make sure the windows are always displayed in the correct order
        if (!Comparators.isInOrder(windows, Comparator.comparingInt(GuiCraftMini::getDepth))) {
            List<GuiCraftMini> orderedWindows = windows.stream()
                .sorted(Comparator.comparingInt(GuiCraftMini::getDepth))
                .collect(Collectors.toList());

            removeAll(windows);
            addAll(orderedWindows);
        }

        return super.onDraw(gui, mouseX, mouseY);
    }

    public Collection<Rectangle2d> getGuiExtraAreas(int offsetX, int offsetY) {
        return getWidgets().stream()
            .filter((w) -> w instanceof GuiCraftMini)
            .map(GuiCraftMini.class::cast)
            .map(w -> new Rectangle2d(w.getOffsetX() + offsetX, w.getOffsetY() + offsetY, w.getWidth(), w.getHeight()))
            .collect(Collectors.toList());
    }

    @Override
    public void onVisible(JecaGui gui) {}

    @Override
    public boolean acceptsTransfer() {
        return false;
    }

    @Override
    public boolean acceptsLabel() {
        return false;
    }

    @Override
    public void setOverlay(IWidget w) {
        throw new UnsupportedOperationException("Unsupported operation.");
    }
}
