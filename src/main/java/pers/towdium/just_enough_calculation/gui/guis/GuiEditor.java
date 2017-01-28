package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.core.Recipe;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.exception.IllegalPositionException;
import pers.towdium.just_enough_calculation.util.function.QuaConsumer;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;
import pers.towdium.just_enough_calculation.util.helpers.PlayerRecordHelper;
import pers.towdium.just_enough_calculation.util.wrappers.Pair;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author:  Towdium
 * Created: 2016/6/21.
 */
public class GuiEditor extends JECGuiContainer {
    public ArrayList<GuiButton> buttonMode;
    boolean newGroup = false;
    JECGuiButton buttonLeft;
    JECGuiButton buttonRight;
    JECGuiButton buttonNew;
    JECGuiButton buttonDup;
    JECGuiButton buttonSave;
    JECGuiButton buttonClear;
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
    public void init() {
        Function<JECGuiButton, JECGuiButton> genButtonNumType = (button) -> {
            button.setLsnLeft(() -> {
                Slot slot = inventorySlots.getSlot(button.id / 2);
                ItemStack stack = slot.getStack();
                switch (button.displayString) {
                    case "#":
                        slot.putStack(stack == null ? null : ItemStackHelper.toItemStackOfType(ItemStackHelper.EnumStackAmountType.PERCENTAGE, stack));
                        button.displayString = "%";
                        break;
                    case "%":
                        slot.putStack(stack == null ? null : ItemStackHelper.toItemStackOfType(ItemStackHelper.EnumStackAmountType.NUMBER, stack));
                        button.displayString = "#";
                        break;
                }
            });
            return button;
        };

        Function<JECGuiButton, JECGuiButton> genButtonStackType = (button) -> {
            button.setLsnLeft(() -> {
                Slot slot = inventorySlots.getSlot(button.id / 2);
                ItemStack stack = slot.getStack();
                Utilities.openGui(new GuiPickerFluid(itemStack -> {
                    Utilities.openGui(this);
                    slot.putStack(itemStack);
                    updateLayout();
                }, this, stack));
            });
            return button;
        };


        buttonMode = new ArrayList<>();
        int count = -1;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                buttonMode.add(genButtonNumType.apply(new JECGuiButton(
                        ++count, 44 + j * 21 + guiLeft, 51 + i * 33 + guiTop, 10, 10, "#", false)));
                buttonMode.add(genButtonStackType.apply(new JECGuiButton(
                        ++count, 54 + j * 21 + guiLeft, 51 + i * 33 + guiTop, 10, 10, "I", false)));
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                buttonMode.add(genButtonNumType.apply(new JECGuiButton(
                        ++count, 44 + j * 21 + guiLeft, 117 + i * 32 + guiTop, 10, 10, "#", false)));
                buttonMode.add(genButtonStackType.apply(new JECGuiButton(
                        ++count, 54 + j * 21 + guiLeft, 117 + i * 32 + guiTop, 10, 10, "I", false)));
            }
        }
        buttonLeft = new JECGuiButton(40, guiLeft + 7, guiTop + 7, 14, 20, "<", false).setLsnLeft(() -> {
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
        });
        buttonRight = new JECGuiButton(41, guiLeft + 90, guiTop + 7, 14, 20, ">", false).setLsnRight(() -> {
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
        });
        buttonNew = new JECGuiButton(42, guiLeft + 108, guiTop + 7, 61, 20, "newGroup").setLsnLeft(() -> {
            if (newGroup) {
                customName = textGroup.getText();
                group = 0;
                textGroup.setText("");
                newGroup = false;
                buttonLeft.visible = true;
                buttonRight.visible = true;
                buttonNew.displayString = localization("newGroup");
            } else {
                buttonLeft.visible = false;
                buttonRight.visible = false;
                newGroup = true;
                buttonNew.displayString = localization("confirm");
            }
        });
        buttonSave = new JECGuiButton(43, guiLeft + 131, guiTop + 31, 38, 18, "save").setLsnLeft(() -> {
            if (dest == null) {
                PlayerRecordHelper.addRecipe(toRecipe(), getGroup());
            } else {
                PlayerRecordHelper.setRecipe(getGroup(), dest.one, dest.two, toRecipe());
            }
            Utilities.openGui(parent);
        });
        buttonClear = new JECGuiButton(44, guiLeft + 131, guiTop + 53, 38, 18, "clear").setLsnLeft(() -> {
            for (int i = 0; i < 20; i++) {
                inventorySlots.getSlot(i).putStack(null);
            }
            updateLayout();
        });
        buttonList.addAll(buttonMode);
        buttonList.add(buttonLeft);
        buttonList.add(buttonRight);
        buttonList.add(buttonNew);
        buttonList.add(buttonSave);
        buttonList.add(buttonClear);
        if (dest != null) {
            buttonDup = new JECGuiButton(45, guiLeft + 131, guiTop + 75, 38, 18, "dup", true, true).
                    setLsnLeft(() -> {
                        PlayerRecordHelper.addRecipe(toRecipe(), getGroup());
                        Utilities.openGui(parent);
                    });
            buttonList.add(buttonDup);
        }
        textGroup = new GuiTextField(0, fontRendererObj, guiLeft + 8, guiTop + 8, 95, 18);
        if (customName == null && PlayerRecordHelper.getSizeGroup() == 0) {
            customName = "Default";
        }
        if (!initialized && dest != null) {
            putRecipe(PlayerRecordHelper.getRecipe(dest.one, dest.two));
            group = PlayerRecordHelper.getIndexGroup(dest.one);
            initialized = true;
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawCenteredStringMultiLine(fontRendererObj, localization("output"), 7, 44, 31, 61, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, localization("catalyst"), 7, 44, 64, 94, 0xFFFFFF);
        drawCenteredStringMultiLine(fontRendererObj, localization("input"), 7, 44, 97, 159, 0xFFFFFF);
        if (!newGroup) {
            drawCenteredStringMultiLine(fontRendererObj, getGroup(), 7, 104, 7, 27, 0xFFFFFF);
        }
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
    public void onItemStackSet(int index) {
        updateLayout();
    }

    @Override
    public void updateLayout() {
        buttonLeft.visible = !newGroup;
        buttonRight.visible = !newGroup;
        for (int i = 0; i < 20; i++) {
            ItemStack stack = inventorySlots.getSlot(i).getStack();
            GuiButton amount = buttonMode.get(2 * i);
            GuiButton type = buttonMode.get(2 * i + 1);
            QuaConsumer<String, Boolean, String, Boolean> setter = (s1, b1, s2, b2) -> {
                amount.displayString = s1;
                amount.enabled = b1;
                type.displayString = s2;
                type.enabled = b2;
            };
            if (stack == null) {
                setter.accept("#", false, "I", true);
            } else {
                switch (ItemStackHelper.NBT.getType(stack)) {
                    case NUMBER:
                        setter.accept("#", true, "I", false);
                        break;
                    case PERCENTAGE:
                        setter.accept("%", true, "I", false);
                        break;
                    case FLUID:
                        setter.accept("#", false, "F", true);
                        break;
                    default:
                        throw new IllegalPositionException();
                }
            }
        }
    }

    @Override
    protected int getDestSlot(int button) {
        int i = getLastFilledSlot();
        if (i == 20) {
            return -1;
        }
        if (button == 0) {
            return i + 1;
        } else {
            if (i < 4) {
                return 4;
            } else if (i < 8) {
                return 8;
            } else {
                return i + 1;
            }
        }
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
}
