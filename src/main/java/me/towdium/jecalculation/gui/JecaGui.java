package me.towdium.jecalculation.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseScrollEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber
public class JecaGui extends Screen {
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
        super(new StringTextComponent(""));
        this.parent = parent;
        this.root = root;
        //if (inventorySlots instanceof JecContainer) ((JecContainer) inventorySlots).setGui(this);
    }

    public static int getMouseX() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        return (int) mc.mouseHelper.getMouseX() * mc.mainWindow.getScaledWidth() / mc.mainWindow.getWidth();
    }

    public static int getMouseY() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        return (int) mc.mouseHelper.getMouseY() * mc.mainWindow.getScaledHeight() / mc.mainWindow.getHeight();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouse(GuiScreenEvent.MouseInputEvent event) {
        if (!(event.getGui() instanceof JecaGui)) return;
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();

        if (event instanceof MouseScrollEvent) {
            double diff = ((MouseScrollEvent) event).getScrollDelta() / 120;
            if (diff != 0) gui.root.onScroll(gui, xMouse, yMouse, (int) diff);
        } else if (event instanceof MouseClickedEvent) {
            int button = ((MouseClickedEvent) event).getButton();
            if (gui.root.onClicked(gui, xMouse, yMouse, button)) event.setCanceled(true);
            else if (gui.hand != ILabel.EMPTY) {
                gui.hand = ILabel.EMPTY;
                event.setCanceled(true);
            }
//            else {
//                ILabel e = JecaPlugin.getLabelUnderMouse();
//                if (e != ILabel.EMPTY) {
//                    gui.hand = e;
//                    event.setCanceled(true);
//                }
//            }
        }
    }

//    @Nullable
//    @Override
//    public Slot getSlotUnderMouse() {
//        IInventory i = new InventoryBasic("", false, 1);
//        Slot s = new Slot(i, 0, 0, 0);
//        ILabel l = getLabelUnderMouse();
//        if (l instanceof LItemStack) s.putStack(((LItemStack) l).getRep());
//        return s;
//    }

//    @Override
//    public void init() {
//        this.guiLeft = (this.width - this.xSize) / 2;
//        this.guiTop = (this.height - this.ySize) / 2;
//    }

    public static boolean mouseIn(int xPos, int yPos, int xSize, int ySize, int xMouse, int yMouse) {
        return xMouse > xPos && yMouse > yPos && xMouse <= xPos + xSize && yMouse <= yPos + ySize;
    }

    public static void displayGui(IGui root) {
        displayGui(true, false, root);
    }

    public static void displayGui(boolean updateParent, boolean acceptsTransfer, IGui root) {
        displayGui(updateParent, acceptsTransfer, false, root);
    }

    // TODO thread check
    public static void displayGui(boolean updateParent, boolean acceptsTransfer, boolean scheduled, IGui root) {
        Runnable r = () -> {
            //if (Minecraft.getMinecraft().isCallingFromMinecraftThread())
            displayGuiUnsafe(updateParent, acceptsTransfer, root);
        };
        if (scheduled) {
            JecaGui.scheduled = r;
            JecaGui.timeout = 2;
        } else r.run();
    }

    /**
     * @return The currently displayed {@link JecaGui}
     * Make sure the method is called when a {@link JecaGui} is displayed!
     * Otherwise it will throw a {@link NullPointerException}
     */
    public static JecaGui getCurrent() {
        Screen gui = Minecraft.getInstance().currentScreen;
        JecaGui ret = gui instanceof JecaGui ? (JecaGui) gui : null;
        Objects.requireNonNull(ret);
        return ret;
    }

    private static void displayGuiUnsafe(boolean updateParent, boolean acceptsTransfer, IGui root) {
        Minecraft mc = Minecraft.getInstance();
        JecaGui parent;
        if (mc.currentScreen == null) parent = null;
        else if (!(mc.currentScreen instanceof JecaGui)) parent = last;
        else if (updateParent) parent = (JecaGui) mc.currentScreen;
        else parent = ((JecaGui) mc.currentScreen).parent;
        JecaGui toShow = new JecaGui(parent, acceptsTransfer, root);
        root.onVisible(toShow);
        last = toShow;
        mc.displayGuiScreen(toShow);
    }

    public static void displayParent() {
        JecaGui gui = getCurrent().parent;
        gui.root.onVisible(gui);
        last = gui;
        Minecraft.getInstance().displayGuiScreen(gui);
    }

    @Nullable
    public ILabel getLabelUnderMouse() {
        return root.getLabelUnderMouse(getMouseX(), getMouseY());
    }

    @SubscribeEvent
    public static void onKey(GuiScreenEvent.KeyboardKeyPressedEvent event) {
//        if (JustEnoughCalculation.keyOpenGuiCraft.isPressed()) Controller.openGuiCraft();
//        if (JustEnoughCalculation.keyOpenGuiMath.isPressed()) Controller.openGuiMath();
    }

    @SubscribeEvent
    public static void onTooltip(RenderTooltipEvent.Pre event) {
        if (Minecraft.getInstance().currentScreen instanceof JecaGui) {
            JecaGui gui = getCurrent();
            if (gui.root.onTooltip(gui, event.getX(), event.getY(), new ArrayList<>())
                    && !event.getStack().isEmpty()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGameTike(TickEvent.PlayerTickEvent e) {
        if (e.player.world.isRemote && scheduled != null) {
            if (timeout <= 0) {
                Runnable r = scheduled;
                scheduled = null;
                r.run();
            } else timeout--;
        }
    }

    public static boolean isShiftDown() {
        return Minecraft.getInstance().player.isSneaking();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        drawDefaultBackground();
//    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        //GlStateManager.(guiLeft, guiTop, 0);
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        root.onDraw(this, mouseX, mouseY);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0, 0, 80);
        hand.drawLabel(this, mouseX, mouseY, true);
        GlStateManager.popMatrix();
        List<String> tooltip = new ArrayList<>();
        root.onTooltip(this, mouseX, mouseY, tooltip);
        renderTooltip(tooltip, mouseX, mouseY, font);
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
    }

    // modified from vanilla
//    public void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
//        if (!textLines.isEmpty()) {
//            GlStateManager.disableRescaleNormal();
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.disableLighting();
//            GlStateManager.disableDepthTest();
//            int i = 0;
//            for (String s : textLines) {
//                int j = this.font.getStringWidth(s);
//                if (j > i) i = j;
//            }
//            int l1 = x + 12;
//            int i2 = y - 12;
//            int k = 8 + (textLines.size() - 1) * 10;
//            if (l1 + i > this.width) l1 -= 28 + i;
//            if (i2 + k + 6 > this.height) i2 = this.height - k - 6;
//            //zLevel = 300.0F;
//            itemRenderer.zLevel = 300.0F;
//            drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
//            drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
//            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
//            drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
//            drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
//            drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
//            drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
//            drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
//            drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
//            for (String s1 : textLines) {
//                font.drawStringWithShadow(s1, (float) l1, (float) i2, -1);
//                i2 += 10;
//            }
//            //zLevel = 0.0F;
//            itemRenderer.zLevel = 0.0F;
//            GlStateManager.enableLighting();
//            GlStateManager.enableDepth();
//            RenderHelper.enableStandardItemLighting();
//            GlStateManager.enableRescaleNormal();
//        }
//    }

    public void drawResource(Resource r, int xPos, int yPos) {
        drawResource(r, xPos, yPos, 0xFFFFFF);
    }

    public void drawResource(Resource r, int xPos, int yPos, int color) {
        setColor(color);
        Objects.requireNonNull(minecraft).getTextureManager().bindTexture(r.getResourceLocation());
        blit(xPos, yPos, r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize());
    }

    public void drawResourceContinuous(Resource r, int xPos, int yPos, int xSize, int ySize, int border) {
        drawResourceContinuous(r, xPos, yPos, xSize, ySize, border, border, border, border);
    }

    public void drawResourceContinuous(
            Resource r, int xPos, int yPos, int xSize, int ySize,
            int borderTop, int borderBottom, int borderLeft, int borderRight) {
        GuiUtils.drawContinuousTexturedBox(r.getResourceLocation(), xPos, yPos, r.getXPos(), r.getYPos(), xSize, ySize,
                r.getXSize(), r.getYSize(), borderTop, borderBottom, borderLeft, borderRight, 0);
    }

    private void setColor(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (~(color >> 24) & 0xFF) / 255.0F;
        GlStateManager.color4f(red, green, blue, alpha);
    }

    public void drawFluid(Fluid f, int xPos, int yPos, int xSize, int ySize) {  // getStill
        TextureAtlasSprite fluidTexture = Objects.requireNonNull(minecraft).getTextureMap().getAtlasSprite(f.getDefaultState().toString());
        minecraft.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        blit(xPos, yPos, 0, xSize, ySize, fluidTexture);
    }

    public void drawRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
//        float f3 = (float) (color >> 24 & 255) / 255.0F;
////        float f = (float) (color >> 16 & 255) / 255.0F;
////        float f1 = (float) (color >> 8 & 255) / 255.0F;
////        float f2 = (float) (color & 255) / 255.0F;
////        int right = xPos + xSize;
////        int bottom = yPos + ySize;
////        Tessellator tessellator = Tessellator.getInstance();
////        BufferBuilder bufferbuilder = tessellator.getBuffer();
////        GlStateManager.disableTexture2D();
////        GlStateManager.color(f, f1, f2, f3);
////        GlStateManager.disableAlpha();
////        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
////        bufferbuilder.pos((double) xPos, (double) bottom, 0.0D).endVertex();
////        bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
////        bufferbuilder.pos((double) right, (double) yPos, 0.0D).endVertex();
////        bufferbuilder.pos((double) xPos, (double) yPos, 0.0D).endVertex();
////        tessellator.draw();
////        GlStateManager.enableAlpha();
////        GlStateManager.enableTexture2D();
        fill(xPos, yPos, xSize, ySize, color);
    }

    public int getStringWidth(String s) {
        return font.getStringWidth(s);
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
                if (f.shadow) font.drawStringWithShadow(i, 0, y, f.color);
                else font.drawString(i, 0, y, f.color);
                y += font.FONT_HEIGHT + 1;
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
            if (f.shadow) font.drawStringWithShadow(str, 0, 0, f.color);
            else font.drawString(str, 0, 0, f.color);
        });
    }

    private void drawText(float xPos, float yPos, Font f, Runnable r) {
        boolean unicode = font.getBidiFlag();
        if (f.raw) font.setBidiFlag(false);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(xPos, yPos, 0);
        if (f.half) GlStateManager.scalef(0.5f, 0.5f, 1);
        r.run();
        GlStateManager.popMatrix();
        font.setBidiFlag(unicode);
    }

    public void drawItemStack(int xPos, int yPos, ItemStack is, boolean centred) {
        if (centred) {
            xPos -= 8;
            yPos -= 8;
        }
        GlStateManager.enableDepthTest();
        RenderHelper.enableGUIStandardItemLighting();
        FontRenderer font = is.getItem().getFontRenderer(is);
        if (font == null) font = this.font;
        itemRenderer.renderItemAndEffectIntoGUI(is, xPos, yPos);
        itemRenderer.renderItemOverlayIntoGUI(font, is, xPos, yPos, null);
        itemRenderer.renderItemIntoGUI(is, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepthTest();
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        return false; // TODO
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifier) {
        if (key == GLFW.GLFW_KEY_ESCAPE && hand != ILabel.EMPTY) hand = ILabel.EMPTY;
        else if (!root.onKey(this, key, modifier)) {
            if (key == GLFW.GLFW_KEY_ESCAPE && parent != null) displayParent();
            else return super.keyPressed(key, scan, modifier);
        }
        return true;
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
            FontRenderer fr = getCurrent().font;
            boolean flag = fr.getBidiFlag();
            if (raw) fr.setBidiFlag(false);
            int ret = (int) Math.ceil(fr.getStringWidth(s) * (half ? 0.5f : 1));
            fr.setBidiFlag(flag);
            return ret;
        }

        public int getTextHeight() {
            return (int) Math.ceil(getCurrent().font.FONT_HEIGHT * (half ? 0.5f : 1));
        }

        public String trimToWidth(String s, int i) {
            return getCurrent().font.trimStringToWidth(s, i * (half ? 2 : 1));
        }
    }

//    public static class JecContainer extends Container {
//        JecaGui gui;
//
//        protected JecContainer() {
//            super(ContainerType.STONECUTTER, id);
//        }
//
//        public JecaGui getGui() {
//            return gui;
//        }
//
//        public void setGui(JecaGui gui) {
//            this.gui = gui;
//        }
//
//        @Override
//        public boolean canInteractWith(PlayerEntity playerIn) {
//            return true;
//        }
//    }
//
//    public static class ContainerTransfer extends JecContainer {
//    }
//
//    public static class ContainerNonTransfer extends JecContainer {
//    }
}
