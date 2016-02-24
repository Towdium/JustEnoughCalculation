package pers.towdium.justEnoughCalculation.gui.guis.itemPicker;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.plugin.JEIPlugin;

import java.io.IOException;

/**
 * @author Towdium
 */
public class GuiItemPicker extends GuiContainer{
    ContainerItemPicker containerItemPicker;
    GuiTextField searchTextField;

    public GuiItemPicker(ContainerItemPicker containerItemPicker){
        super(containerItemPicker);
        this.containerItemPicker = containerItemPicker;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(0, guiLeft+31, guiTop+7, 50, 20, StatCollector.translateToLocal("gui.itemPicker.confirm")));
        buttonList.add(new GuiButton(0, guiLeft+7, guiTop+137, 20, 20, "<"));
        buttonList.add(new GuiButton(0, guiLeft+149, guiTop+137, 20, 20, ">"));
        searchTextField = new GuiTextField(4, fontRendererObj, guiLeft+90, guiTop+7, 79, 20){
            @Override
            public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
                boolean b = super.textboxKeyTyped(p_146201_1_, p_146201_2_);
                JustEnoughCalculation.log.info("执行了！"+JEIPlugin.runtime.getItemListOverlay().getFilterText());
                JEIPlugin.runtime.getItemListOverlay().setFilterText("apple");
                return b;
            }
        };
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,"textures/gui/guiItemPicker.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {

        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.itemPicker"), 8, 6, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        searchTextField.drawTextBox();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        searchTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.searchTextField.textboxKeyTyped(typedChar, keyCode)){
            super.keyTyped(typedChar, keyCode);
        }
    }
}
