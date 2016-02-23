package pers.towdium.justEnoughCalculation.event;


import mezz.jei.gui.ItemListOverlay;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import pers.towdium.justEnoughCalculation.gui.calculator.ContainerCalculator;
import pers.towdium.justEnoughCalculation.gui.calculator.GuiCalculator;
import pers.towdium.justEnoughCalculation.gui.recipeEditor.ContainerRecipeEditor;
import pers.towdium.justEnoughCalculation.gui.recipeEditor.GuiRecipeEditor;
import pers.towdium.justEnoughCalculation.plugin.JEIPlugin;

import java.util.Date;

/**
 * @author Towdium
 */
public class GuiEventHandler {
    long time = new Date().getTime();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event){
        if(event.gui instanceof GuiRecipeEditor){
            if(new Date().getTime() - time < 200){
                event.setCanceled(true);
                return;
            }
            if (((GuiRecipeEditor) event.gui).getActiveSlot() != -1){
                GuiRecipeEditor guiContainer = (GuiRecipeEditor) event.gui;
                ItemStack itemStack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
                Slot slot = ((GuiRecipeEditor) event.gui).inventorySlots.getSlot(((GuiRecipeEditor) event.gui).getActiveSlot());
                slot.inventory.setInventorySlotContents(slot.getSlotIndex(), itemStack);
                if(Mouse.isButtonDown(0)){
                    time = new Date().getTime();
                    if(guiContainer.getSlotUnderMouse() != null){
                        guiContainer.setActiveSlot(guiContainer.getSlotUnderMouse().getSlotIndex());
                        ((ContainerRecipeEditor)((GuiRecipeEditor) event.gui).inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
                        event.setCanceled(true);
                        return;
                    }
                    int y = guiContainer.height - Mouse.getEventY() * guiContainer.height / guiContainer.mc.displayHeight - 1;
                    int x = Mouse.getEventX() * guiContainer.width / guiContainer.mc.displayWidth;
                    boolean over = ((ItemListOverlay)JEIPlugin.runtime.getItemListOverlay()).isMouseOver(x, y);
                    if(!over || (y>25 && y<guiContainer.height-15)){
                        ((GuiRecipeEditor) event.gui).setActiveSlot(-1);
                        ((ContainerRecipeEditor)((GuiRecipeEditor) event.gui).inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
                        event.setCanceled(true);
                    }
                }
            }
        }else if(event.gui instanceof GuiCalculator){
            if(new Date().getTime() - time < 200){
                event.setCanceled(true);
                return;
            }
            if (((GuiCalculator) event.gui).getActiveSlot() == 0){
                Slot slot = ((GuiCalculator) event.gui).inventorySlots.getSlot(0);
                ItemStack itemStack = JEIPlugin.runtime.getItemListOverlay().getStackUnderMouse();
                slot.inventory.setInventorySlotContents(slot.getSlotIndex(), itemStack);
                if (Mouse.isButtonDown(0)){
                    time = new Date().getTime();
                    Slot slot1 = ((GuiCalculator) event.gui).getSlotUnderMouse();
                    if(slot1 != null){
                        ((GuiCalculator) event.gui).setActiveSlot(slot1.getSlotIndex());
                    }else {
                        ((GuiCalculator) event.gui).setActiveSlot(-1);
                    }
                    ((ContainerCalculator)((GuiCalculator) event.gui).inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
                    event.setCanceled(true);
                }
            } else if(((GuiCalculator) event.gui).getActiveSlot() != -1) {
                if (Mouse.isButtonDown(0)){
                    Slot slot1 = ((GuiCalculator) event.gui).getSlotUnderMouse();
                    if(slot1 != null){
                        ((GuiCalculator) event.gui).setActiveSlot(slot1.getSlotIndex());
                    }else {
                        ((GuiCalculator) event.gui).setActiveSlot(-1);
                    }
                    ((ContainerCalculator)((GuiCalculator) event.gui).inventorySlots).getPlayer().playSound("random.click", 1f, 1f );
                    event.setCanceled(true);
                }
            }
        }
    }
}
