package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
public class DTextField implements IDrawable {
    protected int xPos, yPos, xSize;
    GuiTextField textField;
    public DTextField(int xPos, int yPos, int xSize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        textField = new GuiTextField(Minecraft.getMinecraft().fontRenderer, xPos + 1, yPos + 1, xSize - 2, 18);
    }

    @Override
    public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
        boolean flag = xMouse >= textField.xPosition && xMouse < textField.xPosition + textField.width &&
                       yMouse >= textField.yPosition && yMouse < textField.yPosition + textField.height;
        textField.mouseClicked(xMouse, yMouse, button);
        return textField.isFocused() && flag && button == 0;
    }

    @Override
    public void onDraw(JecGui gui, int xMouse, int yMouse) {
        textField.drawTextBox();
    }

    @Override
    public boolean onKey(JecGui gui, char ch, int code) {
        return textField.textboxKeyTyped(ch, code);
    }
}
