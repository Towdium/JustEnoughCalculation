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
import java.util.function.Function;

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
        drawText(xPos, yPos, f, (s) -> 0, text);
    }

    public void drawText(int xPos, int yPos, Font f, Function<String, Integer> indenter, String... text) {
        Single<Integer> y = new Single<>(0);
        boolean unicode = fontRenderer.getUnicodeFlag();
        if (!f.unicode) fontRenderer.setUnicodeFlag(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0);
        GlStateManager.scale(f.size, f.size, 1);
        Arrays.stream(text).forEachOrdered(s -> {
            fontRenderer.drawString(s, indenter.apply(s), y.value, f.color, f.shadow);
            y.value += fontRenderer.FONT_HEIGHT;
        });
        GlStateManager.popMatrix();
        fontRenderer.setUnicodeFlag(unicode);
    }

    public void drawText(int xPos, int yPos, int xSize, Font f, String... text) {
        float sizeScaled = xSize / f.size;
        int l = fontRenderer.getStringWidth("...");
        String[] ss = !f.cut ? text : Arrays.stream(text).map(s -> {
            int w = fontRenderer.getStringWidth(s);
            if (w <= sizeScaled) return s;
            else if (l >= w) return "...";
            else return fontRenderer.trimStringToWidth(s, (int) sizeScaled - l) + "...";
        }).toArray(String[]::new);
        if (f.centred) {
            drawText(xPos, yPos, f, (s) -> (int) ((sizeScaled - fontRenderer.getStringWidth(s)) / 2), ss);
        } else {
            int maxLen = Arrays.stream(ss).mapToInt(s -> fontRenderer.getStringWidth(s)).max().orElse(0);
            int xOffset = (int) ((sizeScaled - maxLen) / 2);
            drawText(xPos, yPos, f, (s) -> xOffset, ss);
        }
    }

    public void drawText(int xPos, int yPos, int xSize, int ySize, Font f, String... text) {
        int yOffset = (ySize - (int) (text.length * fontRenderer.FONT_HEIGHT * f.size)) / 2;
        drawText(xPos, yPos + yOffset, xSize, f, text);
    }

    public void drawTooltip(int xPos, int yPos, String... text) {
        drawTooltip(xPos, yPos, Arrays.asList(text));
    }

    public void drawTooltip(int xPos, int yPos, List<String> text) {
        drawHoveringText(text, xPos, yPos);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
    }

    public void localize(String key) {
        //LocalizationHelper.format(this.getClass(), )  // TODO
    }

    // function to override

    protected void drawExtra() {
    }

    public static class Font {
        public static final Font DEFAULT_SHADOW = new Font(0xFFFFFF, true, true, false, true, 1);
        public static final Font DEFAULT_NO_SHADOW = new Font(0x404040, false, true, false, true, 1);
        public static final Font DEFAULT_HALF = new Font(0xFFFFFF, true, true, true, true, 0.5f);

        public int color;
        public boolean shadow, cut, unicode, centred;
        public float size;

        /**
         * @param color   foreground color
         * @param shadow  whether to draw shadow
         * @param cut     whether to cut string when exceed xSize
         * @param unicode if false, FORCE NOT UNICODE
         * @param size    font size, 1 for default font size
         */
        public Font(int color, boolean shadow, boolean cut, boolean unicode, boolean centred, float size) {
            this.color = color;
            this.shadow = shadow;
            this.cut = cut;
            this.size = size;
            this.unicode = unicode;
            this.centred = centred;
        }

        public Font copy() {
            return new Font(color, shadow, cut, unicode, centred, size);
        }
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public class WidgetManager {
        protected List<Widget> widgets = new ArrayList<>();

        public void add(Widget w) {
            widgets.add(w);
        }

        public void remove(Widget w) {
            widgets.remove(w);
            if (w instanceof Widget.Advanced) ((Widget.Advanced) w).onRemoved(JecGui.this);
        }

        public void onInit() {
            widgets.stream().filter(w -> w instanceof Widget.Advanced)
                    .forEach(w -> ((Widget.Advanced) w).onGuiInit(JecGui.this));
        }

        public void onDraw(int mouseX, int mouseY) {
            widgets.forEach(widget -> widget.onDraw(JecGui.this, mouseX, mouseY));
        }

        public boolean onClick(int xMouse, int yMouse, int button) {
            return widgets.stream().filter(w -> w instanceof Widget.Advanced)
                    .anyMatch(w -> ((Widget.Advanced) w).onClicked(JecGui.this, xMouse, yMouse, button));
        }

        public boolean onKey(char ch, int code) {
            return widgets.stream().filter(w -> w instanceof Widget.Advanced)
                    .anyMatch(w -> ((Widget.Advanced) w).onKey(JecGui.this, ch, code));
        }
    }
}
