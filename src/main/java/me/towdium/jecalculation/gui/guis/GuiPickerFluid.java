package me.towdium.jecalculation.gui.guis;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JECContainer;
import me.towdium.jecalculation.util.helpers.ItemStackHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: Towdium
 * Date:   2016/7/29.
 */
public class GuiPickerFluid extends GuiPicker {
    Consumer<ItemStack> callback;
    GuiTextField fieldAmount;
    JECGuiButton buttonConfirm;

    public GuiPickerFluid(Consumer<ItemStack> callback, GuiScreen parent, ItemStack stack) {
        super(new ContainerPickerFluid(), parent, 4, getRegistryStacks());
        this.callback = callback;
        inventorySlots.getSlot(36).putStack(stack);
    }

    private static List<ItemStack> getRegistryStacks() {
        List<ItemStack> buffer = new ArrayList<>();
        FluidRegistry.getRegisteredFluids().values().forEach(fluid -> buffer.add(ItemStackHelper.toItemStackJEC(new FluidStack(fluid, 1000))));
        return buffer;
    }

    @Override
    public void init() {
        super.init();
        fieldAmount = new GuiTextField(0, fontRenderer, guiLeft + 39, guiTop + 8, 58, 18);
        fieldAmount.setText(inventorySlots.getSlot(36).getStack().isEmpty() ?
                "" : String.valueOf(ItemStackHelper.NBT.getAmount(inventorySlots.getSlot(36).getStack())));
        buttonConfirm = new JECGuiButton(2, guiLeft + 119, guiTop + 7, 50, 20, "confirm").setLsnLeft(() -> {
            int amount;
            try {
                amount = Integer.parseInt(fieldAmount.getText());
            } catch (NumberFormatException e) {
                fieldAmount.setTextColor(0xFF0000);
                return;
            }
            callback.accept(ItemStackHelper.NBT.setAmount(inventorySlots.getSlot(36).getStack(), amount));
        });
        buttonConfirm.enabled = inventorySlots.getSlot(36).getHasStack();
        buttonList.add(buttonConfirm);
    }

    @Override
    protected int getSizeSlot(int index) {
        return index == 36 ? 20 : 18;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/gui_picker_fluid.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        fieldAmount.drawTextBox();
        drawCenteredStringWithoutShadow(fontRenderer, "mb", guiLeft + 107, guiTop + 13, 4210752);
        fontRenderer.drawString(localization("search"), guiLeft + 7, guiTop + 51, 4210752);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    GuiTextField getSearchField(FontRenderer renderer) {
        String textSearch = localization("search");
        return new GuiTextField(0, renderer, guiLeft + fontRenderer.getStringWidth(textSearch) + 15,
                guiTop + 46, 75, 18);
    }

    @Override
    protected void onItemStackPick(ItemStack itemStack) {
        inventorySlots.getSlot(36).putStack(itemStack);
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(fieldAmount.getText());
            buttonConfirm.enabled = true;
        } catch (NumberFormatException e) {
            fieldAmount.setTextColor(0xFF0000);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        fieldAmount.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!fieldAmount.textboxKeyTyped(typedChar, keyCode))
            super.keyTyped(typedChar, keyCode);
        else {
            try {
                //noinspection ResultOfMethodCallIgnored
                Long.parseLong(fieldAmount.getText());
                fieldAmount.setTextColor(0xFFFFFF);
                buttonConfirm.enabled = inventorySlots.getSlot(36).getHasStack();
            } catch (NumberFormatException e) {
                fieldAmount.setTextColor(0xFF0000);
            }
        }
    }

    public static class ContainerPickerFluid extends JECContainer {
        @Override
        protected void addSlots() {
            addSlotGroup(8, 70, 18, 18, 4, 9);
            addSlotSingle(9, 9);
        }

        @Override
        public EnumSlotType getSlotType(int index) {
            return index == 36 ? EnumSlotType.DISABLED : EnumSlotType.PICKER;
        }
    }
}
