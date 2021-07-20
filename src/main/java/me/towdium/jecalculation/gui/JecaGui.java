package me.towdium.jecalculation.gui;

import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.GuiCraft;
import me.towdium.jecalculation.gui.guis.GuiMath;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.WLabel;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
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

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class JecaGui extends GuiContainer {
    public static final KeyBinding keyOpenGuiCraft = new KeyBinding("jecalculation.key.gui_craft", KEY_NONE,
                                                                    "jecalculation.key.category");
    public static final KeyBinding keyOpenGuiMath = new KeyBinding("jecalculation.key.gui_math", KEY_NONE,
                                                                   "jecalculation.key.category");

    public static final int COLOR_GUI_GREY = 0xFFA1A1A1;
    public static final int COLOR_TEXT_RED = 0xFF0000;
    public static final int COLOR_TEXT_GREY = 0x404040;
    public static final int COLOR_TEXT_WHITE = 0xFFFFFF;
    public static final boolean ALWAYS_TOOLTIP = false;
    @SuppressWarnings("StringOperationCanBeSimplified")
    public static final String SEPARATOR = new String();
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
        super(acceptsTransfer ? new JecaGui.ContainerTransfer() : new JecaGui.ContainerNonTransfer());
        this.parent = parent;
        this.root = root;
        if (inventorySlots instanceof JecaContainer)
            ((JecaContainer) inventorySlots).setGui(this);
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
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        int button = Mouse.getEventButton();
        if (button == -1) {
            int diff = Mouse.getEventDWheel() / 120;
            if (diff != 0)
                gui.root.onMouseScroll(gui, xMouse, yMouse, diff);
        } else if (Mouse.getEventButtonState()) {
            gui.root.onMouseFocused(gui, xMouse, yMouse, button);
            if (gui.root.onMouseClicked(gui, xMouse, yMouse, button)) {
                return true;
            } else if (gui.hand != ILabel.EMPTY) {
                gui.hand = button == 0 ? NEIPlugin.getLabelUnderMouse() : ILabel.EMPTY;
                return true;
            } else if (button == 0 && gui.root.acceptsLabel()) {
                ILabel e = NEIPlugin.getLabelUnderMouse();
                if (e != ILabel.EMPTY) {
                    gui.hand = e;
                    return true;
                }
            }
        }
        return false;
    }

    public static void onMouseReleased() {
        Optional<JecaGui> optionalJecaGui = currentJecaGui();
        if (!optionalJecaGui.isPresent())
            return;
        JecaGui gui = optionalJecaGui.get();
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        int button = Mouse.getEventButton();
        gui.root.onMouseReleased(gui, xMouse, yMouse, button);
    }


    @Nullable
    public Slot getSlotUnderMouse() {
        IInventory i = new InventoryBasic("", false, 1);
        Slot s = new Slot(i, 0, 0, 0);
        ILabel l = getLabelUnderMouse();
        Object rep = l == null ? null : l.getRepresentation();
        if (rep instanceof ItemStack)
            s.putStack((ItemStack) rep);
        return s;
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
            JecaGui.timeout = 1;
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
        JecaGui gui = getCurrent().parent;
        gui.root.onVisible(gui);
        last = gui;
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Nullable
    public ILabel getLabelUnderMouse() {
        Wrapper<ILabel> l = new Wrapper<>(null);
        root.getLabelUnderMouse(getMouseX(), getMouseY(), l);
        return l.value;
    }

    /**
     * Called by {@link me.towdium.jecalculation.event.handlers.FMLBusEventHandler}.
     * This event happened only when not in gui.
     * For nei keybinding event, see {@link me.towdium.jecalculation.event.handlers.NEIEventHandler#lastKeyTyped(GuiContainer, char, int)}
     * I think they won't conflict
     */
    public static void onKey() {
        if (keyOpenGuiCraft.isPressed())
            JecaGui.openGuiCraft(false);
        if (keyOpenGuiMath.isPressed())
            JecaGui.openGuiMath(false);
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

    @SideOnly(Side.CLIENT)
    public static void openGuiCraft(boolean scheduled) {
        JecaGui.displayGui(true, true, scheduled, new GuiCraft());
    }

    @SideOnly(Side.CLIENT)
    public static void openGuiMath(boolean scheduled) {
        JecaGui.displayGui(true, true, scheduled, new GuiMath());
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
        mouseX -= guiLeft;
        mouseY -= guiTop;
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        root.onDraw(this, mouseX, mouseY);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 80);
        hand.drawLabel(this, mouseX + guiLeft, mouseY + guiTop, true);
        GlStateManager.popMatrix();
        List<String> tooltip = new ArrayList<>();
        root.onTooltip(this, mouseX, mouseY, tooltip);
        drawHoveringText(tooltip, mouseX + guiLeft, mouseY + guiTop);
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
            int separators = 0;
            for (String s : textLines) {
                int j = font.getStringWidth(s);
                if (j > i)
                    i = j;
                //noinspection StringEquality
                if (s == JecaGui.SEPARATOR)
                    separators++;
            }
            //noinspection StringEquality
            if (textLines.get(textLines.size() - 1) == SEPARATOR)
                separators--;
            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8 + (textLines.size() - separators - 1) * 10 + 2 * separators;
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
                //noinspection StringEquality
                if (s1 == SEPARATOR)
                    i2 += 2;
                else {
                    font.drawStringWithShadow(s1, l1, i2, -1);
                    i2 += 10;
                }
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
                                           0);
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

    public void drawSplitText(float xPos, float yPos, int width, Font f, String s) {
        drawSplitText(xPos, yPos, f, Utilities.I18n.wrap(s, width));
    }

    public void drawSplitText(float xPos, float yPos, Font f, List<String> ss) {
        drawText(xPos, yPos, f, () -> {
            int y = 0;
            for (String i : ss) {
                if (f.shadow)
                    fontRendererObj.drawStringWithShadow(i, 0, y, f.color);
                else
                    fontRendererObj.drawString(i, 0, y, f.color);
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
            if (f.shadow)
                fontRendererObj.drawStringWithShadow(str, 0, 0, f.color);
            fontRendererObj.drawString(str, 0, 0, f.color);
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
        FontRenderer font = is.getItem().getFontRenderer(is);
        if (font == null)
            font = this.fontRendererObj;
        itemRender.renderItemAndEffectIntoGUI(font, Minecraft.getMinecraft().renderEngine, is, xPos, yPos);
        itemRender.renderItemOverlayIntoGUI(font, Minecraft.getMinecraft().renderEngine, is, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        itemRender.zLevel = zLevel - 100F;
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE && hand != ILabel.EMPTY)
            hand = ILabel.EMPTY;
        else if (!root.onKeyPressed(this, typedChar, keyCode)) {
            if (keyCode == Keyboard.KEY_ESCAPE && parent != null)
                displayParent();
            else
                super.keyTyped(typedChar, keyCode);
        }
    }

    @SideOnly(Side.CLIENT)
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


    @SideOnly(Side.CLIENT)
    public static class JecaContainer extends Container {
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

    @SideOnly(Side.CLIENT)
    public static class ContainerTransfer extends JecaContainer {
    }

    @SideOnly(Side.CLIENT)
    public static class ContainerNonTransfer extends JecaContainer {
    }

    public void drawHoveringText(List<String> textLines, int x, int y) {
        this.drawHoveringText(textLines, x, y, fontRendererObj);
    }

}
