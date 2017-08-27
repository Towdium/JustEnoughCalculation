package me.towdium.jecalculation.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;
import me.towdium.jecalculation.client.widget.widgets.WEntry;
import me.towdium.jecalculation.client.widget.widgets.WEntryGroup;
import me.towdium.jecalculation.core.entry.Entry;
import me.towdium.jecalculation.jei.JecPlugin;
import me.towdium.jecalculation.utils.helpers.LocalizationHelper;
import me.towdium.jecalculation.utils.wrappers.Single;
import me.towdium.jecalculation.utils.wrappers.Triple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    public WidgetManager wgtMgr = new WidgetManager();
    public Entry hand = Entry.EMPTY;
    protected GuiScreen parent;
    protected List<Triple<Integer, Integer, List<String>>> tooltipBuffer = new ArrayList<>();

    public JecGui(@Nullable GuiScreen parent) {
        this(parent, false);
    }

    public JecGui(@Nullable GuiScreen parent, boolean acceptsTransfer) {
        super(acceptsTransfer ? new ContainerTransfer() : new ContainerNonTransfer());
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        wgtMgr.onInit();
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
        wgtMgr.onDraw(mouseX, mouseY);
        drawExtra();
        drawItemStack(mouseX, mouseY, hand.getRepresentation(), true);
        drawBufferedTooltip();
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
        wgtMgr.onClick(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!wgtMgr.onKey(typedChar, keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                if (hand != Entry.EMPTY) hand = Entry.EMPTY;
                else if (parent != null) Minecraft.getMinecraft().displayGuiScreen(parent);
                else super.keyTyped(typedChar, keyCode);
            }
        }
    }


    /**
     * @return if the event is canceled
     * This function handles click outside the normal region,
     * especially the overlap with JEI overlay. It handles
     * mouse event before JEI.
     */
    public boolean handleMouseEvent() {
        int xMouse = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int yMouse = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (Mouse.getEventButtonState()) {
            if (Mouse.getEventButton() == 0) {
                if (hand == Entry.EMPTY) {
                    Entry e = JecPlugin.getEntryUnderMouse();
                    if (e != Entry.EMPTY) {
                        hand = e;
                        return true;
                    }
                } else {
                    if (!mouseIn(guiLeft, guiTop, width, height, xMouse, yMouse)) {
                        hand = Entry.EMPTY;
                        return true;
                    }
                }
            } else if (Mouse.getEventButton() == 1) {
                if (hand != Entry.EMPTY) {
                    hand = Entry.EMPTY;
                    return true;
                }
            }
        }
        return false;
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
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        int right = xPos + xSize;
        int bottom = yPos + ySize;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double) xPos, (double) bottom, 0.0D).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
        bufferbuilder.pos((double) right, (double) yPos, 0.0D).endVertex();
        bufferbuilder.pos((double) xPos, (double) yPos, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
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
        tooltipBuffer.add(new Triple<>(xPos, yPos, text));
    }

    protected void drawBufferedTooltip() {
        tooltipBuffer.forEach(i -> {
            drawHoveringText(i.three, i.one, i.two);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
        });
        tooltipBuffer.clear();
    }

    public void drawItemStack(int xPos, int yPos, ItemStack is, boolean centred) {
        if (centred) {
            xPos -= 8;
            yPos -= 8;
        }
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemIntoGUI(is, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
    }

    public String localize(String key, Object... os) {
        return LocalizationHelper.format(this.getClass(), "gui", key, os);
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

    public static class ContainerTransfer extends Container {
        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }

    public static class ContainerNonTransfer extends Container {
        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
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

        public Optional<WEntry> getEntryAt(int xMouse, int yMouse) {
            return widgets.stream().map(w -> {
                if (w instanceof WEntryGroup) return ((WEntryGroup) w).getEntryAt(JecGui.this, xMouse, yMouse);
                else if (w instanceof WEntry) return Optional.ofNullable(
                        ((WEntry) w).mouseIn(JecGui.this, xMouse, yMouse) ? ((WEntry) w) : null);
                else return Optional.<WEntry>empty();
            }).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
        }
    }
}