package me.towdium.jecalculation.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class WTextField implements IWidget {
    public ListenerAction<? super WTextField> listener;
    protected int xPos, yPos, xSize;
    GuiTextField textField;
    public static final int HEIGHT = 20;

    public WTextField(int xPos, int yPos, int xSize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        textField = new GuiTextField(Minecraft.getMinecraft().fontRenderer, xPos + 1, yPos + 1, xSize - 2, 18);
    }

    @Override
    public void onMouseFocused(JecaGui gui, int xMouse, int yMouse, int button) {
        textField.mouseClicked(xMouse, yMouse, button);
    }

    @Override
    public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
        textField.mouseClicked(xMouse, yMouse, button);
        if (textField.isFocused() && button == 1) {
            textField.setText("");
            notifyLsnr();
            return true;
        } else return false;
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        textField.drawTextBox();
        return false;
    }

    @Override
    public boolean onKeyPressed(JecaGui gui, char ch, int code) {
        boolean ret = textField.textboxKeyTyped(ch, code);
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
