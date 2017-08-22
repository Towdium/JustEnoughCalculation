package me.towdium.jecalculation.client.resource;

import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.util.ResourceLocation;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
public class Resource {
    public static final ResourceLocation location = new ResourceLocation(
            JustEnoughCalculation.Reference.MODID, "textures/gui/resources.png");
    public static final Resource BUTTON_LABEL_N = new Resource(0, 14, 14, 14);
    public static final Resource BUTTON_LABEL_F = new Resource(0, 0, 14, 14);
    public static final Resource BUTTON_NEW_N = new Resource(14, 14, 14, 14);
    public static final Resource BUTTON_NEW_F = new Resource(14, 0, 14, 14);
    public static final Resource BUTTON_SEARCH_N = new Resource(28, 14, 14, 14);
    public static final Resource BUTTON_SEARCH_F = new Resource(28, 0, 14, 14);
    public static final Resource WIDGET_BUTTON_N = new Resource(216, 0, 20, 20);
    public static final Resource WIDGET_BUTTON_F = new Resource(236, 0, 20, 20);
    public static final Resource WIDGET_SLOT = new Resource(196, 0, 20, 20);
    public static final Resource WIDGET_LINE = new Resource(0, 28, 163, 4);
    public static final Resource WIDGET_PANEL = new Resource(176, 0, 20, 20);
    public static final Resource WIDGET_ARR_L_F = new Resource(156, 0, 9, 9);
    public static final Resource WIDGET_ARR_L_N = new Resource(156, 10, 9, 9);
    public static final Resource WIDGET_ARR_R_F = new Resource(166, 0, 9, 9);
    public static final Resource WIDGET_ARR_R_N = new Resource(166, 10, 9, 9);
    public static final Resource ICON_RECENT_N = new Resource(0, 46, 14, 14);
    public static final Resource ICON_RECENT_F = new Resource(0, 32, 14, 14);

    protected int xPos, yPos, xSize, ySize;
    protected ResourceLocation rl;

    public Resource(int xPos, int yPos, int xSize, int ySize, ResourceLocation rl) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        this.rl = rl;
    }

    public Resource(int xPos, int yPos, int xSize, int ySize) {
        this(xPos, yPos, xSize, ySize, location);
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public ResourceLocation getResourceLocation() {
        return rl;
    }
}
