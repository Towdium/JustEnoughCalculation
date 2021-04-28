package me.towdium.jecalculation.client.gui;

import cpw.mods.fml.client.config.GuiUtils;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.IllegalPositionException;
import me.towdium.jecalculation.utils.wrappers.Single;
import me.towdium.jecalculation.utils.wrappers.Triple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
public class JecGui extends GuiContainer {
    public static final int COLOR_GUI_GREY = 0xFFA1A1A1;
    public static final int COLOR_TEXT_RED = 0xFF0000;
    public static final int COLOR_TEXT_WHITE = 0xFFFFFF;
    public static final boolean ALWAYS_TOOLTIP = false;

    protected JecGui parent;
    public ILabel hand = ILabel.EMPTY;
    public IWidget root;
    protected List<Triple<Integer, Integer, List<String>>> tooltipBuffer = new ArrayList<>();

    public JecGui(@Nullable JecGui parent, IWidget root) {
        this(parent, false, root);
    }

    public JecGui(@Nullable JecGui parent, boolean acceptsTransfer, IWidget root) {
        super(acceptsTransfer ? new ContainerTransfer() : new ContainerNonTransfer());
        this.parent = parent;
        this.root = root;
        if (inventorySlots instanceof JecContainer)
            ((JecContainer) inventorySlots).setGui(this);
    }

    public static boolean mouseIn(int xPos, int yPos, int xSize, int ySize, int xMouse, int yMouse) {
        return xMouse > xPos && yMouse > yPos && xMouse <= xPos + xSize && yMouse <= yPos + ySize;
    }

    public static void displayGui(IWidget root) {
        displayGui(true, false, root);
    }

    public static void displayGui(boolean updateParent, boolean acceptsTransfer, IWidget root) {
        // isCallingFromMinecraftThread
        if (Minecraft.getMinecraft().func_152345_ab())
            displayGuiUnsafe(updateParent, acceptsTransfer, root);
    }

    /**
     * @return The currently displayed {@link JecGui}
     * Make sure the method is called when a {@link JecGui} is displayed!
     * Otherwise it will throw a {@link NullPointerException}
     */
    public static JecGui getCurrent() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        JecGui ret = gui instanceof JecGui ? (JecGui) gui : null;
        Objects.requireNonNull(ret);
        return ret;
    }

    private static void displayGuiUnsafe(boolean updateParent, boolean acceptsTransfer, IWidget root) {
        Minecraft mc = Minecraft.getMinecraft();
        JecGui parent;
        if (mc.currentScreen == null)
            parent = null;
        else if (!(mc.currentScreen instanceof JecGui))
            parent = getCurrent();
        else if (updateParent)
            parent = (JecGui) mc.currentScreen;
        else
            parent = ((JecGui) mc.currentScreen).parent;
        JecGui toShow = new JecGui(parent, acceptsTransfer, root);
        mc.displayGuiScreen(toShow);
    }

    public static void displayParent() {
        // isCallingFromMinecraftThread
        if (Minecraft.getMinecraft().func_152345_ab()) {
            Minecraft.getMinecraft().displayGuiScreen(getCurrent().parent);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        root.onDraw(this, mouseX - guiLeft, mouseY - guiTop);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(mouseX - 8, mouseY - 8, 80);
        hand.drawLabel(this);
        GlStateManager.popMatrix();
        drawBufferedTooltip();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    /**
     * This function handles events within the interface.
     * Specifically, handles mouse wheel within interface.
     * Different from {@link #handleMouseEvent()}, which is
     * used to handle mouse event outside.
     */
    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int diff = Mouse.getEventDWheel() / 120;
        if (diff != 0)
            root.onScroll(this, Mouse.getEventX() * width / mc.displayWidth - guiLeft,
                          height - Mouse.getEventY() * height / mc.displayHeight - 1 - guiTop, diff);
    }

    /**
     * @return if the event is canceled
     * This function handles click outside the rNormal region,
     * especially the overlap with JEI overlay. It handles
     * mouse event before JEI.
     */
    public boolean handleMouseEvent() {
        JustEnoughCalculation.logger.info("handle mouse clicked");
        int xMouse = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int yMouse = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (Mouse.getEventButtonState()) {
            if (Mouse.getEventButton() == 0) {
                if (hand == ILabel.EMPTY) {
                    ILabel e = NEIPlugin.getLabelUnderMouse();
                    if (e != ILabel.EMPTY) {
                        hand = e;
                        return true;
                    }
                } else {
                    if (!mouseIn(guiLeft, guiTop, width, height, xMouse, yMouse)) {
                        hand = ILabel.EMPTY;
                        return true;
                    }
                }
            } else if (Mouse.getEventButton() == 1) {
                if (hand != ILabel.EMPTY) {
                    hand = ILabel.EMPTY;
                    return true;
                }
            }
        }
        return false;
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

    public void drawResource(Resource r, int xPos, int yPos) {
        drawResource(r, xPos, yPos, 0xFFFFFF);
    }

    public void drawResource(Resource r, int xPos, int yPos, int color) {
        drawTexture(r.getResourceLocation(), xPos, yPos, r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize(), color);
    }

    public void drawResourceContinuous(Resource r,
                                       int xPos,
                                       int yPos,
                                       int xSize,
                                       int ySize,
                                       int borderTop,
                                       int borderBottom,
                                       int borderLeft,
                                       int borderRight) {
        drawTextureContinuous(r.getResourceLocation(), xPos, yPos, xSize, ySize, r.getXPos(), r.getYPos(), r.getXSize(),
                              r.getYSize(), borderTop, borderBottom, borderLeft, borderRight);
    }

    public void drawTexture(ResourceLocation l,
                            int destXPos,
                            int destYPos,
                            int sourceXPos,
                            int sourceYPos,
                            int sourceXSize,
                            int sourceYSize) {
        drawTexture(l, destXPos, destYPos, sourceXPos, sourceYPos, sourceXSize, sourceYSize, 0xFFFFFF);
    }

    public void drawTexture(ResourceLocation l,
                            int destXPos,
                            int destYPos,
                            int sourceXPos,
                            int sourceYPos,
                            int sourceXSize,
                            int sourceYSize,
                            int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (~(color >> 24) & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
        mc.getTextureManager().bindTexture(l);
        drawTexturedModalRect(destXPos, destYPos, sourceXPos, sourceYPos, sourceXSize, sourceYSize);
    }

    public void drawFluid(Fluid f, int xPos, int yPos, int xSize, int ySize) {
        IIcon fluidStillIcon = f.getStillIcon();
        mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        int color = f.getColor();
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, 1.0F);
        if (fluidStillIcon != null)
            drawTexturedModelRectFromIcon(xPos, yPos, fluidStillIcon, xSize, ySize);
    }

    public void drawTextureContinuous(ResourceLocation l,
                                      int destXPos,
                                      int destYPos,
                                      int destXSize,
                                      int destYSize,
                                      int sourceXPos,
                                      int sourceYPos,
                                      int sourceXSize,
                                      int sourceYSize,
                                      int borderTop,
                                      int borderBottom,
                                      int borderLeft,
                                      int borderRight) {
        GuiUtils.drawContinuousTexturedBox(l, destXPos, destYPos, sourceXPos, sourceYPos, destXSize, destYSize,
                                           sourceXSize, sourceYSize, borderTop, borderBottom, borderLeft, borderRight,
                                           zLevel);
    }

    public void drawRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        int right = xPos + xSize;
        int bottom = yPos + ySize;
        Tessellator tessellator = Tessellator.instance;
        GlStateManager.disableTexture2D();
        GlStateManager.color(f, f1, f2, f3);
        GlStateManager.disableAlpha();
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) xPos, (double) bottom, 0.0D);
        tessellator.addVertex((double) right, (double) bottom, 0.0D);
        tessellator.addVertex((double) right, (double) yPos, 0.0D);
        tessellator.addVertex((double) xPos, (double) yPos, 0.0D);
        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public void drawText(float xPos, float yPos, Font f, String... text) {
        Function<String, Integer> indenter;
        switch (f.align) {
            case AUTO:
            case LEFT:
                indenter = s -> 0;
                break;
            case CENTRE:
                indenter = s -> fontRendererObj.getStringWidth(s) / 2;
                break;
            case RIGHT:
                indenter = s -> -fontRendererObj.getStringWidth(s);
                break;
            default:
                throw new IllegalPositionException();
        }
        drawText(xPos, yPos, f, indenter, text);
    }

    private void drawText(float xPos, float yPos, Font f, Function<String, Integer> indenter, String... text) {
        Single<Integer> y = new Single<>(0);
        boolean unicode = fontRendererObj.getUnicodeFlag();
        if (!f.unicode)
            fontRendererObj.setUnicodeFlag(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0);
        GlStateManager.scale(f.size, f.size, 1);
        Arrays.stream(text).forEachOrdered(s -> {
            fontRendererObj.drawString(s, indenter.apply(s), y.value, f.color, f.shadow);
            y.value += fontRendererObj.FONT_HEIGHT;
        });
        GlStateManager.popMatrix();
        fontRendererObj.setUnicodeFlag(unicode);
    }

    public void drawText(float xPos, float yPos, float xSize, Font f, String... text) {
        float sizeScaled = xSize / f.size;
        int l = fontRendererObj.getStringWidth("...");
        String[] ss = !f.cut ? text : Arrays.stream(text).map(s -> {
            int w = fontRendererObj.getStringWidth(s);
            if (w <= sizeScaled)
                return s;
            else if (l >= w)
                return "...";
            else
                return fontRendererObj.trimStringToWidth(s, (int) (sizeScaled - l)) + "...";
        }).toArray(String[]::new);
        switch (f.align) {
            case LEFT:
                drawText(xPos, yPos, f, s -> 0, ss);
                break;
            case AUTO:
            case CENTRE:
                drawText(xPos, yPos, f, s -> ((int) sizeScaled - fontRendererObj.getStringWidth(s)) / 2, ss);
                break;
            case RIGHT:
                drawText(xPos, yPos, f, s -> (int) sizeScaled - fontRendererObj.getStringWidth(s), ss);
                break;
            default:
                throw new IllegalPositionException();
        }
    }

    public void drawText(float xPos, float yPos, int xSize, int ySize, Font f, String... text) {
        int yOffset = (ySize - (int) (text.length * fontRendererObj.FONT_HEIGHT * f.size)) / 2;
        drawText(xPos, yPos + yOffset, xSize, f, text);
    }

    public void drawTooltip(int xPos, int yPos, String... text) {
        drawTooltip(xPos, yPos, Arrays.asList(text));
    }

    public void drawTooltip(int xPos, int yPos, List<String> text) {
        tooltipBuffer.add(new Triple<>(xPos + guiLeft, yPos + guiTop, text));
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
        if (is.getItem() == null) {
            return;
        }
        if (centred) {
            xPos -= 8;
            yPos -= 8;
        }
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), is, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
    }

    public static class Font {
        public static final Font DEFAULT_SHADOW = new Font(0xFFFFFF, true, true, false, 1, enumAlign.AUTO);
        public static final Font DEFAULT_NO_SHADOW = new Font(0x404040, false, true, false, 1, enumAlign.AUTO);
        public static final Font DEFAULT_HALF = new Font(0xFFFFFF, true, true, true, 0.5f, enumAlign.AUTO);

        public int color;
        public boolean shadow, cut, unicode;
        public float size;
        public enumAlign align;

        /**
         * @param color   foreground color
         * @param shadow  whether to draw shadow
         * @param cut     whether to cut string when exceed xSize
         * @param unicode if false, FORCE NOT UNICODE
         * @param size    font size, 1 for default font size
         */
        public Font(int color, boolean shadow, boolean cut, boolean unicode, float size, enumAlign align) {
            this.color = color;
            this.shadow = shadow;
            this.cut = cut;
            this.size = size;
            this.align = align;
            this.unicode = unicode;
        }

        public Font copy() {
            return new Font(color, shadow, cut, unicode, size, align);
        }

        public enum enumAlign {LEFT, CENTRE, RIGHT, AUTO}
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean inGui = root.onClicked(this, mouseX - guiLeft, mouseY - guiTop, mouseButton);
        if (!inGui) {
            this.handleMouseEvent();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!root.onKey(this, typedChar, keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                if (hand != ILabel.EMPTY)
                    hand = ILabel.EMPTY;
                else if (parent != null)
                    Minecraft.getMinecraft().displayGuiScreen(parent);
                else
                    super.keyTyped(typedChar, keyCode);
            }
        }
    }

    public static boolean isShiftDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static class JecContainer extends Container {
        JecGui gui;

        public JecGui getGui() {
            return gui;
        }

        public void setGui(JecGui gui) {
            this.gui = gui;
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    }

    public static class ContainerTransfer extends JecContainer {
    }

    public static class ContainerNonTransfer extends JecContainer {
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    public void drawHoveringText(String text, int x, int y) {
        super.drawHoveringText(Arrays.asList(text), x, y, fontRendererObj);
    }

    public void drawHoveringText(List<String> textLines, int x, int y) {
        super.drawHoveringText(textLines, x, y, fontRendererObj);
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }
}
