package me.towdium.jecalculation.gui.commom;

import codechicken.nei.guihook.GuiContainerManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import me.towdium.jecalculation.core.ItemStackWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Towdium
 */
public abstract class GuiJustEnoughCalculation extends GuiContainer {
    protected GuiScreen parent;
    protected RenderItem renderItem = new RenderItem(){
        @Override
        public void renderItemOverlayIntoGUI(FontRenderer p_77021_1_, TextureManager p_77021_2_, ItemStack p_77021_3_, int p_77021_4_, int p_77021_5_, String text) {
            if(p_77021_3_!=null &&  p_77021_3_.hasTagCompound() && p_77021_3_.getTagCompound().getBoolean("jecalculation")){
                boolean b = p_77021_1_.getUnicodeFlag();
                p_77021_1_.setUnicodeFlag(true);
                super.renderItemOverlayIntoGUI(p_77021_1_, p_77021_2_, p_77021_3_, p_77021_4_, p_77021_5_, ItemStackWrapper.getDisplayAmount(p_77021_3_));
                p_77021_1_.setUnicodeFlag(b);
            }else {
                super.renderItemOverlayIntoGUI(p_77021_1_, p_77021_2_, p_77021_3_, p_77021_4_, p_77021_5_, text);
            }


        }
    };

    public GuiJustEnoughCalculation(@Nonnull Container inventorySlotsIn, @Nullable GuiScreen parent) {
        super(inventorySlotsIn);
        this.parent = parent;
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        if (slotIn != null) {
            slotId = slotIn.slotNumber;
        }
        mc.thePlayer.openContainer.slotClick(slotId, clickedButton, clickType, mc.thePlayer);
    }

    public void click(int mouseX, int mouseY, int mouseButton){
        this.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void click(int index){
        for(Object button : buttonList){
            if(((GuiButton) button).id == index){
                actionPerformed(((GuiButton) button));
            }
        }
    }

    public void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    public void updateLayout(){}

    @Override
    protected void keyTyped(char typedChar, int keyCode){
        if(keyCode == 1){
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        RenderItem buffer1 = itemRender;
        RenderItem buffer2 =  GuiContainerManager.drawItems;
        itemRender = renderItem;
        GuiContainerManager.drawItems = renderItem;
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        itemRender = buffer1;
        GuiContainerManager.drawItems = buffer2;
    }

    protected Slot getSlotUnderMouse(int x, int y){
        Method getSlot = null;
        try {
            getSlot = GuiContainer.class.getDeclaredMethod("getSlotAtPosition", int.class, int.class);
        } catch (NoSuchMethodException ignored) {
            try {
                getSlot = GuiContainer.class.getDeclaredMethod("func_146975_c", int.class, int.class);
            } catch (NoSuchMethodException ignored_) {}
        }
        try {
            getSlot.setAccessible(true);
            return (Slot) getSlot.invoke(this, x, y);
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            return null;
        }
        //getSlotAtPosition(int p_146975_1_, int p_146975_2_)
    }
}
