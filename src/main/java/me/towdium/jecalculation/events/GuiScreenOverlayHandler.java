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
        for (ItemStack itemStack : inventory.mainInventory) {
            if (itemStack.getItem() == JecaItem.CRAFT) {
                add(new GuiCraftMini(itemStack));
            }
        }
    }
}
