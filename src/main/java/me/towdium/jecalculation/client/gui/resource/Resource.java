package me.towdium.jecalculation.client.gui.resource;

import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.util.ResourceLocation;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
public class Resource {
    public static final ResourceLocation location = new ResourceLocation(
            JustEnoughCalculation.Reference.MODID, "textures/gui/resources.png");
    public static final Resource BTN_LABEL_N = new Resource(0, 14, 14, 14);
    public static final Resource BTN_LABEL_F = new Resource(0, 0, 14, 14);
    public static final Resource BTN_NEW_N = new Resource(14, 14, 14, 14);
    public static final Resource BTN_NEW_F = new Resource(14, 0, 14, 14);
    public static final Resource BTN_SEARCH_N = new Resource(28, 14, 14, 14);
    public static final Resource BTN_SEARCH_F = new Resource(28, 0, 14, 14);
    public static final Resource BTN_SAVE_N = new Resource(42, 14, 14, 14);
    public static final Resource BTN_SAVE_F = new Resource(42, 0, 14, 14);
    public static final Resource BTN_COPY_N = new Resource(56, 14, 14, 14);
    public static final Resource BTN_COPY_F = new Resource(56, 0, 14, 14);
    public static final Resource BTN_DEL_N = new Resource(70, 14, 14, 14);
    public static final Resource BTN_DEL_F = new Resource(70, 0, 14, 14);
    public static final Resource BTN_YES_N = new Resource(84, 14, 14, 14);
    public static final Resource BTN_YES_F = new Resource(84, 0, 14, 14);
    public static final Resource BTN_NO_N = new Resource(98, 14, 14, 14);
    public static final Resource BTN_NO_F = new Resource(98, 0, 14, 14);
    public static final Resource WGT_BUTTON_N = new Resource(60, 232, 20, 20);
    public static final Resource WGT_BUTTON_F = new Resource(80, 232, 20, 20);
    public static final Resource WGT_BUTTON_D = new Resource(100, 232, 20, 20);
    public static final Resource WGT_SLOT = new Resource(40, 232, 20, 20);
    public static final Resource WGT_LINE = new Resource(0, 252, 163, 4);
    public static final Resource WGT_PANEL = new Resource(20, 232, 20, 20);
    public static final Resource WGT_ARR_L_F = new Resource(0, 232, 9, 9);
    public static final Resource WGT_ARR_L_N = new Resource(0, 242, 9, 9);
    public static final Resource WGT_ARR_R_F = new Resource(10, 232, 9, 9);
    public static final Resource WGT_ARR_R_N = new Resource(10, 242, 9, 9);
    public static final Resource ICN_RECENT_N = new Resource(0, 42, 14, 14);
    public static final Resource ICN_RECENT_F = new Resource(0, 28, 14, 14);
    public static final Resource ICN_INPUT_N = new Resource(14, 42, 14, 14);
    public static final Resource ICN_INPUT_F = new Resource(14, 28, 14, 14);
    public static final Resource ICN_OUTPUT_N = new Resource(28, 42, 14, 14);
    public static final Resource ICN_OUTPUT_F = new Resource(28, 28, 14, 14);
    public static final Resource ICN_CATALYST_N = new Resource(42, 42, 14, 14);
    public static final Resource ICN_CATALYST_F = new Resource(42, 28, 14, 14);

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
