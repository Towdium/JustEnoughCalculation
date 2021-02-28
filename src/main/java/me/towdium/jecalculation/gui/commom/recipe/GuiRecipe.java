package me.towdium.jecalculation.gui.commom.recipe;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.Recipe;
import me.towdium.jecalculation.gui.commom.GuiTooltipScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import me.towdium.jecalculation.core.ItemStackWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Towdium
 */
public class GuiRecipe extends GuiTooltipScreen {
    private int activeSlot = -1;

    public GuiRecipe(@Nonnull Container inventorySlotsIn, @Nullable GuiScreen parent) {
        super(inventorySlotsIn, parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        int i, left, top;
        i = 0; left = 31 + guiLeft; top = 7 + guiTop;
        for(int a = 0; a < 2; a++){
            for(int b =0; b < 2; b++){
                buttonList.add(new GuiButton(i+2*a+b, left+b*59, top+a*24, 20, 20, "#"));
            }
        }
        i = 4; left = 31 + guiLeft; top = 67 + guiTop;
        for(int a = 0; a < 4; a++){
            for(int b =0; b < 3; b++){
                buttonList.add(new GuiButton(i+3*a+b, left+b*59, top+a*24, 20, 20, "#"));
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,"textures/gui/guiRecipe.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if(activeSlot != -1){
            Slot slot = inventorySlots.getSlot(activeSlot);
            this.drawTexturedModalRect(this.guiLeft+slot.xDisplayPosition-2, this.guiTop+slot.yDisplayPosition-2, 176, 0, 20, 20);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button){
        int buttonId = button.id;
        if(buttonId <= 15){
            Slot slot = inventorySlots.getSlot(buttonId);
            if(button.displayString.equals("#")){
                slot.putStack(ItemStackWrapper.toPercentage(slot.getStack()));
                button.displayString = "%";
            }else {
                slot.putStack(ItemStackWrapper.toNormal(slot.getStack()));
                button.displayString = "#";
            }
        }
    }



    /*@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton){
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Slot slot = null;
        try {
            Field slotField = this.getClass().getDeclaredField("field_146998_K");
            slotField.setAccessible(true);
            slot = (Slot) slotField.get(this);
        }
        catch (NoSuchFieldException ignored) {}
        catch (IllegalAccessException ignored) {}

        if(slot != null && mouseButton == 0 && slot.getStack() == null){
            setActiveSlot(slot.getSlotIndex());
            mc.thePlayer.playSound("random.click", 1f, 1f );
        }
    }*/

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            if(activeSlot != -1){
                setActiveSlot(-1);
            }else {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        boolean b = false;
        if(slotIn != null){
            b = slotIn.getHasStack();
        }
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);
        if(slotIn != null && b && !slotIn.getHasStack()){
            ((GuiButton) buttonList.get(slotIn.getSlotIndex())).displayString = "#";
        }
    }

    public int getActiveSlot() {
        return activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
    }

    public void displayRecipe(Recipe recipe){
        List<ItemStack> output = recipe.getOutput();
        List<ItemStack> input = recipe.getInput();
        for(int i=0; i<4; i++){
            if(i<output.size()){
                ItemStack itemStack = output.get(i);
                this.inventorySlots.getSlot(i).putStack(itemStack);
                ItemStackWrapper.NBT.setBool(itemStack, JustEnoughCalculation.Reference.MODID, true);
                if(itemStack.getTagCompound().hasKey("percentage")){
                    ((GuiButton) buttonList.get(i)).displayString = "%";
                }
            }else {
                this.inventorySlots.getSlot(i).putStack(null);
            }
        }
        for(int i=0; i<12; i++){
            if(i<input.size()){
                ItemStack itemStack = input.get(i);
                this.inventorySlots.getSlot(i+4).putStack(itemStack);
                ItemStackWrapper.NBT.setBool(itemStack, JustEnoughCalculation.Reference.MODID, true);
                if(itemStack.getTagCompound().hasKey("percentage")){
                    ((GuiButton) buttonList.get(i + 4)).displayString = "%";
                }
            }else {
                this.inventorySlots.getSlot(i+4).putStack(null);
            }
        }
    }

    @Override
    protected String GetButtonTooltip(int buttonId) {
        if(buttonId == 0){
            return StatCollector.translateToLocal("gui.calculator.tooltipUse");
        }else {
            return null;
        }
    }

    @Override
    public boolean handleDragNDrop(GuiContainer guiContainer, int i, int i1, ItemStack itemStack, int i2) {
        //JustEnoughCalculation.log.info(itemStack.toString());
        ItemStack buffer = itemStack.copy();
        ItemStackWrapper.NBT.setBool(buffer, "jecalculation", true);
        buffer.stackSize=1;
        Slot slot = getSlotUnderMouse(i, i1);
        if(slot!=null){
            slot.putStack(buffer);
            itemStack.stackSize=0;
        }
        return false;
    }
}
