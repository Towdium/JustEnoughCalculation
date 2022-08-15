package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WTextField implements IWidget {
    public ListenerAction<? super WTextField> listener;
    protected int xPos, yPos, xSize;
    EditBox textField;
    public static final int HEIGHT = 20;

    public WTextField(int xPos, int yPos, int xSize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        textField = new EditBox(Minecraft.getInstance().font, xPos + 1, yPos + 1, xSize - 2, 18, new TextComponent("WIP"));
    }

    @Override
    public void onMouseFocused(JecaGui gui, int xMouse, int yMouse, int button) {
        textField.mouseClicked(xMouse, yMouse, button);
    }

    @Override
    public void onTick(JecaGui gui) {
        textField.tick();
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        if (textField.isFocused() && button == 1) {
            textField.setValue("");
            notifyLsnr();
            return true;
        } else return false;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        textField.renderButton(gui.getMatrix(), xPos, yPos, 0);
        return false;
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
        boolean ret = textField.keyPressed(key, GLFW.glfwGetKeyScancode(key), modifier);
        if (ret) notifyLsnr();
        return textField.isFocused() && textField.isVisible() && key != 256;
    }

    @Override
    public boolean onKeyReleased(JecaGui gui, int key, int modifier) {
        textField.keyReleased(key, GLFW.glfwGetKeyScancode(key), modifier);
        return false;
    }

    @Override
    public boolean onChar(JecaGui gui, char ch, int modifier) {
        boolean ret = textField.charTyped(ch, modifier);
        if (ret) notifyLsnr();
        return ret;
    }

    public String getText() {
        return textField.getValue();
    }

    public WTextField setText(String s) {
        textField.setValue(s);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public WTextField setListener(ListenerAction<? super WTextField> listener) {
        this.listener = listener;
        return this;
    }

    public WTextField setColor(int color) {
        textField.setTextColor(color);
        return this;
    }

    protected void notifyLsnr() {
        if (listener != null) listener.invoke(this);
    }
}
