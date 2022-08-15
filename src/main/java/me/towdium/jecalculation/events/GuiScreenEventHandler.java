package me.towdium.jecalculation.events;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JecaGui;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiScreenEventHandler implements IGlobalGuiHandler {

    protected GuiScreenOverlayHandler overlayHandler = null;
    protected JecaGui gui = null;
    protected InventorySummary cachedInventory;
    protected RenderTooltipEvent.Pre cachedTooltipEvent;

    @SubscribeEvent
    public void onGuiOpen(ScreenOpenEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        Screen screen = event.getScreen();
        if (player == null || !(screen instanceof AbstractContainerScreen)) {
            return;
        }

        overlayHandler = new GuiScreenOverlayHandler(player.getInventory());
        gui = new JecaGui(null, false, overlayHandler);
        gui.init(Minecraft.getInstance(), screen.width, screen.height);
        overlayHandler.setGui(gui);
    }

    protected boolean isScreenValidForOverlay(Screen screen) {
        return screen instanceof AbstractContainerScreen
            && !(screen instanceof JecaGui);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawForeground(ScreenEvent.DrawScreenEvent.Post event) {
        Screen screen = event.getScreen();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || overlayHandler == null || !isScreenValidForOverlay(screen)) {
            return;
        }

        Inventory inventory = player.getInventory();
        if (didInventoryChange(inventory)) {
            overlayHandler = new GuiScreenOverlayHandler(inventory);
            gui = new JecaGui(null, false, overlayHandler);
            gui.init(Minecraft.getInstance(), screen.width, screen.height);
            overlayHandler.setGui(gui);
        } else if (screen.width != gui.width || screen.height != gui.height) {
            gui.init(screen.getMinecraft(), screen.width, screen.height);
        }

        gui.setMatrix(event.getPoseStack());
        int mouseX = gui.getGlobalMouseX();
        int mouseY = gui.getGlobalMouseY();

        event.getPoseStack().pushPose();
        event.getPoseStack().translate(gui.getGuiLeft(), gui.getGuiTop(), 0);
        overlayHandler.onDraw(gui, mouseX, mouseY);
        event.getPoseStack().popPose();

        List<String> tooltip = new ArrayList<>();
        overlayHandler.onTooltip(gui, mouseX, mouseY, tooltip);
        gui.drawHoveringText(event.getPoseStack(), tooltip, mouseX + gui.getGuiLeft(), mouseY + gui.getGuiTop(), minecraft.font);
        if (cachedTooltipEvent != null) {
            RenderTooltipEvent.Pre e = cachedTooltipEvent;
            gui.renderTooltipInternal(e.getPoseStack(), e.getComponents(), e.getX(), e.getY());
            //Screen.renderTooltip(e.getItemStack(), e.getPoseStack(), e.getComponents(), e.getX(), e.getY(), e.getScreenWidth(), e.getScreenHeight(), 1, e.getFont());
            cachedTooltipEvent = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTooltip(RenderTooltipEvent.Pre event) {
        if (overlayHandler == null || cachedTooltipEvent != null) {
            return;
        }

        boolean overlap = overlayHandler.onTooltip(gui, event.getX() - gui.getGuiLeft(), event.getY() - gui.getGuiTop(), new ArrayList<>());
        if (!overlap) {
            cachedTooltipEvent = event;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouse(ScreenEvent.MouseInputEvent event) {
        Screen screen = event.getScreen();
        if (overlayHandler == null || !isScreenValidForOverlay(screen)) {
            return;
        }

        int xMouse = gui.getGlobalMouseX();
        int yMouse = gui.getGlobalMouseY();

        if (event instanceof ScreenEvent.MouseScrollEvent.Pre) {
            double diff = ((ScreenEvent.MouseScrollEvent) event).getScrollDelta();
            if (diff != 0) {
                overlayHandler.onMouseScroll(gui, xMouse, yMouse, (int) diff);
            }
        } else if (event instanceof ScreenEvent.MouseClickedEvent.Pre) {
            int button = ((ScreenEvent.MouseClickedEvent) event).getButton();
            overlayHandler.onMouseFocused(gui, xMouse, yMouse, button);
            if (overlayHandler.onMouseClicked(gui, xMouse, yMouse, button)) {
                event.setCanceled(true);
            }
        } else if (event instanceof ScreenEvent.MouseDragEvent.Pre) {
            ScreenEvent.MouseDragEvent mde = (ScreenEvent.MouseDragEvent) event;
            overlayHandler.onMouseDragged(gui, xMouse, yMouse, (int) mde.getDragX(), (int)mde.getDragY());
        } else if (event instanceof ScreenEvent.MouseReleasedEvent.Pre) {
            int button = ((ScreenEvent.MouseReleasedEvent) event).getButton();
            overlayHandler.onMouseReleased(gui, xMouse, yMouse, button);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onKeyboardKey(ScreenEvent.KeyboardKeyEvent event) {
        Screen screen = event.getScreen();
        if (overlayHandler == null || !isScreenValidForOverlay(screen)) {
            return;
        }

        if (event instanceof ScreenEvent.KeyboardKeyPressedEvent.Pre) {
            if (overlayHandler.onKeyPressed(gui, event.getKeyCode(), event.getModifiers())) {
                event.setCanceled(true);
            }
        } else if (event instanceof ScreenEvent.KeyboardKeyReleasedEvent.Pre) {
            if (overlayHandler.onKeyReleased(gui, event.getKeyCode(), event.getModifiers())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onCharTyped(ScreenEvent.KeyboardCharTypedEvent event) {
        Screen screen = event.getScreen();
        if (overlayHandler == null || !isScreenValidForOverlay(screen)) {
            return;
        }

        if (overlayHandler.onChar(gui, event.getCodePoint(), event.getModifiers())) {
            event.setCanceled(true);
        }
    }

    private boolean didInventoryChange(Inventory inventory) {
        if (cachedInventory == null) {
            cachedInventory = new InventorySummary(inventory);
            return false;
        }

        InventorySummary newSummery = new InventorySummary(inventory);
        if (newSummery.equals(cachedInventory)) {
            return false;
        }

        cachedInventory = newSummery;
        return true;
    }

    @Override
    public Collection<Rect2i> getGuiExtraAreas() {
        if (overlayHandler != null && gui != null) {
            return overlayHandler.getGuiExtraAreas(gui.getGuiLeft(), gui.getGuiTop());
        }
        return Collections.emptyList();
    }
}
