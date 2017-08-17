package me.towdium.jecalculation.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.resource.Resource;
import me.towdium.jecalculation.client.widget.Widget;
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
import java.util.List;

/**
 * Author: towdium
 * Date:   8/12/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JecGui extends GuiContainer {
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

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        widgetManager.onDraw(mouseX, mouseY);
        GlStateManager.enableLighting();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID,
                "textures/gui/" + getBackground() + ".png"), guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // here I haven't copied the event thingy, don't think anyone will use it
        widgetManager.onClick(mouseX, mouseY, mouseButton);
    }

    // new functions

    public void drawResource(Resource r, int xPos, int yPos) {
        drawTexture(Resource.location, xPos, yPos, r.getXPos(), r.getYPos(), r.getXSize(), r.getYSize());
    }

    public void drawResourceContinuous(
            Resource r, int xPos, int yPos, int xSize, int ySize,
            int borderTop, int borderBottom, int borderLeft, int borderRight) {
        drawTextureContinuous(Resource.location, xPos, yPos, xSize, ySize,
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

    protected String getBackground() {
        return "error";
    }

    public class WidgetManager {
        protected List<Widget> widgets = new ArrayList<>();

        public void add(Widget w) {
            widgets.add(w);
        }

        public void remove(Widget w) {
            widgets.remove(w);
            w.onRemoved(JecGui.this);
        }

        public void onInit() {
            widgets.forEach(w -> w.onGuiInit(JecGui.this));
        }

        public void onDraw(int mouseX, int mouseY) {
            widgets.forEach(widget -> widget.onDraw(JecGui.this, mouseX, mouseY));
        }

        public void onClick(int xMouse, int yMouse, int button) {
            widgets.forEach(w -> w.onClicked(JecGui.this, xMouse, yMouse, button));
        }
    }
}
