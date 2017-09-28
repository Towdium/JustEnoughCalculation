package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IWidget;
import me.towdium.jecalculation.client.gui.JecGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WTextField implements IWidget {
    protected int xPos, yPos, xSize;
    GuiTextField textField;
    public Consumer<String> lsnrText;

    public WTextField(int xPos, int yPos, int xSize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        textField = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, xPos + 1, yPos + 1, xSize - 2, 18);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        return textField.mouseClicked(xMouse, yMouse, button);
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        textField.drawTextBox();
    }

    @Override
    public boolean onKey(JecGui gui, char ch, int code) {
        boolean ret = textField.textboxKeyTyped(ch, code);
        if (ret) lsnrText.accept(textField.getText());
        return ret;
    }

    @SuppressWarnings("UnusedReturnValue")
    public WTextField setLsnrText(Consumer<String> lsnrText) {
        this.lsnrText = lsnrText;
        return this;
    }

    public WTextField setColor(int color) {
        textField.setTextColor(color);
        return this;
    }
}
