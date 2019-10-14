package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
    TextFieldWidget textField;
    public static final int HEIGHT = 20;

    public WTextField(int xPos, int yPos, int xSize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        textField = new TextFieldWidget(Minecraft.getInstance().fontRenderer, xPos + 1, yPos + 1, xSize - 2, 18, "WIP");
    }

    @Override
    public boolean onClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        textField.mouseClicked(xMouse, yMouse, button);
        if (textField.isFocused() && button == 1) {
            textField.setText("");
            notifyLsnr();
        }
        return false;
    }

    @Override
    public void onDraw(JecaGui gui, int xMouse, int yMouse) {
        textField.renderButton(0, 0, 0);
    }

    @Override
    public boolean onPressed(JecaGui gui, int key, int modifier) {
        boolean ret = textField.keyPressed(key, GLFW.glfwGetKeyScancode(key), modifier);
        if (ret) notifyLsnr();
        return ret;
    }

    @Override
    public boolean onReleased(JecaGui gui, int key, int modifier) {
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
        return textField.getText();
    }

    public WTextField setText(String s) {
        textField.setText(s);
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
