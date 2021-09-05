package me.towdium.jecalculation.events;

import com.mojang.blaze3d.systems.RenderSystem;
import me.towdium.jecalculation.gui.JecaGui;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class GuiScreenEventHandler implements IGlobalGuiHandler {

    protected GuiScreenOverlayHandler overlayHandler = null;
    protected JecaGui gui = null;
    protected PlayerInventory cachedInventory;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        Screen screen = event.getGui();
        if (player == null || !(screen instanceof ContainerScreen)) {
            return;
        }

        overlayHandler = new GuiScreenOverlayHandler(player.inventory);
        gui = new JecaGui(null, false, overlayHandler);
        gui.init(Minecraft.getInstance(), screen.width, screen.height);
    }

    protected boolean isScreenValidForOverlay(Screen screen) {
        return screen instanceof ContainerScreen
            && !(screen instanceof JecaGui);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    @SuppressWarnings("deprecation")
    public void onDrawForeground(GuiScreenEvent.DrawScreenEvent.Post event) {
        Screen screen = event.getGui();
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || overlayHandler == null || !isScreenValidForOverlay(screen)) {
            return;
        }

        PlayerInventory inventory = player.inventory;
        if (didInventoryChange(inventory)) {
            overlayHandler = new GuiScreenOverlayHandler(inventory);
            gui = new JecaGui(null, false, overlayHandler);
            gui.init(Minecraft.getInstance(), screen.width, screen.height);
        } else if (screen.width != gui.width || screen.height != gui.height) {
            gui.init(screen.getMinecraft(), screen.width, screen.height);
        }

        gui.setMatrix(event.getMatrixStack());
        int mouseX = gui.getGlobalMouseX();
        int mouseY = gui.getGlobalMouseY();

        RenderSystem.pushMatrix();
        RenderSystem.translatef(gui.getGuiLeft(), gui.getGuiTop(), 0);
        overlayHandler.onDraw(gui, mouseX, mouseY);
        RenderSystem.popMatrix();
//        RenderSystem.pushMatrix();
//        RenderSystem.translatef(0, 0, 80);
//        gui.hand.drawLabel(this, mouseX + guiLeft, mouseY + guiTop, true);
//        RenderSystem.popMatrix();
//        List<String> tooltip = new ArrayList<>();
//        root.onTooltip(this, mouseX, mouseY, tooltip);
//        drawHoveringText(matrixStack, tooltip, mouseX + guiLeft, mouseY + guiTop, font);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTooltip(RenderTooltipEvent.Pre event) {
        if (overlayHandler == null) {
            return;
        }

        boolean overlap = overlayHandler.onTooltip(gui, event.getX() - gui.getGuiLeft(), event.getY() - gui.getGuiTop(), new ArrayList<>());
        if (overlap && !event.getStack().isEmpty()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouse(GuiScreenEvent.MouseInputEvent event) {
        Screen screen = event.getGui();
        if (overlayHandler == null || !isScreenValidForOverlay(screen)) {
            return;
        }

        int xMouse = gui.getGlobalMouseX();
        int yMouse = gui.getGlobalMouseY();

        if (event instanceof GuiScreenEvent.MouseScrollEvent.Pre) {
            double diff = ((GuiScreenEvent.MouseScrollEvent) event).getScrollDelta();
            if (diff != 0) {
                overlayHandler.onMouseScroll(gui, xMouse, yMouse, (int) diff);
            }
        } else if (event instanceof GuiScreenEvent.MouseClickedEvent.Pre) {
            int button = ((GuiScreenEvent.MouseClickedEvent) event).getButton();
            if (overlayHandler.onMouseClicked(gui, xMouse, yMouse, button)) {
                event.setCanceled(true);
            }
        } else if (event instanceof GuiScreenEvent.MouseDragEvent.Pre) {
            GuiScreenEvent.MouseDragEvent mde = (GuiScreenEvent.MouseDragEvent) event;
            overlayHandler.onMouseDragged(gui, xMouse, yMouse, (int) mde.getDragX(), (int)mde.getDragY());
        } else if (event instanceof GuiScreenEvent.MouseReleasedEvent.Pre) {
            int button = ((GuiScreenEvent.MouseReleasedEvent) event).getButton();
            overlayHandler.onMouseReleased(gui, xMouse, yMouse, button);
        }
    }

    private boolean didInventoryChange(PlayerInventory inventory) {
        if (cachedInventory == null) {
            cacheInventory(inventory);
            return false;
        }

        if (!cachedInventory.mainInventory.equals(inventory.mainInventory)) {
            cacheInventory(inventory);
            return true;
        }

        if (!cachedInventory.offHandInventory.equals(inventory.offHandInventory)) {
            cacheInventory(inventory);
            return true;
        }

        if (!cachedInventory.armorInventory.equals(inventory.armorInventory)) {
            cacheInventory(inventory);
            return true;
        }

        return false;
    }

    private void cacheInventory(PlayerInventory inventory) {
        cachedInventory = new PlayerInventory(inventory.player);
        cachedInventory.copyInventory(inventory);
    }

    @Override
    public Collection<Rectangle2d> getGuiExtraAreas() {
        if (overlayHandler != null && gui != null) {
            return overlayHandler.getGuiExtraAreas(gui.getGuiLeft(), gui.getGuiTop());
        }
        return Collections.emptyList();
    }
}
