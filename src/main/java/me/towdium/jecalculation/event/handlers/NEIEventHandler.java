package me.towdium.jecalculation.event.handlers;

import codechicken.nei.guihook.IContainerInputHandler;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.client.gui.inventory.GuiContainer;

public class NEIEventHandler implements IContainerInputHandler {
    @Override
    public boolean keyTyped(GuiContainer guiContainer, char c, int i) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer guiContainer, char c, int i) {

    }

    @Override
    public boolean lastKeyTyped(GuiContainer guiContainer, char c, int i) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer guiContainer, int mouseX, int mouseY, int button) {
        return JecaGui.onMouse();
    }

    @Override
    public void onMouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public void onMouseUp(GuiContainer guiContainer, int i, int i1, int i2) {
        JecaGui.onMouseReleased();
    }

    @Override
    public boolean mouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {
        return JecaGui.onMouse();
    }

    @Override
    public void onMouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public void onMouseDragged(GuiContainer guiContainer, int i, int i1, int i2, long l) {

    }
}
