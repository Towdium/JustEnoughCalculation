package pers.towdium.justEnoughCalculation.gui.commom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.core.ItemStackWrapper;
import pers.towdium.justEnoughCalculation.gui.guis.calculator.ContainerCalculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Towdium
 */
public abstract class GuiJustEnoughCalculation extends GuiContainer {
    protected GuiScreen parent;

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
        try {
            this.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException ignored) {}
    }

    public void click(int index){
        for(GuiButton button : buttonList){
            if(button.id == index){
                try {
                    actionPerformed(button);
                } catch (IOException ignored) {}
            }
        }
    }

    public void drawCenteredStringWithoutShadow(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawString(text, x - fontRendererIn.getStringWidth(text) / 2, y, color);
    }

    public void updateLayout(){}

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1){
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        ModelManager modelManager = null;
        Field[] fields = mc.getClass().getDeclaredFields();
        for(Field field : fields){
            if(ModelManager.class.equals(field.getType())){
                field.setAccessible(true);
                try {
                    modelManager = (ModelManager) field.get(mc);
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if(modelManager != null){
            itemRender = new RenderItem(mc.renderEngine, modelManager){
                @Override
                public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
                    boolean b = fr.getUnicodeFlag();
                    fr.setUnicodeFlag(true);
                    super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, ItemStackWrapper.getDisplayAmount(stack));
                    fr.setUnicodeFlag(b);
                }
            };
        }
    }
}
