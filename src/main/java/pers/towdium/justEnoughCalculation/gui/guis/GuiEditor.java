package pers.towdium.justEnoughCalculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.justEnoughCalculation.JECTranslator;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.gui.JECContainer;
import pers.towdium.justEnoughCalculation.gui.JECGuiContainer;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Author:  Towdium
 * Created: 2016/6/21.
 */
public class GuiEditor extends JECGuiContainer {
    ArrayList<GuiButton> buttonMode;
    GuiButton buttonLeft;
    GuiButton buttonRight;
    GuiButton buttonNew;
    GuiButton buttonHelp;
    GuiButton buttonSave;
    GuiButton buttonClear;


    public GuiEditor(GuiScreen parent) {
        super(new ContainerEditor(), parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonMode = new ArrayList<>();
        int count = -1;
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 4; j++){
                buttonMode.add(new GuiButtonExt(++count, 44+j*21+guiLeft, 51+i*33+guiTop, 10, 10, "#"));
                buttonMode.add(new GuiButtonExt(++count, 54+j*21+guiLeft, 51+i*33+guiTop, 10, 10, "I"));
            }
        }
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 6; j++){
                buttonMode.add(new GuiButtonExt(++count, 44+j*21+guiLeft, 117+i*32+guiTop, 10, 10, "#"));
                buttonMode.add(new GuiButtonExt(++count, 54+j*21+guiLeft, 117+i*32+guiTop, 10, 10, "I"));
            }
        }
        buttonLeft = new GuiButtonExt(40, guiLeft+7, guiTop+7, 14, 20, "<");
        buttonRight = new GuiButtonExt(41, guiLeft+90, guiTop+7, 14, 20, ">");
        buttonNew = new GuiButtonExt(42, guiLeft+108, guiTop+7, 61, 20, JECTranslator.format("gui.editor.newGroup"));
        buttonHelp = new GuiButtonExt(43, guiLeft+131, guiTop+75, 38, 18, JECTranslator.format("gui.editor.help"));
        buttonSave = new GuiButtonExt(44, guiLeft+131, guiTop+31, 38, 18, JECTranslator.format("gui.editor.save"));
        buttonClear = new GuiButtonExt(45, guiLeft+131, guiTop+53, 38, 18, JECTranslator.format("gui.editor.clear"));
        buttonList.addAll(buttonMode);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(buttonNew);
        buttonList.add(buttonHelp);
        buttonList.add(buttonSave);
        buttonList.add(buttonClear);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredStringMultiLine(fontRendererObj, JECTranslator.format("gui.editor.output"), 7, 44, 31, 61, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, JECTranslator.format("gui.editor.catalyst"), 7, 44, 64, 94, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, JECTranslator.format("gui.editor.input"), 7, 44, 97, 159, 0xFFFFFF);
    }

    @Nullable
    @Override
    protected String getButtonTooltip(int buttonId) {
        return null;
    }

    @Override
    protected int getSizeSlot(int index) {
        return 20;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,"textures/gui/guiEditor.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    public static class ContainerEditor extends JECContainer{
        @Override
        protected void addSlots() {
            addSlotGroup(46, 33, 21, 33, 1, 4);
            addSlotGroup(46, 66, 21, 33, 1, 4);
            addSlotGroup(46, 99, 21, 32, 2, 6);
        }

        @Override
        public EnumSlotType getSlotType(int index) {
            return EnumSlotType.AMOUNT;
        }
    }
}
