package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.LocalizationHelper;
import pers.towdium.just_enough_calculation.util.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.wrappers.Pair;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Author:  Towdium
 * Created: 2016/6/21.
 */
public class GuiEditor extends JECGuiContainer {
    public boolean newGroup = false;
    public ArrayList<GuiButton> buttonMode;
    GuiButton buttonLeft;
    GuiButton buttonRight;
    GuiButton buttonNew;
    GuiButton buttonHelp;
    GuiButton buttonSave;
    GuiButton buttonClear;
    GuiTextField textGroup;
    int group;
    String customName;
    Pair<String, Integer> dest;
    boolean initialized = false;

    public GuiEditor(GuiScreen parent, Pair<String, Integer> index) {
        super(new ContainerEditor(), parent);
        this.dest = index;
        int size = PlayerRecordHelper.getSizeGroup();
        group = size == 0 ? 0 : size - 1;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonMode = new ArrayList<>();
        int count = -1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                buttonMode.add(new GuiButtonExt(++count, 44 + j * 21 + guiLeft, 51 + i * 33 + guiTop, 10, 10, "#"));
                buttonMode.add(new GuiButtonExt(++count, 54 + j * 21 + guiLeft, 51 + i * 33 + guiTop, 10, 10, "I"));
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                buttonMode.add(new GuiButtonExt(++count, 44 + j * 21 + guiLeft, 117 + i * 32 + guiTop, 10, 10, "#"));
                buttonMode.add(new GuiButtonExt(++count, 54 + j * 21 + guiLeft, 117 + i * 32 + guiTop, 10, 10, "I"));
            }
        }
        buttonLeft = new GuiButtonNew(40, guiLeft + 7, guiTop + 7, 14, 20, "<");
        buttonRight = new GuiButtonNew(41, guiLeft + 90, guiTop + 7, 14, 20, ">");
        buttonNew = new GuiButtonExt(42, guiLeft + 108, guiTop + 7, 61, 20, LocalizationHelper.format("gui.editor.newGroup"));
        buttonHelp = new GuiButtonExt(43, guiLeft + 131, guiTop + 75, 38, 18, LocalizationHelper.format("gui.editor.help"));
        buttonSave = new GuiButtonExt(44, guiLeft + 131, guiTop + 31, 38, 18, LocalizationHelper.format("gui.editor.save"));
        buttonClear = new GuiButtonExt(45, guiLeft + 131, guiTop + 53, 38, 18, LocalizationHelper.format("gui.editor.clear"));
        buttonList.addAll(buttonMode);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(buttonNew);
        buttonList.add(buttonHelp);
        buttonList.add(buttonSave);
        buttonList.add(buttonClear);
        textGroup = new GuiTextField(0, fontRendererObj, guiLeft + 8, guiTop + 8, 95, 18);
        if (customName == null && PlayerRecordHelper.getSizeGroup() == 0) {
            customName = "Default";
        }
        buttonRight.enabled = !newGroup;
        buttonRight.enabled = !newGroup;
        if (!initialized && dest != null) {
            putRecipe(PlayerRecordHelper.getRecipe(dest.one, dest.two));
            group = PlayerRecordHelper.getIndexGroup(dest.one);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredStringMultiLine(fontRendererObj, LocalizationHelper.format("gui.editor.output"), 7, 44, 31, 61, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, LocalizationHelper.format("gui.editor.catalyst"), 7, 44, 64, 94, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, LocalizationHelper.format("gui.editor.input"), 7, 44, 97, 159, 0xFFFFFF);
        if (!newGroup) {
            drawCenteredStringMultiLine(fontRendererObj, getGroup(), 7, 104, 7, 27, 0xFFFFFF);
        }
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
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiEditor.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (newGroup) {
            textGroup.drawTextBox();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id < 40) {
            Slot slot = inventorySlots.getSlot(button.id / 2);
            if (button.id % 2 == 0) {
                switch (button.displayString) {
                    case "#":
                        slot.putStack(ItemStackHelper.toItemStackOfType(ItemStackHelper.EnumStackAmountType.PERCENTAGE, slot.getStack()));
                        button.displayString = "%";
                        break;
                    case "%":
                        slot.putStack(ItemStackHelper.toItemStackOfType(ItemStackHelper.EnumStackAmountType.NUMBER, slot.getStack()));
                        button.displayString = "#";
                        break;
                }
            }
        } else {
            switch (button.id) {
                case 40:
                    if (customName == null) {
                        if (group == 0) {
                            group = PlayerRecordHelper.getSizeGroup() - 1;
                        } else {
                            --group;
                        }
                    } else {
                        if (PlayerRecordHelper.getSizeGroup() != 0) {
                            group = PlayerRecordHelper.getSizeGroup() - 1;
                            customName = null;
                        } else {
                            customName = "Default";
                        }
                    }
                    break;
                case 41:
                    if (customName == null) {
                        if (group + 1 >= PlayerRecordHelper.getSizeGroup()) {
                            group = 0;
                        } else {
                            ++group;
                        }
                    } else {
                        if (PlayerRecordHelper.getSizeGroup() != 0) {
                            group = 0;
                            customName = null;
                        } else {
                            customName = "Default";
                        }
                    }
                    break;
                case 42:
                    if (newGroup) {
                        customName = textGroup.getText();
                        group = 0;
                        textGroup.setText("");
                        newGroup = false;
                        buttonLeft.enabled = true;
                        buttonRight.enabled = true;
                        buttonNew.displayString = LocalizationHelper.format("gui.editor.newGroup");
                    } else {
                        buttonLeft.enabled = false;
                        buttonRight.enabled = false;
                        newGroup = true;
                        buttonNew.displayString = LocalizationHelper.format("gui.editor.confirm");
                    }
                    break;
                case 44:
                    if (dest == null) {
                        PlayerRecordHelper.addRecipe(toRecipe(), getGroup());
                    } else {
                        PlayerRecordHelper.setRecipe(getGroup(), dest.one, dest.two, toRecipe());
                    }

                    mc.displayGuiScreen(parent);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (newGroup) {
            textGroup.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!(newGroup && textGroup.textboxKeyTyped(typedChar, keyCode))) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        onItemStackSet(slotId);
    }

    @Override
    public void onItemStackSet(int index) {
        ItemStack stack = inventorySlots.getSlot(index).getStack();
        boolean fluid = stack != null && ItemStackHelper.NBT.getType(stack) == ItemStackHelper.EnumStackAmountType.FLUID;
        GuiButton button = buttonMode.get(2 * index);
        button.enabled = !fluid;
        button.displayString = "#";
        button = buttonMode.get(2 * index + 1);
        button.displayString = fluid ? "F" : "I";
    }

    public void putRecipe(Recipe recipe) {
        BiConsumer<Integer, List<ItemStack>> putStacks = (integer, itemStacks) -> {
            Singleton<Integer> i = new Singleton<>(integer - 1);
            itemStacks.forEach(itemStack -> inventorySlots.getSlot(++i.value).putStack(itemStack));
        };
        putStacks.accept(0, recipe.getOutput());
        putStacks.accept(4, recipe.getCatalyst());
        putStacks.accept(8, recipe.getInput());
    }

    public Recipe toRecipe() {
        BiFunction<Integer, Integer, ItemStack[]> toArray = (start, end) -> {
            ItemStack[] buffer = new ItemStack[end - start + 1];
            for (int i = start; i <= end; i++) {
                buffer[i - start] = inventorySlots.getSlot(i).getStack();
            }
            return buffer;
        };
        return new Recipe(toArray.apply(0, 3), toArray.apply(4, 7), toArray.apply(8, 19));
    }

    protected String getGroup() {
        if (customName != null) {
            return customName;
        } else if (PlayerRecordHelper.getSizeGroup() > group) {
            return PlayerRecordHelper.getGroupName(group);
        } else {
            return "Default";
        }
    }

    public static class ContainerEditor extends JECContainer {
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

    class GuiButtonNew extends GuiButtonExt {
        public GuiButtonNew(int id, int xPos, int yPos, int width, int height, String displayString) {
            super(id, xPos, yPos, width, height, displayString);
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (enabled) {
                super.drawButton(mc, mouseX, mouseY);
            }
        }
    }
}
