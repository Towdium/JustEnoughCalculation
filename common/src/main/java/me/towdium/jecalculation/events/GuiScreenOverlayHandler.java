package me.towdium.jecalculation.events;

import com.google.common.collect.Comparators;
import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.guis.GuiCraftMini;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.IWidget;
import me.towdium.jecalculation.gui.widgets.WContainer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiScreenOverlayHandler extends WContainer implements IGui {

    protected Inventory inventory;
    protected JecaGui gui;

    public GuiScreenOverlayHandler(Inventory inventory) {
        this.inventory = inventory;
    }

    public void setGui(JecaGui gui) {
        this.gui = gui;
        setupOverlay(inventory);
    }

    public void setupOverlay(Inventory inventory) {
        clear();

        // Sort widgets by depth
        List<GuiCraftMini> widgets = inventoryToWidgets(inventory).stream()
                .sorted(Comparator.comparingInt(GuiCraftMini::getDepth)).toList();

        // Normalize depth
        int numWidgetsInDefaultPosition = 0;
        for (int i = 0; i < widgets.size(); i++) {
            GuiCraftMini widget = widgets.get(i);
            if (widget.getOffsetX() == 0 && widget.getOffsetY() == 0) {
                moveToDefaultPosition(widget, numWidgetsInDefaultPosition);
                numWidgetsInDefaultPosition++;
            }
            widget.setDepth(i, true);
            widget.setOnFocusListener((w) -> {
                Optional<GuiCraftMini> topWidget = widgets.stream().max(Comparator.comparingInt(GuiCraftMini::getDepth));
                if (topWidget.isPresent() && w != topWidget.get()) {
                    w.setDepth(topWidget.get().getDepth() + 1);
                }
            });
            widget.setWindowCloseListener((w) -> {
                setupOverlay(inventory);
            });
            add(widget);
        }
    }

    protected void moveToDefaultPosition(GuiCraftMini widget, int offset) {
        widget.setOffsetX(-widget.getWidth() - 10);
        widget.setOffsetY(gui.getYSize() / 2 - widget.getHeight() / 2 + offset * 20);
    }

    public List<GuiCraftMini> inventoryToWidgets(Inventory inventory) {
        List<GuiCraftMini> results = new ArrayList<>();
        if (Controller.isServerActive())
        {
            for (int i = 0; i < inventory.items.size(); i++)
            {
                ItemStack itemStack = inventory.items.get(i);
                if (itemStack.getItem() == JecaItem.CRAFT.get())
                {
                    GuiCraftMini widget = new GuiCraftMini(itemStack, i);
                    if (widget.record.overlayOpen)
                    {
                        results.add(widget);
                    }
                }
            }
        }
        else
        {
            GuiCraftMini widget = new GuiCraftMini(null, 0);
            if (widget.record.overlayOpen)
            {
                results.add(widget);
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
            windows = orderedWindows;
        }

        windows.forEach(this::ensureWindowIsOnScreen);
        boolean result = super.onDraw(gui, mouseX, mouseY);
        windows.forEach(this::ensureWindowIsOnScreen);
        return result;
    }

    public void ensureWindowIsOnScreen(GuiCraftMini widget) {
        int topOffset = -gui.getGuiTop();
        int bottomOffset = gui.height - gui.getGuiTop();
        int leftOffset = -gui.getGuiLeft();
        int rightOffset = gui.width - gui.getGuiLeft();

        if (widget.getOffsetX() < leftOffset) {
            widget.setOffsetX(leftOffset);
        } else if (widget.getOffsetX() + widget.getWidth() > rightOffset) {
            widget.setOffsetX(rightOffset - widget.getWidth());
        }

        if (widget.getOffsetY() < topOffset) {
            widget.setOffsetY(topOffset);
        } else if (widget.getOffsetY() + widget.getHeight() > bottomOffset) {
            widget.setOffsetY(bottomOffset - widget.getHeight());
        }
    }

    public List<Rect2i> getGuiExtraAreas(int offsetX, int offsetY) {
        return getWidgets().stream()
                .filter((w) -> w instanceof GuiCraftMini)
                .map(GuiCraftMini.class::cast)
                .map(w -> new Rect2i(w.getOffsetX() + offsetX, w.getOffsetY() + offsetY, w.getWidth(), w.getHeight()))
                .collect(Collectors.toList());
    }

    @Override
    public void onVisible(JecaGui gui) {
    }

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
