package me.towdium.jecalculation.client.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WTextField implements IWidget {
    protected int xPos, yPos, xSize;
    GuiTextField textField;
    public Consumer<String> lsnrText;

    public WTextField(int xPos, int yPos, int xSize) {
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
        boolean ret = textField.textboxKeyTyped(ch, code);
        if (ret && lsnrText != null) lsnrText.accept(textField.getText());
        return ret;
    }

    public String getText() {
        return textField.getText();
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
