package me.towdium.jecalculation.client.gui.guis;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.drawables.*;
import me.towdium.jecalculation.data.ControllerClient;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.User;
import me.towdium.jecalculation.item.ItemCalculator;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

import static me.towdium.jecalculation.client.gui.drawables.WLabel.enumMode.*;

/**
 * Author: towdium
 * Date:   8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiCalculator extends WContainer {
    WLabelGroup wRecent = new WLabelGroup(7, 31, 8, 1, PICKER);
    User.Recent recent;

    public GuiCalculator() {
        add(new WPanel());
        add(new WTextField(61, 7, 64));
        add(new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL_N, Resource.BTN_LABEL_F, "calculator.label")
                .setListenerLeft(() -> JecGui.displayGui(new GuiLabel(l -> {
                    JecGui.displayParent();
                    JecGui.getCurrent().hand = l;
                }))));
        add(new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW_N, Resource.BTN_NEW_F, "calculator.recipe")
                .setListenerLeft(() -> JecGui.displayGui(true, true, new GuiRecipe())));
        add(new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH_N, Resource.BTN_SEARCH_F, "calculator.search"));
        add(new WLabelGroup(7, 87, 9, 4, RESULT));
        add(wRecent);
        add(new WLabel(31, 7, 20, 20, SELECTOR).setLsnrUpdate(this::setRecent));
        add(new WLine(52));
        add(new WIcon(151, 31, 18, 18, Resource.ICN_RECENT_N, Resource.ICN_RECENT_F, "calculator.history"));
        add(new WSwitcher(7, 56, 162, 5));
        Optional<ItemStack> ois = getStack();
        recent = ois.map(is -> new User.Recent(Utilities.getTag(is).getTagList(User.Recent.IDENTIFIER, 9)))
                .orElseGet(ControllerClient::getRecent);
    }

    void setRecent(ILabel label) {
        recent.push(label);
        Optional<ItemStack> ois = getStack();
        ois.ifPresent(is -> Utilities.getTag(is).setTag(User.Recent.IDENTIFIER, recent.serialize()));
        wRecent.setLabel(recent.getRecords().subList(1, recent.getRecords().size()), 0);
    }

    Optional<ItemStack> getStack() {
        ItemStack is = Minecraft.getMinecraft().player.inventory.getCurrentItem();
        return Optional.ofNullable(is.getItem() instanceof ItemCalculator ? is : null);
    }
}
