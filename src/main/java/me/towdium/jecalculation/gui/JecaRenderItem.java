package me.towdium.jecalculation.gui;

import me.towdium.jecalculation.utils.ItemStackHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

public class JecaRenderItem extends RenderItem {
    @Override
    public void renderItemOverlayIntoGUI(FontRenderer fr,
                                         TextureManager textureManager,
                                         ItemStack stack,
                                         int xPosition,
                                         int yPosition,
                                         String text) {
        boolean b = fr.getUnicodeFlag();
        fr.setUnicodeFlag(true);
        String customText = stack == null ?
                            "" :
                            ItemStackHelper.NBT.getType(stack)
                                               .getDisplayString(ItemStackHelper.NBT.getAmount(stack));
        System.out.println("render " + customText);
        super.renderItemOverlayIntoGUI(fr, textureManager, stack, xPosition, yPosition, customText);
        fr.setUnicodeFlag(b);
    }
}
