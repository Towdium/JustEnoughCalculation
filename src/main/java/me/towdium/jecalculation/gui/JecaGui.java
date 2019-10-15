package me.towdium.jecalculation.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.jei.JecaPlugin;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseDragEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseScrollEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraftforge.fml.LogicalSidedProvider.WORKQUEUE;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber
public class JecaGui extends ContainerScreen<JecaGui.JecaContainer> {
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
        super(acceptsTransfer ? new JecaGui.ContainerTransfer() : new JecaGui.ContainerNonTransfer(),
                Minecraft.getInstance().player.inventory, new StringTextComponent(""));
        this.parent = parent;
        this.root = root;
        if (container != null) container.setGui(this);
    }

    public static int getMouseX() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        return (int) mc.mouseHelper.getMouseX() * mc.mainWindow.getScaledWidth() / mc.mainWindow.getWidth() - gui.guiLeft;
    }

    public static int getMouseY() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        return (int) mc.mouseHelper.getMouseY() * mc.mainWindow.getScaledHeight() / mc.mainWindow.getHeight() - gui.guiTop;
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
            } else {
                ILabel e = JecaPlugin.getLabelUnderMouse();
                if (e != ILabel.EMPTY) {
                    gui.hand = e;
                    event.setCanceled(true);
                }
            }
        } else if (event instanceof MouseDragEvent) {
            MouseDragEvent mde = (MouseDragEvent) event;
            gui.root.onDragged(gui, xMouse, yMouse, (int) mde.getDragX(), (int) mde.getDragY());
        }
    }

    @Nullable
    @Override
    public Slot getSlotUnderMouse() {
        IInventory i = new Inventory(ItemStack.EMPTY);
        Slot s = new Slot(i, 0, 0, 0);
        ILabel l = getLabelUnderMouse();
        Object rep = l == null ? null : l.getRepresentation();
        if (rep instanceof ItemStack) s.putStack((ItemStack) rep);
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
        Runnable r = () -> displayGuiUnsafe(updateParent, acceptsTransfer, root);
        if (scheduled) {
            JecaGui.scheduled = r;
            JecaGui.timeout = 2;
        } else {
            ThreadTaskExecutor<?> executor = WORKQUEUE.get(LogicalSide.CLIENT);
            executor.deferTask(r);
        }
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

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        renderBackground();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        mouseX -= guiLeft;
        mouseY -= guiTop;
        GlStateManager.pushMatrix();
        GlStateManager.translatef(guiLeft, guiTop, 0);
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        root.onDraw(this, mouseX, mouseY);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0, 0, 80);
        hand.drawLabel(this, mouseX + guiLeft, mouseY + guiTop, true);
        GlStateManager.popMatrix();
        List<String> tooltip = new ArrayList<>();
        root.onTooltip(this, mouseX, mouseY, tooltip);
        renderTooltip(tooltip, mouseX + guiLeft, mouseY + guiTop, font);
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
        TextureAtlasSprite fluidTexture = Objects.requireNonNull(minecraft).getTextureMap()
                .getSprite(f.getFluid().getAttributes().getStillTexture());
        minecraft.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        setColor(f.getAttributes().getColor() & 0x00FFFFFF);
        blit(xPos, yPos, 0, xSize, ySize, fluidTexture);
    }

    public void drawRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        fill(xPos, yPos, xPos + xSize, yPos + ySize, color);
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
    protected void init() {
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
    }

    @Override
    public boolean charTyped(char ch, int modifier) {
        return root.onChar(this, ch, modifier) || super.charTyped(ch, modifier);
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifier) {
        if (key == GLFW.GLFW_KEY_ESCAPE && hand != ILabel.EMPTY) hand = ILabel.EMPTY;
        else if (!root.onPressed(this, key, modifier)) {
            if (key == GLFW.GLFW_KEY_ESCAPE && parent != null) displayParent();
            else return super.keyPressed(key, scan, modifier);
        }
        return true;

    }

    @Override
    public boolean keyReleased(int key, int scan, int modifier) {
        return root.onReleased(this, key, modifier)
                || super.keyReleased(key, scan, modifier);
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

    @Mod.EventBusSubscriber(bus = MOD)
    public static class JecaContainer extends Container {
        JecaGui gui;
        public static ContainerType<JecaContainer> GENERIC;

        protected JecaContainer() {
            super(GENERIC, 0);
        }


        @SubscribeEvent
        public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
            GENERIC = new ContainerType<>((i, j) -> new JecaContainer());
            GENERIC.setRegistryName("generic");
            event.getRegistry().register(GENERIC);
        }

        public JecaGui getGui() {
            return gui;
        }

        public void setGui(JecaGui gui) {
            this.gui = gui;
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return true;
        }
    }

    public static class ContainerTransfer extends JecaContainer {
    }

    public static class ContainerNonTransfer extends JecaContainer {
    }
}
