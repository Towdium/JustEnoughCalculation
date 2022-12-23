package me.towdium.jecalculation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.hooks.fluid.FluidStackHooks;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.GuiCraft;
import me.towdium.jecalculation.gui.guis.GuiMath;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.utils.GuiUtils;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.architectury.event.EventResult.interruptFalse;
import static dev.architectury.event.EventResult.pass;
import static me.towdium.jecalculation.utils.Utilities.getPlayer;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Environment(EnvType.CLIENT)
public class JecaGui extends AbstractContainerScreen<JecaGui.JecaContainer> {
    public static final KeyMapping keyOpenGuiCraft = new KeyMapping(
            "jecalculation.key.gui_craft", GLFW.GLFW_KEY_UNKNOWN, "jecalculation.key.category");
    public static final KeyMapping keyOpenGuiMath = new KeyMapping(
            "jecalculation.key.gui_math", GLFW.GLFW_KEY_UNKNOWN, "jecalculation.key.category");
    public static final int COLOR_GUI_GREY = 0xFFA1A1A1;
    public static final int COLOR_TEXT_RED = 0xFF0000;
    public static final int COLOR_TEXT_GREY = 0x404040;
    public static final int COLOR_TEXT_WHITE = 0xFFFFFF;
    public static final boolean ALWAYS_TOOLTIP = false;
    @SuppressWarnings("StringOperationCanBeSimplified")
    public static final String SEPARATOR = new String();
    public static final boolean IS_OSX = Util.OS.OSX.equals(Util.getPlatform());
    public ILabel hand = ILabel.EMPTY;
    protected static JecaGui last;
    public static JecaGui override;
    protected JecaGui parent;
    protected PoseStack matrix;
    protected final Utilities.OffsetStack itemOffset = new Utilities.OffsetStack();
    protected final boolean isWidget;
    protected boolean preventRecipeScreen = false;

    public IGui root;

    public JecaGui(@Nullable JecaGui parent, IGui root, boolean isWidget) {
        this(parent, false, root, isWidget);
    }

    public JecaGui(@Nullable JecaGui parent, boolean acceptsTransfer, IGui root, boolean isWidget) {
        super(acceptsTransfer ? new JecaGui.ContainerTransfer() : new JecaGui.ContainerNonTransfer(),
                getPlayer().getInventory(), Component.literal(""));
        this.parent = parent;
        this.root = root;
        this.isWidget = isWidget;
        if (menu != null) menu.setGui(this);
    }

    public static void registerEvents() {
        ClientGuiEvent.SET_SCREEN.register(JecaGui::onGuiOpen);
        ClientTooltipEvent.RENDER_PRE.register(JecaGui::onTooltip);
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(JecaGui::onFocused);
        ClientScreenInputEvent.MOUSE_SCROLLED_PRE.register(JecaGui::onMouseScroll);
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(JecaGui::onMouseClicked);
        ClientScreenInputEvent.MOUSE_DRAGGED_PRE.register(JecaGui::onMouseDragged);
        ClientScreenInputEvent.MOUSE_RELEASED_PRE.register(JecaGui::onMouseReleased);

        ClientRawInputEvent.KEY_PRESSED.register(JecaGui::onKeyPressed);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        if (!isWidget) {
            super.init(minecraft, width, height);
            minecraft.keyboardHandler.setSendRepeatsToGui(true);
            return;
        }
        this.minecraft = minecraft;
        this.itemRenderer = minecraft.getItemRenderer();
        this.font = minecraft.font;
        this.width = width;
        this.height = height;
        this.init();
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
    }

    @Override
    public void removed() {
        super.removed();
        Objects.requireNonNull(this.minecraft);
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    public static int getMouseX() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        int windowWidth = mc.getWindow().getScreenWidth();
        if (windowWidth == 0) return 0;
        return (int) mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / windowWidth - gui.leftPos;
    }

    public static int getMouseY() {
        JecaGui gui = getCurrent();
        Minecraft mc = Objects.requireNonNull(gui.minecraft, "Internal error");
        int windowHeight = mc.getWindow().getScreenHeight();
        if (windowHeight == 0) return 0;
        return (int) mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / windowHeight - gui.topPos;
    }

    public int getGlobalMouseX() {
        Minecraft mc = Objects.requireNonNull(Minecraft.getInstance(), "Internal error");
        int width = mc.getWindow().getWidth();
        if (width == 0) {
            return 0;
        }
        if(IS_OSX)
            width /= 2;
        return (int) mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / width - this.leftPos;
    }

    public int getGlobalMouseY() {
        Minecraft mc = Objects.requireNonNull(Minecraft.getInstance(), "Internal error");
        int height = mc.getWindow().getHeight();
        if (height == 0) {
            return 0;
        }
        if(IS_OSX)
            height /= 2;
        return (int) mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / height - this.topPos;
    }

    public int getGuiLeft() {
        return leftPos;
    }

    public int getGuiTop() {
        return topPos;
    }

    public int getYSize() {
        return imageHeight;
    }


    public static EventResult onFocused(Minecraft client, Screen screen, double mouseX, double mouseY, int button) {
        if (!(screen instanceof JecaGui)) return pass();
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        gui.root.onMouseFocused(gui, xMouse, yMouse, button);
        ILabel e = Utilities.getLabelUnderMouse();
        if (e != ILabel.EMPTY) {
            gui.hand = e;
            gui.preventRecipeScreen = true;
            return interruptFalse();
        }
        return pass();
    }

    public static EventResult onMouseClicked(Minecraft client, Screen screen, double mouseX, double mouseY, int button) {
        if (!(screen instanceof JecaGui)) return pass();
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        if (gui.root.onMouseClicked(gui, xMouse, yMouse, button))
            return interruptFalse();
        else if (gui.hand != ILabel.EMPTY) {
            gui.hand = ILabel.EMPTY;
            gui.preventRecipeScreen = false;
            return interruptFalse();
        }
        return pass();
    }


    public static EventResult onMouseScroll(Minecraft client, Screen screen, double mouseX, double mouseY, double amount) {
        if (!(screen instanceof JecaGui)) return pass();
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        if (amount != 0)
            gui.root.onMouseScroll(gui, xMouse, yMouse, (int) amount);
        return pass();
    }

    public static EventResult onMouseReleased(Minecraft minecraft, Screen screen, double mouseX, double mouseY, int button) {
        if (!(screen instanceof JecaGui)) return pass();
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        gui.root.onMouseReleased(gui, xMouse, yMouse, button);
        return gui.hand == ILabel.EMPTY ? pass() : interruptFalse();
    }

    public static EventResult onMouseDragged(Minecraft client, Screen screen, double mouseX1, double mouseY1, int button, double mouseX2, double mouseY2) {
        if (!(screen instanceof JecaGui)) return pass();
        JecaGui gui = getCurrent();
        int xMouse = getMouseX();
        int yMouse = getMouseY();
        gui.root.onMouseDragged(gui, xMouse, yMouse, (int) mouseX2, (int) mouseY2);
        return pass();
    }

    @Override
    public void tick() {
        super.tick();
        root.onTick(this);
    }

    @Nullable
    public Slot getSlotUnderMouse() {
        Container i = new SimpleContainer(ItemStack.EMPTY);
        Slot s = new Slot(i, 0, 0, 0);
        ILabel l = getLabelUnderMouse();
        Object rep = l == null ? null : l.getRepresentation();
        if (rep instanceof ItemStack) s.set((ItemStack) rep);
        return s;
    }

    public static boolean mouseIn(int xPos, int yPos, int xSize, int ySize, int xMouse, int yMouse) {
        return xMouse > xPos && yMouse > yPos && xMouse <= xPos + xSize && yMouse <= yPos + ySize;
    }

    public static void displayGui(IGui root) {
        Screen s = Minecraft.getInstance().screen;
        if (s instanceof JecaGui) {
            displayGui(root, true);
        } else {
            displayGui(root, null);
        }
    }

    public static void displayGui(IGui root, @Nullable JecaGui parent) {
        Minecraft mc = Minecraft.getInstance();
        Screen s = mc.screen;
        JecaGui gui = new JecaGui(parent, root.acceptsTransfer(), root, false);
        if (Utilities.isRecipeScreen(s) || s instanceof ChatScreen) {
            JecaGui.override = gui;
        } else {
            Runnable runnable = () -> {
                root.onVisible(gui);
                last = gui;
                mc.setScreen(gui);
            };
            if (Minecraft.getInstance().isSameThread())
                runnable.run();
            else
                Minecraft.getInstance().submitAsync(runnable);
        }
    }

    public static void displayGui(IGui root, boolean updateParent) {
        JecaGui current = JecaGui.getCurrent();
        JecaGui parent = updateParent ? current : current.parent;
        JecaGui.displayGui(root, parent);
    }

    public PoseStack getMatrix() {
        return matrix;
    }

    public void setMatrix(PoseStack matrix) {
        this.matrix = matrix;
    }

    public Utilities.OffsetStack getItemOffsetStack() {
        return itemOffset;
    }

    /**
     * @return The currently displayed {@link JecaGui}
     * Make sure the method is called when a {@link JecaGui} is displayed!
     * Otherwise it will throw a {@link NullPointerException}
     */
    public static JecaGui getCurrent() {
        Screen gui = Minecraft.getInstance().screen;
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
        Minecraft.getInstance().setScreen(gui);
    }

    @Nullable
    public ILabel getLabelUnderMouse() {
        Wrapper<ILabel> l = new Wrapper<>(null);
        root.getLabelUnderMouse(getGlobalMouseX(), getGlobalMouseY(), l);
        return l.value;
    }

    public static EventResult onKeyPressed(Minecraft minecraft, int keyCode, int scanCode, int action, int modifiers) {
        if (keyOpenGuiCraft.isDown()) JecaGui.openGuiCraft(null, 0);
        if (keyOpenGuiMath.isDown()) JecaGui.openGuiMath(null, 0);
        return pass();
    }


    public static EventResult onTooltip(PoseStack poseStack, List<? extends ClientTooltipComponent> components, int x, int y) {
        if (Minecraft.getInstance().screen instanceof JecaGui) {
            JecaGui gui = getCurrent();
            return gui.root.onTooltip(gui, x - gui.leftPos,
                    y - gui.topPos, new ArrayList<>()) ? interruptFalse() : pass();
        }
        return pass();
    }

    public static CompoundEventResult<Screen> onGuiOpen(Screen screen) {
        if (override != null) {
            override.root.onVisible(override);
            last = override;
            Screen s = override;
            override = null;
            return CompoundEventResult.interruptTrue(s);
        }
        if (Minecraft.getInstance().screen instanceof JecaGui gui && gui.preventRecipeScreen && Utilities.isRecipeScreen(screen)) {
            gui.preventRecipeScreen = false;
            return CompoundEventResult.interruptFalse(gui);
        }
        return CompoundEventResult.pass();
    }

    @Environment(EnvType.CLIENT)
    public static int openGuiMath(@Nullable ItemStack is, int slot) {
        boolean ret = is == null && Controller.isServerActive();
        String s = "jecalculation.chat.server_mode";
        if (ret) getPlayer().displayClientMessage(Component.translatable(s), false);
        else JecaGui.displayGui(new GuiMath(is, slot));
        return ret ? 1 : 0;
    }

    @Environment(EnvType.CLIENT)
    public static int openGuiCraft(@Nullable ItemStack is, int slot) {
        boolean ret = is == null && Controller.isServerActive();
        String s = "jecalculation.chat.server_mode";
        if (ret) getPlayer().displayClientMessage(Component.translatable(s), false);
        else JecaGui.displayGui(new GuiCraft(is, slot));
        return ret ? 1 : 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        renderBackground(matrixStack);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrix = matrixStack;
        mouseX -= leftPos;
        mouseY -= topPos;
        matrixStack.pushPose();
        matrixStack.translate(leftPos, topPos, 0);
        root.onDraw(this, mouseX, mouseY);
        matrixStack.popPose();
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 80);
        hand.drawLabel(this, mouseX + leftPos, mouseY + topPos, true, true);
        matrixStack.popPose();
        List<String> tooltip = new ArrayList<>();
        root.onTooltip(this, mouseX, mouseY, tooltip);
        drawHoveringText(matrixStack, tooltip, mouseX + leftPos, mouseY + topPos, font);
    }

    // modified from vanilla
    public void drawHoveringText(PoseStack matrixStack, List<String> textLines, int x, int y, net.minecraft.client.gui.Font font) {
        if (!textLines.isEmpty()) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 400);
            int i = 0;
            int separators = 0;
            for (String s : textLines) {
                int j = this.font.width(s);
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
            fillGradient(matrixStack, l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            fillGradient(matrixStack, l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            fillGradient(matrixStack, l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            fillGradient(matrixStack, l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            fillGradient(matrixStack, l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            fillGradient(matrixStack, l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            fillGradient(matrixStack, l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            fillGradient(matrixStack, l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            fillGradient(matrixStack, l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
            for (String s1 : textLines) {
                //noinspection StringEquality
                if (s1 == SEPARATOR) i2 += 2;
                else {
                    font.drawShadow(matrixStack, s1, (float) l1, (float) i2, -1);
                    i2 += 10;
                }
            }
            matrixStack.popPose();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }
    }

    public void drawResource(Resource r, int xPos, int yPos) {
        drawResource(r, xPos, yPos, 0xFFFFFF);
    }

    public void drawResource(Resource r, int xPos, int yPos, int color) {
        setColor(color);
        RenderSystem.setShaderTexture(0, r.getResourceLocation());
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

    private void setColor(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = (~(color >> 24) & 0xFF) / 255.0F;
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    public void drawFluid(Fluid f, int xPos, int yPos, int xSize, int ySize) {
        TextureAtlasSprite fluidTexture = FluidStackHooks.getStillTexture(f);
        if (fluidTexture == null)
            fluidTexture = FluidStackHooks.getStillTexture(Fluids.WATER);
        if (fluidTexture == null)
            return;
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        setColor(FluidStackHooks.getColor(f) & 0x00FFFFFF);
        blit(matrix, xPos, yPos, 0, xSize, ySize, fluidTexture);
    }

    public void drawRectangle(int xPos, int yPos, int xSize, int ySize, int color) {
        fill(matrix, xPos, yPos, xPos + xSize, yPos + ySize, color);
    }

    public int getStringWidth(String s) {
        return font.width(s);
    }

    public void drawSplitText(float xPos, float yPos, int width, FontType f, String s) {
        drawSplitText(xPos, yPos, f, Utilities.I18n.wrap(s, width));
    }

    public void drawSplitText(float xPos, float yPos, FontType f, List<String> ss) {
        drawText(xPos, yPos, f, () -> {
            int y = 0;
            for (String i : ss) {
                if (f.shadow) font.drawShadow(matrix, i, 0, y, f.color);
                else font.draw(matrix, i, 0, y, f.color);
                y += font.lineHeight + 1;
            }
        });
    }

    public void drawText(float xPos, float yPos, FontType f, String s) {
        drawText(xPos, yPos, Integer.MAX_VALUE, f, s);
    }

    public void drawText(float xPos, float yPos, int width, FontType f, String s) {
        drawText(xPos, yPos, f, () -> {
            String str = s;
            int strWidth = f.getTextWidth(str);
            int ellipsisWidth = f.getTextWidth("...");
            if (strWidth > width && strWidth > ellipsisWidth)
                str = f.trimToWidth(str, width - ellipsisWidth).trim() + "...";
            if (f.shadow) font.drawShadow(matrix, str, 0, 0, f.color);
            else font.draw(matrix, str, 0, 0, f.color);
        });
    }

    private void drawText(float xPos, float yPos, FontType f, Runnable r) {
        getMatrix().pushPose();
        getMatrix().translate(xPos, yPos, 200);
        if (f.half) getMatrix().scale(0.5f, 0.5f, 1);
        r.run();
        getMatrix().popPose();
    }

    public void drawItemStack(int xPos, int yPos, ItemStack is, boolean centred, boolean hand) {
        if (centred) {
            xPos -= 8;
            yPos -= 8;
        }

        int x = hand ? xPos : leftPos + xPos;
        int y = hand ? yPos : topPos + yPos;

        RenderSystem.enableDepthTest();
        itemRenderer.renderAndDecorateItem(is, x + itemOffset.x(), y + itemOffset.y());
        itemRenderer.renderGuiItemDecorations(font, is, leftPos + xPos, topPos + yPos, null);
        RenderSystem.disableDepthTest();
    }

    @Override
    protected void init() {
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
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

    @Environment(EnvType.CLIENT)
    public static class FontType {
        public static final FontType SHADOW = new FontType(JecaGui.COLOR_TEXT_WHITE, true, false, false);
        public static final FontType PLAIN = new FontType(JecaGui.COLOR_TEXT_GREY, false, false, false);
        public static final FontType RAW = new FontType(JecaGui.COLOR_TEXT_GREY, false, false, true);
        public static final FontType HALF = new FontType(JecaGui.COLOR_TEXT_WHITE, true, true, true);

        public int color;
        public boolean shadow, half, raw;
        private final Font font = Minecraft.getInstance().font;

        public FontType(int color, boolean shadow, boolean half, boolean raw) {
            this.color = color;
            this.shadow = shadow;
            this.half = half;
            this.raw = raw;
        }

        public int getTextWidth(String s) {
            return (int) Math.ceil(font.width(s) * (half ? 0.5f : 1));
        }

        public int getTextHeight() {
            return (int) Math.ceil(font.lineHeight * (half ? 0.5f : 1));
        }

        public String trimToWidth(String s, int i) {
            return font.plainSubstrByWidth(s, i * (half ? 2 : 1));
        }
    }

    @Environment(EnvType.CLIENT)
    public static class JecaContainer extends AbstractContainerMenu {
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
        public ItemStack quickMoveStack(Player player, int index) {
            return null;
        }

        @Override
        public boolean stillValid(Player playerIn) {
            return true;
        }
    }


    @Environment(EnvType.CLIENT)
    public static class ContainerTransfer extends JecaContainer {
    }

    @Environment(EnvType.CLIENT)
    public static class ContainerNonTransfer extends JecaContainer {
    }
}
