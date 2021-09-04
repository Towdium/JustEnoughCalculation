package me.towdium.jecalculation.events;

import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.gui.guis.Gui;
import me.towdium.jecalculation.gui.guis.GuiCraftMini;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class GuiScreenOverlayHandler extends Gui {

    protected PlayerInventory inventory;

    public GuiScreenOverlayHandler(PlayerInventory inventory) {
        this.inventory = inventory;
        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            ItemStack itemStack = inventory.mainInventory.get(i);
            if (itemStack.getItem() == JecaItem.CRAFT) {
                GuiCraftMini widget = new GuiCraftMini(itemStack, i);
                if (widget.record.overlayOpen) {
                    add(widget);
                }
            }
        }
    }
}
