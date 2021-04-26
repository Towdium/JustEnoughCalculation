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
    public static final Resource LABEL_NORMAL = new Resource(0, 14, 14, 14);
    public static final Resource LABEL_FOCUSED = new Resource(0, 0, 14, 14);
    public static final Resource NEW_NORMAL = new Resource(14, 14, 14, 14);
    public static final Resource NEW_FOCUSED = new Resource(14, 0, 14, 14);
    public static final Resource SEARCH_NORMAL = new Resource(28, 14, 14, 14);
    public static final Resource SEARCH_FOCUSED = new Resource(28, 0, 14, 14);
    public static final Resource BUTTON_NORMAL = new Resource(216, 0, 20, 20);
    public static final Resource BUTTON_FOCUSED = new Resource(236, 0, 20, 20);
    public static final Resource SLOT = new Resource(196, 0, 20, 20);
    public static final Resource LINE = new Resource(0, 28, 163, 4);

    protected int xPos, yPos, xSize, ySize;

    public Resource(int xPos, int yPos, int xSize, int ySize) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
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
}
