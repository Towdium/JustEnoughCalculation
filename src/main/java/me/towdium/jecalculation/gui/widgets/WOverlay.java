package me.towdium.jecalculation.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: Towdium
 * Date: 16/02/19
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WOverlay extends WContainer {
    @Override
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        if (super.onKeyPressed(gui, key, modifier)) return true;
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            gui.root.setOverlay(null);
            return true;
        } else return false;
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (!super.onMouseClicked(gui, xMouse, yMouse, button)) gui.root.setOverlay(null);
        return true;
    }

    @Override
    public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
        if (!super.onMouseScroll(gui, xMouse, yMouse, diff)) gui.root.setOverlay(null);
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onDraw(JecaGui gui, int mouseX, int mouseY) {
        gui.getMatrix().pushPose();
        gui.getMatrix().translate(0, 0, 180);
        super.onDraw(gui, mouseX, mouseY);
        gui.getMatrix().popPose();
        return false;
    }
}
