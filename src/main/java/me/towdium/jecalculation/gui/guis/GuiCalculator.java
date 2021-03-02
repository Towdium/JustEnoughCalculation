package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JecaContainer;
import me.towdium.jecalculation.gui.JecaGuiContainer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class GuiCalculator extends JecaGuiContainer {
    GuiTextField textFieldAmount;

    GuiButton buttonSearch;
    GuiButton buttonAdd;
    GuiButton buttonView;
    GuiButton buttonSettings;
    GuiButton buttonLeft;
    GuiButton buttonRight;
    GuiButton buttonMode;
    GuiButton buttonStock;

    int activeSlot = -1;
    int page = 1;
    int total = 0;
    EnumMode mode = EnumMode.INPUT;

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public enum EnumMode {
        INPUT, PROCEDURE, OUTPUT, CATALYST;

        int toInt() {
            switch (this) {
                case INPUT:
                    return 1;
                case PROCEDURE:
                    return 2;
                case OUTPUT:
                    return 3;
                case CATALYST:
                    return 4;
                default:
                    return 1;
            }
        }

        static EnumMode fromInt(int i) {
            switch (i) {
                case 1:
                    return INPUT;
                case 2:
                    return PROCEDURE;
                case 3:
                    return OUTPUT;
                case 4:
                    return CATALYST;
                default:
                    return INPUT;
            }
        }
    }

    public GuiCalculator(GuiScreen parent) {
        super(new ContainerCalculator(), parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        buttonSearch = new GuiButton(1, guiLeft + 119, guiTop + 7, 50, 20, "search");
        buttonAdd = new GuiButton(2, guiLeft + 7, guiTop + 53, 52, 20, "Add");
        buttonView = new GuiButton(3, guiLeft + 63, guiTop + 53, 52, 20, "Records");
        buttonSettings = new GuiButton(4, guiLeft + 119, guiTop + 53, 50, 20, "Settings");
        buttonLeft = new GuiButton(5, guiLeft + 7, guiTop + 139, 14, 20, "<");
        buttonRight = new GuiButton(6, guiLeft + 45, guiTop + 139, 14, 20, ">");
        buttonMode = new GuiButton(7, guiLeft + 63, guiTop + 139, 52, 20, "Catalyst");
        buttonStock = new GuiButton(8, guiLeft + 119, guiTop + 139, 50, 20, "Stock");
        buttonList.add(buttonSearch);
        buttonList.add(buttonAdd);
        buttonList.add(buttonView);
        buttonList.add(buttonSettings);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(buttonMode);
        buttonList.add(buttonStock);
        textFieldAmount = new GuiTextField(fontRendererObj, guiLeft + 39, guiTop + 8, 75, 18);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(
                new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiCalculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        textFieldAmount.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString("x", 30, 13, 0x404040);
        drawCenteredStringWithoutShadow(fontRendererObj, "Recent", 144, 36, 0x404040);
        drawCenteredString(fontRendererObj, page + "/" + total, 33, 145, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (button.id) {
            case 1: mc.displayGuiScreen(new GuiRecipeSearch(this));
        }
    }
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 4:
                return "Hello\\nShit";
            default:
                return null;
        }
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 0 ? 20: 0;
    }

    public static class ContainerCalculator extends JecaContainer {
        @Override
        protected void addSlots() {
            addSlotSingle(9, 9);
            addSlotGroup(8, 32, 18, 18, 1, 6);
            addSlotGroup(8, 82, 18, 18, 3, 9);
        }

        @Override
        public EnumSlotType getSlotType(int index) {
            return index == 0 ? EnumSlotType.SELECT : EnumSlotType.DISABLED;
        }
    }
}
