package me.towdium.jecalculation.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class Resource {
    public static final ResourceLocation location = new ResourceLocation(
            JustEnoughCalculation.Reference.MODID, "textures/gui/resources.png");
    // letters abbr for button & icon: N - normal, F - focused, D - disabled
    // letters abbr for fuzzy related: N - normal, F - fuzzy
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
    public static final Resource BTN_META_N_N = getIcon(14, 0);
    public static final Resource BTN_META_N_F = getIcon(13, 0);
    public static final Resource BTN_META_F_N = getIcon(16, 0);
    public static final Resource BTN_META_F_F = getIcon(15, 0);
    public static final Resource BTN_META_D = getIcon(17, 0);
    public static final Resource BTN_NBT_N_N = getIcon(14, 1);
    public static final Resource BTN_NBT_N_F = getIcon(13, 1);
    public static final Resource BTN_NBT_F_N = getIcon(16, 1);
    public static final Resource BTN_NBT_F_F = getIcon(15, 1);
    public static final Resource BTN_NBT_D = getIcon(17, 1);
    public static final Resource BTN_CAP_N_N = getIcon(14, 2);
    public static final Resource BTN_CAP_N_F = getIcon(13, 2);
    public static final Resource BTN_CAP_F_N = getIcon(16, 2);
    public static final Resource BTN_CAP_F_F = getIcon(15, 2);
    public static final Resource BTN_CAP_D = getIcon(17, 2);
    public static final Resource WGT_BUTTON_N = new Resource(40, 232, 20, 20);
    public static final Resource WGT_BUTTON_F = new Resource(60, 232, 20, 20);
    public static final Resource WGT_BUTTON_D = new Resource(80, 232, 20, 20);
    public static final Resource WGT_BUTTON_S_N = new Resource(0, 212, 20, 20);
    public static final Resource WGT_BUTTON_S_F = new Resource(20, 212, 20, 20);
    public static final Resource WGT_BUTTON_S_D = new Resource(40, 212, 20, 20);
    public static final Resource WGT_SLOT = new Resource(20, 232, 20, 20);
    public static final Resource WGT_LINE = new Resource(0, 252, 163, 4);
    public static final Resource WGT_PANEL_F = new Resource(0, 232, 20, 20);
    public static final Resource WGT_PAGER_FN = new Resource(100, 232, 20, 20);
    public static final Resource WGT_PAGER_F0 = new Resource(120, 232, 20, 20);
    public static final Resource WGT_PANEL_N = new Resource(140, 232, 20, 20);
    public static final Resource WGT_ARR_L_F = new Resource(160, 232, 7, 9);
    public static final Resource WGT_ARR_L_N = new Resource(167, 232, 7, 9);
    public static final Resource WGT_ARR_L_D = new Resource(174, 232, 7, 9);
    public static final Resource WGT_ARR_R_F = new Resource(160, 242, 7, 9);
    public static final Resource WGT_ARR_R_N = new Resource(167, 242, 7, 9);
    public static final Resource WGT_ARR_R_D = new Resource(174, 242, 7, 9);
    public static final Resource WGT_SCROLL = new Resource(181, 232, 12, 15);
    public static final Resource WGT_HELP_F = new Resource(193, 232, 20, 20);
    public static final Resource WGT_HELP_N = new Resource(213, 232, 20, 20);
    public static final Resource WGT_HELP_B = new Resource(233, 232, 20, 20);
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

    public static final ResourceGroup BTN_LABEL = new ResourceGroup(BTN_LABEL_N, BTN_LABEL_F);
    public static final ResourceGroup BTN_NEW = new ResourceGroup(BTN_NEW_N, BTN_NEW_F);
    public static final ResourceGroup BTN_SEARCH = new ResourceGroup(BTN_SEARCH_N, BTN_SEARCH_F);
    public static final ResourceGroup BTN_SAVE = new ResourceGroup(BTN_SAVE_N, BTN_SAVE_F);
    public static final ResourceGroup BTN_COPY = new ResourceGroup(BTN_COPY_N, BTN_COPY_F, BTN_COPY_D);
    public static final ResourceGroup BTN_DEL = new ResourceGroup(BTN_DEL_N, BTN_DEL_F);
    public static final ResourceGroup BTN_YES = new ResourceGroup(BTN_YES_N, BTN_YES_F, BTN_YES_D);
    public static final ResourceGroup BTN_NO = new ResourceGroup(BTN_NO_N, BTN_NO_F, BTN_NO_D);
    public static final ResourceGroup BTN_DISAMB = new ResourceGroup(BTN_DISAMB_N, BTN_DISAMB_F, BTN_DISAMB_D);
    public static final ResourceGroup BTN_IN = new ResourceGroup(BTN_IN_N, BTN_IN_F, BTN_IN_D);
    public static final ResourceGroup BTN_OUT = new ResourceGroup(BTN_OUT_N, BTN_OUT_F, BTN_OUT_D);
    public static final ResourceGroup BTN_CAT = new ResourceGroup(BTN_CAT_N, BTN_CAT_F, BTN_CAT_D);
    public static final ResourceGroup BTN_LIST = new ResourceGroup(BTN_LIST_N, BTN_LIST_F, BTN_LIST_D);
    public static final ResourceGroup BTN_META_N = new ResourceGroup(BTN_META_N_N, BTN_META_N_F, BTN_META_D);
    public static final ResourceGroup BTN_CAP_N = new ResourceGroup(BTN_CAP_N_N, BTN_CAP_N_F, BTN_CAP_D);
    public static final ResourceGroup BTN_NBT_N = new ResourceGroup(BTN_NBT_N_N, BTN_NBT_N_F, BTN_NBT_D);
    public static final ResourceGroup BTN_META_F = new ResourceGroup(BTN_META_F_N, BTN_META_F_F, BTN_META_D);
    public static final ResourceGroup BTN_CAP_F = new ResourceGroup(BTN_CAP_F_N, BTN_CAP_F_F, BTN_CAP_D);
    public static final ResourceGroup BTN_NBT_F = new ResourceGroup(BTN_NBT_F_N, BTN_NBT_F_F, BTN_NBT_D);
    public static final ResourceGroup WGT_ARR_L = new ResourceGroup(WGT_ARR_L_N, WGT_ARR_L_F, WGT_ARR_L_D);
    public static final ResourceGroup WGT_ARR_R = new ResourceGroup(WGT_ARR_R_N, WGT_ARR_R_F, WGT_ARR_R_D);
    public static final ResourceGroup ICN_RECENT = new ResourceGroup(ICN_RECENT_N, ICN_RECENT_F);
    public static final ResourceGroup ICN_INPUT = new ResourceGroup(ICN_INPUT_N, ICN_INPUT_F);
    public static final ResourceGroup ICN_OUTPUT = new ResourceGroup(ICN_OUTPUT_N, ICN_OUTPUT_F);
    public static final ResourceGroup ICN_CATALYST = new ResourceGroup(ICN_CATALYST_N, ICN_CATALYST_F);
    public static final ResourceGroup ICN_LIST = new ResourceGroup(ICN_LIST_N, ICN_LIST_F);
    public static final ResourceGroup ICN_LABEL = new ResourceGroup(ICN_LABEL_N, ICN_LABEL_F);
    public static final ResourceGroup ICN_TEXT = new ResourceGroup(ICN_TEXT_N, ICN_TEXT_F);

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

    public static class ResourceGroup {
        public Resource normal;
        public Resource focused;
        public Resource disabled;

        public ResourceGroup(Resource normal, Resource focused, Resource disabled) {
            this.normal = normal;
            this.focused = focused;
            this.disabled = disabled;
        }

        public ResourceGroup(Resource normal, Resource focused) {
            this.normal = normal;
            this.focused = focused;
        }
    }
}
