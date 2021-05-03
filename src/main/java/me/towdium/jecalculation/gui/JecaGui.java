package me.towdium.jecalculation.gui;

import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.network.ClientHandler;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static codechicken.lib.gui.GuiDraw.renderEngine;

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
    protected static JecaGui last;
    protected static Runnable scheduled;
    protected static int timeout;
    protected JecaGui parent;
    public IGui root;

    public JecaGui(@Nullable JecaGui parent, IGui root) {
        this(parent, false, root);
    }

    public JecaGui(@Nullable JecaGui parent, boolean acceptsTransfer, IGui root) {
        super(acceptsTransfer ? new ContainerTransfer() : new ContainerNonTransfer());
        this.parent = parent;
        this.root = root;
        if (inventorySlots instanceof JecContainer)
            ((JecContainer) inventorySlots).setGui(this);
    }

    public static int getMouseX() {
        JecaGui gui = getCurrent();
        return Mouse.getEventX() * gui.width / gui.mc.displayWidth - gui.guiLeft;
    }

    public static int getMouseY() {
        JecaGui gui = getCurrent();
        return gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1 - gui.guiTop;
    }


    /**
     * @return true to terminate further processing of this event.
     */
    public static boolean onMouse() {
        Optional<JecaGui> optionalJecaGui = currentJecaGui();
        if (!optionalJecaGui.isPresent())
            return false;
        JecaGui gui = optionalJecaGui.get();
        int mouseX = getMouseX();
        int mouseY = getMouseY();
        int button = Mouse.getEventButton();
        if (button == -1) {
            int diff = Mouse.getEventDWheel() / 120;
            if (diff != 0)
                gui.root.onScroll(gui, mouseX, mouseY, diff);
        } else if (Mouse.getEventButtonState()) {
            if (gui.root.onClicked(gui, mouseX, mouseY, button)) {
                return true;
            } else if (gui.hand != ILabel.EMPTY) {
                gui.hand = button == 0 ? NEIPlugin.getLabelUnderMouse() : ILabel.EMPTY;
                return true;
            } else if (button == 0) {
                ILabel e = NEIPlugin.getLabelUnderMouse();
                if (e != ILabel.EMPTY) {
                    gui.hand = e;
                    return true;
                }
            }
        }
        return false;
    }


    @Nullable
    public Slot getSlotUnderMouse() {
        IInventory i = new InventoryBasic("", false, 1);
        Slot s = new Slot(i, 0, 0, 0);
        ILabel l = getLabelUnderMouse();
        if (l instanceof LItemStack)
            s.putStack(((LItemStack) l).getRep());
        return s;
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    public static boolean mouseIn(int xPos, int yPos, int xSize, int ySize, int xMouse, int yMouse) {
        return xMouse > xPos && yMouse > yPos && xMouse <= xPos + xSize && yMouse <= yPos + ySize;
    }

    public static void displayGui(IGui root) {
        displayGui(true, false, root);
    }

    public static void displayGui(boolean updateParent, boolean acceptsTransfer, IGui root) {
        displayGui(updateParent, acceptsTransfer, false, root);
    }

    public static void displayGui(boolean updateParent, boolean acceptsTransfer, boolean scheduled, IGui root) {
        Runnable r = () -> {
            // isCallingFromMinecraftThread
            if (Minecraft.getMinecraft().func_152345_ab())
                displayGuiUnsafe(updateParent, acceptsTransfer, root);
        };
        if (scheduled) {
            JecaGui.scheduled = r;
            JecaGui.timeout = 2;
        } else
            r.run();
    }

    public static Optional<JecaGui> currentJecaGui() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof JecaGui) {
            return Optional.of((JecaGui) gui);
        } else {
            return Optional.empty();
        }
    }

    /**
     * @return The currently displayed {@link JecaGui}
     * Make sure the method is called when a {@link JecaGui} is displayed!
     * Otherwise it will throw a {@link NullPointerException}
     */
    public static JecaGui getCurrent() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        JecaGui ret = gui instanceof JecaGui ? (JecaGui) gui : null;
        return Objects.requireNonNull(ret);
    }

    private static void displayGuiUnsafe(boolean updateParent, boolean acceptsTransfer, IGui root) {
        Minecraft mc = Minecraft.getMinecraft();
        JecaGui parent;
        if (mc.currentScreen == null)
            parent = null;
        else if (!(mc.currentScreen instanceof JecaGui))
            parent = last;
        else if (updateParent)
            parent = (JecaGui) mc.currentScreen;
        else
            parent = ((JecaGui) mc.currentScreen).parent;
        JecaGui toShow = new JecaGui(parent, acceptsTransfer, root);
        root.onVisible(toShow);
        last = toShow;
        mc.displayGuiScreen(toShow);
    }

    public static void displayParent() {
        // isCallingFromMinecraftThread
        if (Minecraft.getMinecraft().func_152345_ab()) {
            JecaGui gui = getCurrent().parent;
            gui.root.onVisible(gui);
            last = gui;
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }
    }

    @Nullable
    public ILabel getLabelUnderMouse() {
        int xMouse = Mouse.getEventX() * width / mc.displayWidth - guiLeft;
        int yMouse = height - Mouse.getEventY() * height / mc.displayHeight - 1 - guiTop;
        return root.getLabelUnderMouse(xMouse, yMouse);
    }

    /**
     * called by {@link me.towdium.jecalculation.event.handlers.FMLBusEventHandler}
     */
    public static void onKey() {
        if (ClientHandler.keyOpenGuiCraft.isPressed())
            Controller.openGuiCraft();
        if (ClientHandler.keyOpenGuiMath.isPressed())
            Controller.openGuiMath();
    }

    public static void onGameTick() {
        if (scheduled != null) {
            if (timeout <= 0) {
                Runnable r = scheduled;
                scheduled = null;
                r.run();
            } else
                timeout--;
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

    @Override
    // modified from vanilla
    public void drawHoveringText(List lines, int x, int y, FontRenderer font) {
        //noinspection unchecked
        List<String> textLines = (List<String>) lines;
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;
            for (String s : textLines) {
                int j = this.fontRendererObj.getStringWidth(s);
                if (j > i)
                    i = j;
            }
            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8 + (textLines.size() - 1) * 10;
            if (l1 + i > this.width)
                l1 -= 28 + i;
            if (i2 + k + 6 > this.height)
                i2 = this.height - k - 6;
            zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
            for (String s1 : textLines) {
                fontRendererObj.drawStringWithShadow(s1, l1, i2, -1);
                i2 += 10;
            }
            zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public void drawResource(Resource r, int xPos, int yPos) {
        drawResource(r, xPos, yPos, 0xFFFFFF);
    }

    public void drawResource(Resource r, int xPos, int yPos, int color) {
        setColor(color);
        mc.getTextureManager().bindTexture(r.getResourceLocation());
        drawTexturedModalRect(xPos, yPos, r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize());
    }

    public void drawResourceContinuous(Resource r, int xPos, int yPos, int xSize, int ySize, int border) {
        drawResourceContinuous(r, xPos, yPos, xSize, ySize, border, border, border, border);
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
        GuiUtils.drawContinuousTexturedBox(r.getResourceLocation(), xPos, yPos, r.getXPos(), r.getYPos(), xSize, ySize,
                                           r.getXSize(), r.getYSize(), borderTop, borderBottom, borderLeft, borderRight,
                                           zLevel);
    }

    private void setColor(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (~(color >> 24) & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
    }

    public void drawFluid(Fluid f, int xPos, int yPos, int xSize, int ySize) {
        IIcon fluidIcon = f.getFlowingIcon();
        if (fluidIcon == null)
            fluidIcon = f.getStillIcon();
        mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        setColor(f.getColor() & 0x00FFFFFF);
        if (fluidIcon != null)
            drawTexturedModelRectFromIcon(xPos, yPos, fluidIcon, xSize, ySize);
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
        tessellator.addVertex(xPos, bottom, 0.0D);
        tessellator.addVertex(right, bottom, 0.0D);
        tessellator.addVertex(right, yPos, 0.0D);
        tessellator.addVertex(xPos, yPos, 0.0D);
        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public int getStringWidth(String s) {
        return fontRendererObj.getStringWidth(s);
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
        if (f.raw)
            fontRendererObj.setUnicodeFlag(false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0);
        if (f.half)
            GlStateManager.scale(0.5, 0.5, 1);
        r.run();
        GlStateManager.popMatrix();
        fontRendererObj.setUnicodeFlag(unicode);
    }

    public void drawItemStack(int xPos, int yPos, ItemStack is, boolean centred) {
        if (ItemStackHelper.isEmpty(is)) {
            return;
        }
        if (centred) {
            xPos -= 8;
            yPos -= 8;
        }
        float zLevel = itemRender.zLevel += 100F;
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(fontRendererObj, renderEngine, is, xPos, yPos);
        itemRender.renderItemOverlayIntoGUI(fontRendererObj, renderEngine, is, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        itemRender.zLevel = zLevel - 100F;
    }

    //    @Override
    //    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    //        JustEnoughCalculation.logger.info("mouse clicked");
    //        super.mouseClicked(mouseX, mouseY, mouseButton);
    //        boolean inGui = root.onClicked(this, mouseX - guiLeft, mouseY - guiTop, mouseButton);
    //        if (!inGui) {
    //            this.handleMouseEvent();
    //        }
    //    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && hand != ILabel.EMPTY)
            hand = ILabel.EMPTY;
        else if (!root.onKey(this, typedChar, keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE && parent != null)
                displayParent();
            else
                super.keyTyped(typedChar, keyCode);
        }

    }

    public static class Font {
        public static final Font SHADOW = new Font(JecaGui.COLOR_TEXT_WHITE, true, false, false);
        public static final Font PLAIN = new Font(JecaGui.COLOR_TEXT_GREY, false, false, false);
        public static final Font RAW = new Font(JecaGui.COLOR_TEXT_GREY, false, false, true);
        public static final Font HALF = new Font(JecaGui.COLOR_TEXT_WHITE, true, true, true);

        public int color;
        public boolean shadow, half, raw;

        public Font(int color, boolean shadow, boolean half, boolean raw) {
            this.color = color;
            this.shadow = shadow;
            this.half = half;
            this.raw = raw;
        }

        public int getTextWidth(String s) {
            FontRenderer fr = getCurrent().fontRendererObj;
            boolean flag = fr.getUnicodeFlag();
            if (raw)
                fr.setUnicodeFlag(false);
            int ret = (int) Math.ceil(fr.getStringWidth(s) * (half ? 0.5f : 1));
            fr.setUnicodeFlag(flag);
            return ret;
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

    public void drawHoveringText(List<String> textLines, int x, int y) {
        super.drawHoveringText(textLines, x, y, fontRendererObj);
    }

}
