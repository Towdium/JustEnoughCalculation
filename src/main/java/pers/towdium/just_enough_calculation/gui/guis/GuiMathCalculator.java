package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;

import javax.annotation.Nullable;

/**
 * Author: Towdium
 * Date:   2016/9/24.
 */
public class GuiMathCalculator extends JECGuiContainer {
    public GuiMathCalculator(GuiScreen parent) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {}

            @Override
            public EnumSlotType getSlotType(int index) {
                return EnumSlotType.DISABLED;
            }
        }, parent);
    }

    @Nullable
    @Override
    protected String getButtonTooltip(int buttonId) {
        return null;
    }

    @Override
    protected int getSizeSlot(int index) {
        return 0;
    }

    @Override
    protected void init() {
        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + 67, 28, 20, "7"));
        buttonList.add(new GuiButton(0, guiLeft + 39, guiTop + 67, 28, 20, "8"));
        buttonList.add(new GuiButton(0, guiLeft + 71, guiTop + 67, 28, 20, "9"));
        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + 91, 28, 20, "4"));
        buttonList.add(new GuiButton(0, guiLeft + 39, guiTop + 91, 28, 20, "5"));
        buttonList.add(new GuiButton(0, guiLeft + 71, guiTop + 91, 28, 20, "6"));
        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + 115, 28, 20, "3"));
        buttonList.add(new GuiButton(0, guiLeft + 39, guiTop + 115, 28, 20, "2"));
        buttonList.add(new GuiButton(0, guiLeft + 71, guiTop + 115, 28, 20, "1"));
        buttonList.add(new GuiButton(0, guiLeft + 7, guiTop + 139, 28, 20, "0"));
        buttonList.add(new GuiButton(0, guiLeft + 39, guiTop + 139, 28, 20, "00"));
        buttonList.add(new GuiButton(0, guiLeft + 71, guiTop + 139, 28, 20, "."));
        buttonList.add(new GuiButton(0, guiLeft + 109, guiTop + 67, 28, 20, "◄"));
        buttonList.add(new GuiButton(0, guiLeft + 141, guiTop + 67, 28, 20, "+"));
        buttonList.add(new GuiButton(0, guiLeft + 109, guiTop + 91, 28, 20, "C"));
        buttonList.add(new GuiButton(0, guiLeft + 141, guiTop + 91, 28, 20, "-"));
        buttonList.add(new GuiButtonExt(0, guiLeft + 109, guiTop + 115, 28, 44, "="));
        buttonList.add(new GuiButton(0, guiLeft + 141, guiTop + 115, 28, 20, "×"));
        buttonList.add(new GuiButton(0, guiLeft + 141, guiTop + 139, 28, 20, "÷"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiMathCalculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        drawNum(2, 5);
    }

    protected void drawLT(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 176, 0, 4, 14);
    }

    protected void drawRT(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 14, guiTop + y, 180, 0, 4, 14);
    }

    protected void drawLB(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y + 14, 184, 0, 4, 14);
    }

    protected void drawRB(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 14, guiTop + y + 14, 188, 0, 4, 14);
    }

    protected void drawT(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 3, guiTop + y, 176, 17, 12, 4);
    }

    protected void drawM(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 3, guiTop + y + 13, 176, 21, 12, 4);
    }

    protected void drawB(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 3, guiTop + y + 26, 176, 25, 12, 4);
    }

    protected void drawDot(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 17, guiTop + y + 26, 188, 17, 4, 4);
    }

    protected void drawCharPlus(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 0, 12, 4);
    }

    protected void drawCharMinus(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 7, 12, 4);
    }

    protected void drawCharProduct(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 14, 12, 4);
    }

    protected void drawCharDivision(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 21, 12, 4);
    }

    protected void drawNum(int x, int y, int num) {
        boolean[] sh = getShape(num);
        if(sh[0]) drawT(x, y);
        if(sh[1]) drawRT(x, y);
        if(sh[2]) drawRB(x, y);
        if(sh[3]) drawB(x, y);
        if(sh[4]) drawLB(x, y);
        if(sh[5]) drawLT(x, y);
        if(sh[6]) drawM(x, y);
    }

    protected void drawNum(int pos, int num) {
        drawNum(141 - pos*20, 21, num);
    }

    protected void drawDot(int pos) {
        drawDot(158 - pos*20, 47);
    }

    protected void drawCharPlus() {
        drawCharPlus(152, 12);
    }

    protected void drawCharMinus() {
        drawCharMinus(146, 12);
    }

    protected void drawCharProduct() {
        drawCharProduct(140, 12);
    }

    protected void drawCharDivision() {
        drawCharDivision(134, 12);
    }

    static class Shapes{
        static boolean[] ZERO = {true, true, true, true, true, true, false};
        static boolean[] ONE = {false, true, true, false, false, false, false};
        static boolean[] TWO = {true, true, false, true, true, false, true};
        static boolean[] THREE = {true, true, true, true, false, false, true};
        static boolean[] FOUR = {false, true, true, false, false, true, true};
        static boolean[] FIVE = {true, false, true, true, false, true, true};
        static boolean[] SIX = {true, false, true, true, true, true, true};
        static boolean[] SEVEN = {true, true, true, false, false, false, false};
        static boolean[] EIGHT = {true, true, true, true, true, true, true};
        static boolean[] NINE = {true, true, true, true, false, true, true};
    }

    protected boolean[] getShape(int num) {
        switch (num) {
            case 0: return Shapes.ZERO;
            case 1: return Shapes.ONE;
            case 2: return Shapes.TWO;
            case 3: return Shapes.THREE;
            case 4: return Shapes.FOUR;
            case 5: return Shapes.FIVE;
            case 6: return Shapes.SIX;
            case 7: return Shapes.SEVEN;
            case 8: return Shapes.EIGHT;
            case 9: return Shapes.NINE;
        }
        return new boolean[] {false, false, false, false, false, false, false};
    }
}
