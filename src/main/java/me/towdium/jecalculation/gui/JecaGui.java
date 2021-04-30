package me.towdium.jecalculation.gui;

import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.drawables.WContainer;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class JecaGui extends GuiContainer {
    public static final int COLOR_GUI_GREY = 0xFFA1A1A1;
    public static final int COLOR_TEXT_RED = 0xFF0000;
    public static final int COLOR_TEXT_GREY = 0x404040;
    public static final int COLOR_TEXT_WHITE = 0xFFFFFF;
    public static final boolean ALWAYS_TOOLTIP = false;
    public ILabel hand = ILabel.EMPTY;
    public WContainer root;
    protected JecaGui parent;

    public JecaGui(@Nullable JecaGui parent, WContainer root) {
        this(parent, false, root);
    }

    public JecaGui(@Nullable JecaGui parent, boolean acceptsTransfer, WContainer root) {
        super(acceptsTransfer ? new ContainerTransfer() : new ContainerNonTransfer());
        this.parent = parent;
        this.root = root;
        if (inventorySlots instanceof JecContainer)
            ((JecContainer) inventorySlots).setGui(this);
    }

    public static boolean mouseIn(int xPos, int yPos, int xSize, int ySize, int xMouse, int yMouse) {
        return xMouse > xPos && yMouse > yPos && xMouse <= xPos + xSize && yMouse <= yPos + ySize;
    }

    public static void displayGui(WContainer root) {
        displayGui(true, false, root);
    }

    public static void displayGui(boolean updateParent, boolean acceptsTransfer, WContainer root) {
        // isCallingFromMinecraftThread
        if (Minecraft.getMinecraft().func_152345_ab())
            displayGuiUnsafe(updateParent, acceptsTransfer, root);
    }

    /**
     * @return The currently displayed {@link JecaGui}
     * Make sure the method is called when a {@link JecaGui} is displayed!
     * Otherwise it will throw a {@link NullPointerException}
     */
    public static JecaGui getCurrent() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        JecaGui ret = gui instanceof JecaGui ? (JecaGui) gui : null;
        Objects.requireNonNull(ret);
        return ret;
    }



    private static void displayGuiUnsafe(boolean updateParent, boolean acceptsTransfer, WContainer root) {
        Minecraft mc = Minecraft.getMinecraft();
        JecaGui parent;
        if (mc.currentScreen == null)
            parent = null;
        else if (!(mc.currentScreen instanceof JecaGui))
            parent = getCurrent();
        else if (updateParent)
            parent = (JecaGui) mc.currentScreen;
        else
            parent = ((JecaGui) mc.currentScreen).parent;
        JecaGui toShow = new JecaGui(parent, acceptsTransfer, root);
        mc.displayGuiScreen(toShow);
    }

    public static void displayParent() {
        // isCallingFromMinecraftThread
        if (Minecraft.getMinecraft().func_152345_ab()) {
            Minecraft.getMinecraft().displayGuiScreen(getCurrent().parent);
        }
    }

    public static boolean isShiftDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        root.onDraw(this, mouseX - guiLeft, mouseY - guiTop);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 80);
        hand.drawLabel(this, mouseX, mouseY, true);
        GlStateManager.popMatrix();
        List<String> tooltip = new ArrayList<>();
        root.onTooltip(this, mouseX - guiLeft, mouseY - guiTop, tooltip);
        drawHoveringText(tooltip, mouseX, mouseY);
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
        int button = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            if (button == 0) {
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
            } else if (button == 1) {
                if (hand != ILabel.EMPTY) {
                    hand = ILabel.EMPTY;
                    return true;
                }
            }
        }
        return false;
    }

    public void drawResource(Resource r, int xPos, int yPos) {
        drawResource(r, xPos, yPos, 0xFFFFFF);
    }

    public void drawResource(Resource r, int xPos, int yPos, int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (~(color >> 24) & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
        mc.getTextureManager().bindTexture(r.getResourceLocation());
        drawTexturedModalRect(xPos, yPos, r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize());
    }

    public void drawResourceContinuous(Resource r, int xPos, int yPos, int xSize, int ySize, int border) {
        drawResourceContinuous(r, xPos, yPos, xSize, ySize, border, border, border, border);
    }

    public void drawResourceContinuous(
            Resource r, int xPos, int yPos, int xSize, int ySize,
            int borderTop, int borderBottom, int borderLeft, int borderRight) {
        GuiUtils.drawContinuousTexturedBox(r.getResourceLocation(), xPos, yPos, r.getXPos(), r.getYPos(), xSize, ySize,
                                           r.getXSize(), r.getYSize(), borderTop, borderBottom, borderLeft, borderRight, zLevel);
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

    public void drawSplitText(float xPos, float yPos, Font f, String s) {
        drawSplitText(xPos, yPos, Integer.MAX_VALUE, f, s);
    }

    public void drawSplitText(float xPos, float yPos, int width, Font f, String s) {
        drawSplitText(xPos, yPos, f, Utilities.I18n.wrap(s, width));
    }

    public void drawSplitText(float xPos, float yPos, Font f, List<String> ss) {
        drawText(xPos, yPos, f, () -> {
            int y = 0;
            for (String i : ss) {
                fontRendererObj.drawString(i, 0, y, f.color, f.shadow);
                y += fontRendererObj.FONT_HEIGHT + 1;
            }
        });
    }

    public void drawText(float xPos, float yPos, Font f, String s) {
        drawText(xPos, yPos, Integer.MAX_VALUE, f, s);
    }

    public void drawText(float xPos, float yPos, int width, Font f, String s) {
        drawText(xPos, yPos, f, () -> {
            String str = s;
            int strWidth = f.getTextWidth(str);
            int ellipsisWidth = f.getTextWidth("...");
            if (strWidth > width && strWidth > ellipsisWidth)
                str = f.trimToWidth(str, width - ellipsisWidth).trim() + "...";
            fontRendererObj.drawString(str, 0, 0, f.color, f.shadow);
        });
    }

    private void drawText(float xPos, float yPos, Font f, Runnable r) {
        boolean unicode = fontRendererObj.getUnicodeFlag();
        if (f.half) fontRendererObj.setUnicodeFlag(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0);
        if (f.half) GlStateManager.scale(0.5, 0.5, 1);
        r.run();
        GlStateManager.popMatrix();
        fontRendererObj.setUnicodeFlag(unicode);
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

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        JustEnoughCalculation.logger.info("mouse clicked");
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean inGui = root.onClicked(this, mouseX - guiLeft, mouseY - guiTop, mouseButton);
        if (!inGui) {
            this.handleMouseEvent();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && hand != ILabel.EMPTY) hand = ILabel.EMPTY;
        else if (!root.onKey(this, typedChar, keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE && parent != null) displayParent();
            else super.keyTyped(typedChar, keyCode);
        }

    }

    public static class Font {
        public static final Font SHADOW = new Font(JecaGui.COLOR_TEXT_WHITE, true, false);
        public static final Font PLAIN = new Font(JecaGui.COLOR_TEXT_GREY, false, false);
        public static final Font HALF = new Font(JecaGui.COLOR_TEXT_WHITE, true, true);

        public int color;
        public boolean shadow, half;

        public Font(int color, boolean shadow, boolean half) {
            this.color = color;
            this.shadow = shadow;
            this.half = half;
        }

        public int getTextWidth(String s) {
            return (int) Math.ceil(getCurrent().fontRendererObj.getStringWidth(s) * (half ? 0.5f : 1));
        }

        public int getTextHeight() {
            return (int) Math.ceil(getCurrent().fontRendererObj.FONT_HEIGHT * (half ? 0.5f : 1));
        }

        public String trimToWidth(String s, int i) {
            return getCurrent().fontRendererObj.trimStringToWidth(s, i * (half ? 2 : 1));
        }
    }


    public static class JecContainer extends Container {
        JecaGui gui;

        public JecaGui getGui() {
            return gui;
        }

        public void setGui(JecaGui gui) {
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
