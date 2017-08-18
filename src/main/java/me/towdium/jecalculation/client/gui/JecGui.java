package me.towdium.jecalculation.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;
import me.towdium.jecalculation.utils.wrappers.Single;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JecGui extends GuiContainer {
    public static final int COLOR_GREY = 0xFFA1A1A1;
    public static final int COLOR_BLUE = 0xFFb0b9e6;

    public List<GuiButton> buttonList = super.buttonList;
    public WidgetManager widgetManager = new WidgetManager();
    GuiScreen parent;

    public JecGui(@Nullable GuiScreen parent) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer playerIn) {
                return true;
            }
        });
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        widgetManager.onInit();
    }

    public static boolean mouseIn(int xPos, int yPos, int xSize, int ySize, int xMouse, int yMouse) {
        return xMouse > xPos && yMouse > yPos && xMouse <= xPos + xSize && yMouse <= yPos + ySize;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        widgetManager.onDraw(mouseX, mouseY);
        drawExtra();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawResourceContinuous(Resource.WIDGET_PANEL, guiLeft, guiTop, xSize, ySize, 5, 5, 5, 5);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        widgetManager.onClick(mouseX, mouseY, mouseButton);
    }

    // new functions

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        widgetManager.onKey(typedChar, keyCode);
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public void drawResource(Resource r, int xPos, int yPos) {
        drawTexture(r.getResourceLocation(), xPos, yPos, r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize());
    }

    public void drawResourceContinuous(
            Resource r, int xPos, int yPos, int xSize, int ySize,
            int borderTop, int borderBottom, int borderLeft, int borderRight) {
        drawTextureContinuous(r.getResourceLocation(), xPos, yPos, xSize, ySize,
                r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize(),
                borderTop, borderBottom, borderLeft, borderRight);
    }

    public void drawTexture(ResourceLocation l, int destXPos, int destYPos,
                            int sourceXPos, int sourceYPos, int sourceXSize, int sourceYSize) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(l);
        this.drawTexturedModalRect(destXPos, destYPos, sourceXPos, sourceYPos, sourceXSize, sourceYSize);
    }

    public void drawTextureContinuous(
            ResourceLocation l, int destXPos, int destYPos, int destXSize, int destYSize,
            int sourceXPos, int sourceYPos, int sourceXSize, int sourceYSize,
            int borderTop, int borderBottom, int borderLeft, int borderRight) {
        GuiUtils.drawContinuousTexturedBox(l, destXPos, destYPos, sourceXPos, sourceYPos, destXSize, destYSize,
                sourceXSize, sourceYSize, borderTop, borderBottom, borderLeft, borderRight, zLevel);
    }

    public void drawRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        drawRect(xPos, yPos, xPos + xSize, yPos + ySize, color);
    }

    public void drawText(int xPos, int yPos, Font f, String... text) {
        Single<Integer> y = new Single<>(yPos);
        Arrays.stream(text).forEachOrdered(s -> {
            fontRenderer.drawString(s, xPos, y.value, f.color, f.shadow);
            y.value += fontRenderer.FONT_HEIGHT;
        });
    }

    public void drawText(int xPos, int yPos, int xSize, Font f, String... text) {
        int l = fontRenderer.getStringWidth("...");
        String[] ss = !f.cut ? text : Arrays.stream(text).map(s -> {
            int w = fontRenderer.getStringWidth(s);
            if (w <= xSize) return s;
            else if (l >= w) return "...";
            else return fontRenderer.trimStringToWidth(s, xSize - l) + "...";
        }).toArray(String[]::new);
        int xOffset = (xSize - Arrays.stream(ss).mapToInt(s -> fontRenderer.getStringWidth(s)).max().orElse(0)) / 2;
        drawText(xPos + xOffset, yPos, f, ss);
    }

    public void drawText(int xPos, int yPos, int xSize, int ySize, Font f, String... text) {
        int yOffset = (ySize - text.length * fontRenderer.FONT_HEIGHT) / 2;
        drawText(xPos, yPos + yOffset, xSize, f, text);
    }

    public void localize(String key) {
        //LocalizationHelper.format(this.getClass(), )
    }

    // function to override

    protected void drawExtra() {
    }

    public static class Font {
        public static final Font DEFAULT_SHADOW = new Font(0xA0A0A0, true, true);
        public static final Font DEFAULT_NO_SHADOW = new Font(0x404040, false, true);

        protected int color;
        protected boolean shadow, cut;

        public Font(int color, boolean shadow, boolean cut) {
            this.color = color;
            this.shadow = shadow;
            this.cut = cut;
        }
    }

    public class WidgetManager {
        protected List<Widget> widgets = new ArrayList<>();

        public void add(Widget w) {
            widgets.add(w);
        }

        public void remove(Widget w) {
            widgets.remove(w);
            w.onRemoved(JecGui.this);
        }

        public void onInit() {
            widgets.forEach(w -> w.onGuiInit(JecGui.this));
        }

        public void onDraw(int mouseX, int mouseY) {
            widgets.forEach(widget -> widget.onDraw(JecGui.this, mouseX, mouseY));
        }

        @SuppressWarnings("UnusedReturnValue")
        public boolean onClick(int xMouse, int yMouse, int button) {
            return widgets.stream().anyMatch(w -> w.onClicked(JecGui.this, xMouse, yMouse, button));
        }

        @SuppressWarnings("UnusedReturnValue")
        public boolean onKey(char ch, int code) {
            return widgets.stream().anyMatch(w -> w.onKey(JecGui.this, ch, code));
        }
    }
}
