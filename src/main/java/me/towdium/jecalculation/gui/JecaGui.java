package me.towdium.jecalculation.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.GuiCraft;
import me.towdium.jecalculation.gui.guis.GuiMath;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.jei.JecaPlugin;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseClickedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseDragEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseReleasedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.towdium.jecalculation.utils.Utilities.getPlayer;
import static net.minecraftforge.fml.LogicalSidedProvider.WORKQUEUE;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mod.EventBusSubscriber(Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class JecaGui extends ContainerScreen<JecaGui.JecaContainer> {
    public static final KeyBinding keyOpenGuiCraft = new KeyBinding(
            "jecalculation.key.gui_craft", GLFW.GLFW_KEY_UNKNOWN, "jecalculation.key.category");
    public static final KeyBinding keyOpenGuiMath = new KeyBinding(
            "jecalculation.key.gui_math", GLFW.GLFW_KEY_UNKNOWN, "jecalculation.key.category");
    public static final int COLOR_GUI_GREY = 0xFFA1A1A1;
    public static final int COLOR_TEXT_RED = 0xFF0000;
    public static final int COLOR_TEXT_GREY = 0x404040;
    public static final int COLOR_TEXT_WHITE = 0xFFFFFF;
    public static final boolean ALWAYS_TOOLTIP = false;
    @SuppressWarnings("StringOperationCanBeSimplified")
    public static final String SEPARATOR = new String();
    public ILabel hand = ILabel.EMPTY;
    protected static JecaGui last;
    protected static JecaGui override;
    protected JecaGui parent;
    protected MatrixStack matrix;
    public IGui root;

    public JecaGui(@Nullable JecaGui parent, IGui root) {
        this(parent, false, root);
    }

    public JecaGui(@Nullable JecaGui parent, boolean acceptsTransfer, IGui root) {
        super(acceptsTransfer ? new JecaGui.ContainerTransfer() : new JecaGui.ContainerNonTransfer(),
                getPlayer().inventory, new StringTextComponent(""));
        this.parent = parent;
        this.root = root;
        if (container != null) container.setGui(this);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        minecraft.keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public void onClose() {
        super.onClose();
        Objects.requireNonNull(this.minecraft);
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    public static int getMouseX() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        return (int) mc.mouseHelper.getMouseX() * mc.getMainWindow().getScaledWidth() / mc.getMainWindow().getWidth() - gui.guiLeft;
    }

    public static int getMouseY() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        return (int) mc.mouseHelper.getMouseY() * mc.getMainWindow().getScaledHeight() / mc.getMainWindow().getHeight() - gui.guiTop;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onFocus(MouseClickedEvent.Pre event) {
        if (!(event.getGui() instanceof JecaGui)) return;
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        int button = event.getButton();
        gui.root.onMouseFocused(gui, xMouse, yMouse, button);
    }

    // TODO No need to keep events merged
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onMouse(GuiScreenEvent.MouseInputEvent event) {
        if (!(event.getGui() instanceof JecaGui)) return;
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();

        if (event instanceof MouseScrollEvent.Pre) {
            double diff = ((MouseScrollEvent) event).getScrollDelta();
            if (diff != 0) gui.root.onMouseScroll(gui, xMouse, yMouse, (int) diff);
        } else if (event instanceof MouseClickedEvent.Pre) {
            int button = ((MouseClickedEvent) event).getButton();
            if (gui.root.onMouseClicked(gui, xMouse, yMouse, button)) event.setCanceled(true);
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
        } else if (event instanceof MouseDragEvent.Pre) {
            MouseDragEvent mde = (MouseDragEvent) event;
            gui.root.onMouseDragged(gui, xMouse, yMouse, (int) mde.getDragX(), (int) mde.getDragY());
        } else if (event instanceof MouseReleasedEvent.Pre) {
            int button = ((MouseReleasedEvent) event).getButton();
            gui.root.onMouseReleased(gui, xMouse, yMouse, button);
        }
    }

    @Override
    public void tick() {
        super.tick();
        root.onTick(this);
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
        Screen s = Minecraft.getInstance().currentScreen;
        if (s instanceof JecaGui) {
            displayGui(root, true);
        } else {
            displayGui(root, null);
        }
    }

    public static void displayGui(IGui root, @Nullable JecaGui parent) {
        Minecraft mc = Minecraft.getInstance();
        Screen s = mc.currentScreen;
        JecaGui gui = new JecaGui(parent, root.acceptsTransfer(), root);
        if (s instanceof IRecipesGui || s instanceof ChatScreen) {
            JecaGui.override = gui;
        } else  {
            ThreadTaskExecutor<?> executor = WORKQUEUE.get(LogicalSide.CLIENT);
            executor.deferTask(() -> {
                root.onVisible(gui);
                last = gui;
                mc.displayGuiScreen(gui);
            });
        }
    }

    public static void displayGui(IGui root, boolean updateParent) {
        JecaGui current = JecaGui.getCurrent();
        JecaGui parent = updateParent ? current : current.parent;
        JecaGui.displayGui(root, parent);
    }

    public MatrixStack getMatrix() {
        return matrix;
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

    public static JecaGui getLast() {
        return last;
    }

    public static void displayParent() {
        JecaGui gui = getCurrent().parent;
        gui.root.onVisible(gui);
        last = gui;
        Minecraft.getInstance().displayGuiScreen(gui);
    }

    @Nullable
    public ILabel getLabelUnderMouse() {
        Wrapper<ILabel> l = new Wrapper<>(null);
        root.getLabelUnderMouse(getMouseX(), getMouseY(), l);
        return l.value;
    }

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        if (keyOpenGuiCraft.isKeyDown()) JecaGui.openGuiCraft(null);
        if (keyOpenGuiMath.isKeyDown()) JecaGui.openGuiMath(null);
    }

    @SubscribeEvent
    public static void onTooltip(RenderTooltipEvent.Pre event) {
        if (Minecraft.getInstance().currentScreen instanceof JecaGui) {
            JecaGui gui = getCurrent();
            boolean overlap = gui.root.onTooltip(gui, event.getX() - gui.guiLeft,
                    event.getY() - gui.guiTop, new ArrayList<>());
            if (overlap && !event.getStack().isEmpty()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent e) {
        if (override != null) {
            override.root.onVisible(override);
            last = override;
            e.setGui(override);
            override = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static int openGuiMath(@Nullable ItemStack is) {
        boolean ret = is == null && Controller.isServerActive();
        String s = "jecalculation.chat.server_mode";
        if (ret) getPlayer().sendStatusMessage(new TranslationTextComponent(s), false);
        else JecaGui.displayGui(new GuiMath(is));
        return ret ? 1 : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static int openGuiCraft(@Nullable ItemStack is) {
        boolean ret = is == null && Controller.isServerActive();
        String s = "jecalculation.chat.server_mode";
        if (ret) getPlayer().sendStatusMessage(new TranslationTextComponent(s), false);
        else JecaGui.displayGui(new GuiCraft(is));
        return ret ? 1 : 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        renderBackground(matrixStack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrix = matrixStack;
        mouseX -= guiLeft;
        mouseY -= guiTop;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(guiLeft, guiTop, 0);
        root.onDraw(this, mouseX, mouseY);
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0, 0, 80);
        hand.drawLabel(this, mouseX + guiLeft, mouseY + guiTop, true);
        RenderSystem.popMatrix();
        List<String> tooltip = new ArrayList<>();
        root.onTooltip(this, mouseX, mouseY, tooltip);
        drawHoveringText(matrixStack, tooltip, mouseX + guiLeft, mouseY + guiTop, font);
    }

    // modified from vanilla
    @SuppressWarnings("deprecation")
    public void drawHoveringText(MatrixStack matrixStack, List<String> textLines, int x, int y, FontRenderer font) {
        if (!textLines.isEmpty()) {
            RenderSystem.disableRescaleNormal();
            RenderSystem.disableDepthTest();
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, 0, 400);
            int i = 0;
            int separators = 0;
            for (String s : textLines) {
                int j = this.font.getStringWidth(s);
                if (j > i) i = j;
                //noinspection StringEquality
                if (s == JecaGui.SEPARATOR) separators++;
            }
            //noinspection StringEquality
            if (textLines.get(textLines.size() - 1) == SEPARATOR) separators--;
            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8 + (textLines.size() - separators - 1) * 10 + 2 * separators;
            if (l1 + i > this.width) l1 -= 28 + i;
            if (i2 + k + 6 > this.height) i2 = this.height - k - 6;
            fillGradient(matrixStack,l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            fillGradient(matrixStack,l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            fillGradient(matrixStack,l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            fillGradient(matrixStack,l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            fillGradient(matrixStack,l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            fillGradient(matrixStack,l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            fillGradient(matrixStack,l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            fillGradient(matrixStack,l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            fillGradient(matrixStack,l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
            for (String s1 : textLines) {
                //noinspection StringEquality
                if (s1 == SEPARATOR) i2 += 2;
                else {
                    font.drawStringWithShadow(matrixStack, s1, (float) l1, (float) i2, -1);
                    i2 += 10;
                }
            }
            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.enableRescaleNormal();
        }
    }

    public void drawResource(Resource r, int xPos, int yPos) {
        drawResource(r, xPos, yPos, 0xFFFFFF);
    }

    public void drawResource(Resource r, int xPos, int yPos, int color) {
        setColor(color);
        Objects.requireNonNull(minecraft).getTextureManager().bindTexture(r.getResourceLocation());
        blit(matrix, xPos, yPos, r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize());
    }

    public void drawResourceContinuous(Resource r, int xPos, int yPos, int xSize, int ySize, int border) {
        drawResourceContinuous(r, xPos, yPos, xSize, ySize, border, border, border, border);
    }

    public void drawResourceContinuous(
            Resource r, int xPos, int yPos, int xSize, int ySize,
            int borderTop, int borderBottom, int borderLeft, int borderRight) {
        GuiUtils.drawContinuousTexturedBox(matrix, r.getResourceLocation(), xPos, yPos, r.getXPos(), r.getYPos(),
                xSize, ySize, r.getXSize(), r.getYSize(), borderTop, borderBottom, borderLeft, borderRight, 0);
    }

    @SuppressWarnings("deprecation")
    private void setColor(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (~(color >> 24) & 0xFF) / 255.0F;
        RenderSystem.color4f(red, green, blue, alpha);
    }

    public void drawFluid(Fluid f, int xPos, int yPos, int xSize, int ySize) {
        TextureAtlasSprite fluidTexture = Objects.requireNonNull(minecraft)
                .getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                .getSprite(f.getFluid().getAttributes().getStillTexture());
        minecraft.textureManager.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        setColor(f.getAttributes().getColor() & 0x00FFFFFF);
        blit(matrix, xPos, yPos, 0, xSize, ySize, fluidTexture);
    }

    public void drawRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        fill(matrix, xPos, yPos, xPos + xSize, yPos + ySize, color);
    }

    public int getStringWidth(String s) {
        return font.getStringWidth(s);
    }

    public void drawSplitText(float xPos, float yPos, int width, Font f, String s) {
        drawSplitText(xPos, yPos, f, Utilities.I18n.wrap(s, width));
    }

    public void drawSplitText(float xPos, float yPos, Font f, List<String> ss) {
        drawText(xPos, yPos, f, () -> {
            int y = 0;
            for (String i : ss) {
                if (f.shadow) font.drawStringWithShadow(matrix, i, 0, y, f.color);
                else font.drawString(matrix, i, 0, y, f.color);
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
            if (f.shadow) font.drawStringWithShadow(matrix, str, 0, 0, f.color);
            else font.drawString(matrix, str, 0, 0, f.color);
        });
    }

    @SuppressWarnings("deprecation")
    private void drawText(float xPos, float yPos, Font f, Runnable r) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(xPos, yPos, 200);
        if (f.half) RenderSystem.scalef(0.5f, 0.5f, 1);
        r.run();
        RenderSystem.popMatrix();
    }

    public void drawItemStack(int xPos, int yPos, ItemStack is, boolean centred) {
        if (centred) {
            xPos -= 8;
            yPos -= 8;
        }
        RenderSystem.enableDepthTest();
        FontRenderer font = is.getItem().getFontRenderer(is);
        if (font == null) font = this.font;
        itemRenderer.renderItemAndEffectIntoGUI(is, xPos, yPos);
        itemRenderer.renderItemOverlayIntoGUI(font, is, xPos, yPos, null);
        itemRenderer.renderItemIntoGUI(is, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableDepthTest();
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
        else if (!root.onKeyPressed(this, key, modifier)) {
            if (key == GLFW.GLFW_KEY_ESCAPE && parent != null) displayParent();
            else return super.keyPressed(key, scan, modifier);
        }
        return true;
    }

    @Override
    public boolean keyReleased(int key, int scan, int modifier) {
        return root.onKeyReleased(this, key, modifier)
                || super.keyReleased(key, scan, modifier);
    }

    @OnlyIn(Dist.CLIENT)
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
            return (int) Math.ceil(getCurrent().font.getStringWidth(s) * (half ? 0.5f : 1));
        }

        public int getTextHeight() {
            return (int) Math.ceil(getCurrent().font.FONT_HEIGHT * (half ? 0.5f : 1));
        }

        public String trimToWidth(String s, int i) {
            return getCurrent().font.func_238412_a_(s, i * (half ? 2 : 1));
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = MOD)
    @OnlyIn(Dist.CLIENT)
    public static class JecaContainer extends Container {
        JecaGui gui;

        protected JecaContainer() {
            super(null, 0);
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

    @OnlyIn(Dist.CLIENT)
    public static class ContainerTransfer extends JecaContainer {
    }

    @OnlyIn(Dist.CLIENT)
    public static class ContainerNonTransfer extends JecaContainer {
    }
}
