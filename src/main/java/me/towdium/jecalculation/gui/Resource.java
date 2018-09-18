package me.towdium.jecalculation.gui;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class Resource {
    public static final ResourceLocation location = new ResourceLocation(
            JustEnoughCalculation.Reference.MODID, "textures/gui/resources.png");
    public static final Resource BTN_LABEL_N = getIcon(0, 1);
    public static final Resource BTN_LABEL_F = getIcon(0, 0);
    public static final Resource BTN_NEW_N = getIcon(1, 1);
    public static final Resource BTN_NEW_F = getIcon(1, 0);
    public static final Resource BTN_SEARCH_N = getIcon(2, 1);
    public static final Resource BTN_SEARCH_F = getIcon(2, 0);
    public static final Resource BTN_SAVE_N = getIcon(3, 1);
    public static final Resource BTN_SAVE_F = getIcon(3, 0);
    public static final Resource BTN_COPY_N = getIcon(4, 1);
    public static final Resource BTN_COPY_F = getIcon(4, 0);
    public static final Resource BTN_COPY_D = getIcon(4, 2);
    public static final Resource BTN_DEL_N = getIcon(5, 1);
    public static final Resource BTN_DEL_F = getIcon(5, 0);
    public static final Resource BTN_YES_N = getIcon(6, 1);
    public static final Resource BTN_YES_F = getIcon(6, 0);
    public static final Resource BTN_YES_D = getIcon(6, 2);
    public static final Resource BTN_NO_N = getIcon(7, 1);
    public static final Resource BTN_NO_F = getIcon(7, 0);
    public static final Resource BTN_NO_D = getIcon(7, 2);
    public static final Resource BTN_DISAMB_N = getIcon(8, 1);
    public static final Resource BTN_DISAMB_F = getIcon(8, 0);
    public static final Resource BTN_DISAMB_D = getIcon(8, 2);
    public static final Resource BTN_IN_N = getIcon(9, 1);
    public static final Resource BTN_IN_F = getIcon(9, 0);
    public static final Resource BTN_IN_D = getIcon(9, 2);
    public static final Resource BTN_OUT_N = getIcon(10, 1);
    public static final Resource BTN_OUT_F = getIcon(10, 0);
    public static final Resource BTN_OUT_D = getIcon(10, 2);
    public static final Resource BTN_CAT_N = getIcon(11, 1);
    public static final Resource BTN_CAT_F = getIcon(11, 0);
    public static final Resource BTN_CAT_D = getIcon(11, 2);
    public static final Resource BTN_LIST_N = getIcon(12, 1);
    public static final Resource BTN_LIST_F = getIcon(12, 0);
    public static final Resource BTN_LIST_D = getIcon(12, 2);
    public static final Resource BTN_META_E_N = getIcon(14, 0);
    public static final Resource BTN_META_E_F = getIcon(13, 0);
    public static final Resource BTN_META_D_N = getIcon(16, 0);
    public static final Resource BTN_META_D_F = getIcon(15, 0);
    public static final Resource BTN_NBT_E_N = getIcon(14, 1);
    public static final Resource BTN_NBT_E_F = getIcon(13, 1);
    public static final Resource BTN_NBT_D_N = getIcon(16, 1);
    public static final Resource BTN_NBT_D_F = getIcon(15, 1);
    public static final Resource BTN_CAP_E_N = getIcon(14, 2);
    public static final Resource BTN_CAP_E_F = getIcon(13, 2);
    public static final Resource BTN_CAP_D_N = getIcon(16, 2);
    public static final Resource BTN_CAP_D_F = getIcon(15, 2);
    public static final Resource WGT_BUTTON_N = new Resource(40, 232, 20, 20);
    public static final Resource WGT_BUTTON_F = new Resource(60, 232, 20, 20);
    public static final Resource WGT_BUTTON_D = new Resource(80, 232, 20, 20);
    public static final Resource WGT_SLOT = new Resource(20, 232, 20, 20);
    public static final Resource WGT_LINE = new Resource(0, 252, 163, 4);
    public static final Resource WGT_PANEL = new Resource(0, 232, 20, 20);
    public static final Resource WGT_PAGER_FN = new Resource(100, 232, 20, 20);
    public static final Resource WGT_PAGER_F0 = new Resource(120, 232, 20, 20);
    public static final Resource WGT_PAGER_N = new Resource(140, 232, 20, 20);
    public static final Resource WGT_ARR_L_F = new Resource(160, 232, 7, 9);
    public static final Resource WGT_ARR_L_N = new Resource(167, 232, 7, 9);
    public static final Resource WGT_ARR_L_D = new Resource(174, 232, 7, 9);
    public static final Resource WGT_ARR_R_F = new Resource(160, 242, 7, 9);
    public static final Resource WGT_ARR_R_N = new Resource(167, 242, 7, 9);
    public static final Resource WGT_ARR_R_D = new Resource(174, 242, 7, 9);
    public static final Resource WGT_SCROLL = new Resource(181, 232, 12, 15);
    public static final Resource ICN_RECENT_N = getIcon(0, 4);
    public static final Resource ICN_RECENT_F = getIcon(0, 3);
    public static final Resource ICN_INPUT_N = getIcon(1, 4);
    public static final Resource ICN_INPUT_F = getIcon(1, 3);
    public static final Resource ICN_OUTPUT_N = getIcon(2, 4);
    public static final Resource ICN_OUTPUT_F = getIcon(2, 3);
    public static final Resource ICN_CATALYST_N = getIcon(3, 4);
    public static final Resource ICN_CATALYST_F = getIcon(3, 3);
    public static final Resource ICN_LIST_N = getIcon(4, 4);
    public static final Resource ICN_LIST_F = getIcon(4, 3);
    public static final Resource ICN_LABEL_N = getIcon(5, 4);
    public static final Resource ICN_LABEL_F = getIcon(5, 3);
    public static final Resource ICN_HELP_N = getIcon(6, 4);
    public static final Resource ICN_HELP_F = getIcon(6, 3);
    public static final Resource ICN_STACK_N = getIcon(7, 4);
    public static final Resource ICN_STACK_F = getIcon(7, 3);
    public static final Resource ICN_TEXT_N = getIcon(8, 4);
    public static final Resource ICN_TEXT_F = getIcon(8, 3);
    public static final Resource ICN_MULTI_N = getIcon(9, 4);
    public static final Resource ICN_MULTI_F = getIcon(9, 3);
    public static final Resource LBL_FRAME = getLabel(0);
    public static final Resource LBL_FLUID = getLabel(1);
    public static final Resource LBL_UNIV_B = getLabel(2);
    public static final Resource LBL_UNIV_F = getLabel(3);
    public static final Resource LBL_FR_UL = getLabel(4);
    public static final Resource LBL_FR_UR = getLabel(5);
    public static final Resource LBL_FR_LL = getLabel(6);

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

    private static Resource getIcon(int x, int y) {
        return new Resource(x * 14, y * 14, 14, 14);
    }

    private static Resource getLabel(int x) {
        return new Resource(x * 16, 70, 16, 16);
    }
}
