package me.towdium.jecalculation.gui;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-8-17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class Resource {
    public static final ResourceLocation location = new ResourceLocation(
            JustEnoughCalculation.MODID, "textures/gui/resources.png");
    // letters abbr for button & icon: N - normal, F - focused, D - disabled
    // letters abbr for fuzzy related: N - normal, F - fuzzy
    // letters abbr for state related: N - normal, A - active
    public static final Resource BTN_LABEL_N = getIcon(0, 0);
    public static final Resource BTN_NEW_N = getIcon(1, 0);
    public static final Resource BTN_SEARCH_N = getIcon(2, 0);
    public static final Resource BTN_SAVE_N = getIcon(3, 0);
    public static final Resource BTN_SAVE_D = getIcon(3, 1);
    public static final Resource BTN_COPY_N = getIcon(4, 0);
    public static final Resource BTN_COPY_D = getIcon(4, 1);
    public static final Resource BTN_DEL_N = getIcon(5, 0);
    public static final Resource BTN_YES_N = getIcon(6, 0);
    public static final Resource BTN_YES_D = getIcon(6, 1);
    public static final Resource BTN_NO_N = getIcon(7, 0);
    public static final Resource BTN_NO_D = getIcon(7, 1);
    public static final Resource BTN_DISAMB_N = getIcon(8, 0);
    public static final Resource BTN_DISAMB_D = getIcon(8, 1);
    public static final Resource BTN_IN_N = getIcon(9, 0);
    public static final Resource BTN_IN_D = getIcon(9, 1);
    public static final Resource BTN_OUT_N = getIcon(10, 0);
    public static final Resource BTN_OUT_D = getIcon(10, 1);
    public static final Resource BTN_CAT_N = getIcon(11, 0);
    public static final Resource BTN_CAT_D = getIcon(11, 1);
    public static final Resource BTN_LIST_N = getIcon(12, 0);
    public static final Resource BTN_LIST_D = getIcon(12, 1);
    public static final Resource BTN_IMPORT_N = getIcon(13, 0);
    public static final Resource BTN_IMPORT_D = getIcon(13, 1);
    public static final Resource BTN_EXPORT_N_N = getIcon(14, 0);
    public static final Resource BTN_EXPORT_N_D = getIcon(14, 1);
    public static final Resource BTN_EXPORT_1_N = getIcon(15, 0);
    public static final Resource BTN_EXPORT_1_D = getIcon(15, 1);
    public static final Resource BTN_INV_E_N = getIcon(16, 0);
    public static final Resource BTN_INV_D_N = getIcon(17, 0);
    public static final Resource BTN_CAP_N_N = getIcon(0, 2);
    public static final Resource BTN_CAP_F_N = getIcon(1, 2);
    public static final Resource BTN_CAP_D = getIcon(0, 3);
    public static final Resource BTN_META_N_N = getIcon(2, 2);
    public static final Resource BTN_META_F_N = getIcon(3, 2);
    public static final Resource BTN_META_D = getIcon(2, 3);
    public static final Resource BTN_NBT_N_N = getIcon(4, 2);
    public static final Resource BTN_NBT_F_N = getIcon(5, 2);
    public static final Resource BTN_NBT_D = getIcon(4, 3);
    public static final Resource BTN_EDIT_N = getIcon(6, 2);
    public static final Resource BTN_EDIT_D = getIcon(6, 3);
    public static final Resource BTN_PICK_N = getIcon(7, 2);
    public static final Resource BTN_DELETE_N_N = getIcon(8, 2);
    public static final Resource BTN_DELETE_N_D = getIcon(8, 3);
    public static final Resource BTN_DELETE_1_N = getIcon(9, 2);
    public static final Resource BTN_DELETE_1_D = getIcon(9, 3);
    public static final Resource BTN_DOWN_N_N = getIcon(10, 2);
    public static final Resource BTN_DOWN_A_N = getIcon(11, 2);
    public static final Resource BTN_UP_N_N = getIcon(12, 2);

    public static final Resource WGT_BUTTON_N = getWidget(0);
    public static final Resource WGT_BUTTON_F = getWidget(1);
    public static final Resource WGT_BUTTON_D = getWidget(2);
    public static final Resource WGT_BUTTON_S_N = getWidget(3);
    public static final Resource WGT_BUTTON_S_F = getWidget(4);
    public static final Resource WGT_BUTTON_S_D = getWidget(5);
    public static final Resource WGT_SLOT = getWidget(6);
    public static final Resource WGT_PANEL_N = getWidget(7);
    public static final Resource WGT_PANEL_F = getWidget(8);
    public static final Resource WGT_PAGER_F = getWidget(9);
    public static final Resource WGT_HELP_B = getWidget(10);
    public static final Resource WGT_LCD_BG = getWidget(11);

    public static final Resource WGT_ARR_L_N = new Resource(119, 104, 7, 10);
    public static final Resource WGT_ARR_L_D = new Resource(126, 104, 7, 10);
    public static final Resource WGT_ARR_R_N = new Resource(119, 114, 7, 10);
    public static final Resource WGT_ARR_R_D = new Resource(126, 114, 7, 10);
    public static final Resource WGT_SCROLL_N = new Resource(133, 104, 14, 10);
    public static final Resource WGT_SCROLL_F = new Resource(133, 114, 14, 10);
    public static final Resource WGT_HELP_F = new Resource(147, 104, 20, 20);
    public static final Resource WGT_HELP_N = new Resource(167, 104, 20, 20);
    public static final Resource WGT_LCD_UL_F = new Resource(187, 104, 4, 16);
    public static final Resource WGT_LCD_UR_F = new Resource(191, 104, 4, 16);
    public static final Resource WGT_LCD_LL_F = new Resource(195, 104, 4, 16);
    public static final Resource WGT_LCD_LR_F = new Resource(199, 104, 4, 16);
    public static final Resource WGT_LCD_H_F = new Resource(187, 120, 12, 4);
    public static final Resource WGT_LCD_DO_F = new Resource(199, 120, 4, 4);
    public static final Resource WGT_LCD_UL_N = new Resource(203, 104, 4, 16);
    public static final Resource WGT_LCD_UR_N = new Resource(207, 104, 4, 16);
    public static final Resource WGT_LCD_LL_N = new Resource(211, 104, 4, 16);
    public static final Resource WGT_LCD_LR_N = new Resource(215, 104, 4, 16);
    public static final Resource WGT_LCD_H_N = new Resource(203, 120, 12, 4);
    public static final Resource WGT_LCD_DO_N = new Resource(215, 120, 4, 4);
    public static final Resource WGT_LCD_P_F = new Resource(219, 104, 7, 7);
    public static final Resource WGT_LCD_M_F = new Resource(226, 104, 7, 7);
    public static final Resource WGT_LCD_T_F = new Resource(233, 104, 7, 7);
    public static final Resource WGT_LCD_D_F = new Resource(240, 104, 7, 7);
    public static final Resource WGT_LCD_P_N = new Resource(219, 111, 7, 7);
    public static final Resource WGT_LCD_M_N = new Resource(226, 111, 7, 7);
    public static final Resource WGT_LCD_T_N = new Resource(233, 111, 7, 7);
    public static final Resource WGT_LCD_D_N = new Resource(240, 111, 7, 7);

    public static final Resource ICN_RECENT_N = getIcon(0, 5);
    public static final Resource ICN_RECENT_F = getIcon(0, 4);
    public static final Resource ICN_INPUT_N = getIcon(1, 5);
    public static final Resource ICN_INPUT_F = getIcon(1, 4);
    public static final Resource ICN_OUTPUT_N = getIcon(2, 5);
    public static final Resource ICN_OUTPUT_F = getIcon(2, 4);
    public static final Resource ICN_CATALYST_N = getIcon(3, 5);
    public static final Resource ICN_CATALYST_F = getIcon(3, 4);
    public static final Resource ICN_LIST_N = getIcon(4, 5);
    public static final Resource ICN_LIST_F = getIcon(4, 4);
    public static final Resource ICN_LABEL_N = getIcon(5, 5);
    public static final Resource ICN_LABEL_F = getIcon(5, 4);
    public static final Resource ICN_HELP_N = getIcon(6, 5);
    public static final Resource ICN_HELP_F = getIcon(6, 4);
    public static final Resource ICN_STACK_N = getIcon(7, 5);
    public static final Resource ICN_STACK_F = getIcon(7, 4);
    public static final Resource ICN_TEXT_N = getIcon(8, 5);
    public static final Resource ICN_TEXT_F = getIcon(8, 4);
    public static final Resource ICN_MULTI_N = getIcon(9, 5);
    public static final Resource ICN_MULTI_F = getIcon(9, 4);
    public static final Resource LBL_FRAME = getLabel(0);
    public static final Resource LBL_FLUID = getLabel(1);
    public static final Resource LBL_UNIV_B = getLabel(2);
    public static final Resource LBL_UNIV_F = getLabel(3);
    public static final Resource LBL_FR_UL = getLabel(4);
    public static final Resource LBL_FR_UR = getLabel(5);
    public static final Resource LBL_FR_LL = getLabel(6);

    public static final ResourceGroup BTN_LABEL = new ResourceGroup(BTN_LABEL_N);
    public static final ResourceGroup BTN_NEW = new ResourceGroup(BTN_NEW_N);
    public static final ResourceGroup BTN_SEARCH = new ResourceGroup(BTN_SEARCH_N);
    public static final ResourceGroup BTN_SAVE = new ResourceGroup(BTN_SAVE_N, BTN_SAVE_D);
    public static final ResourceGroup BTN_COPY = new ResourceGroup(BTN_COPY_N, BTN_COPY_D);
    public static final ResourceGroup BTN_DEL = new ResourceGroup(BTN_DEL_N);
    public static final ResourceGroup BTN_YES = new ResourceGroup(BTN_YES_N, BTN_YES_D);
    public static final ResourceGroup BTN_NO = new ResourceGroup(BTN_NO_N, BTN_NO_D);
    public static final ResourceGroup BTN_DISAMB = new ResourceGroup(BTN_DISAMB_N, BTN_DISAMB_D);
    public static final ResourceGroup BTN_IN = new ResourceGroup(BTN_IN_N, BTN_IN_D);
    public static final ResourceGroup BTN_OUT = new ResourceGroup(BTN_OUT_N, BTN_OUT_D);
    public static final ResourceGroup BTN_CAT = new ResourceGroup(BTN_CAT_N, BTN_CAT_D);
    public static final ResourceGroup BTN_LIST = new ResourceGroup(BTN_LIST_N, BTN_LIST_D);
    public static final ResourceGroup BTN_IMPORT = new ResourceGroup(BTN_IMPORT_N, BTN_IMPORT_D);
    public static final ResourceGroup BTN_EXPORT_N = new ResourceGroup(BTN_EXPORT_N_N, BTN_EXPORT_N_D);
    public static final ResourceGroup BTN_EXPORT_1 = new ResourceGroup(BTN_EXPORT_1_N, BTN_EXPORT_1_D);
    public static final ResourceGroup BTN_INV_E = new ResourceGroup(BTN_INV_E_N);
    public static final ResourceGroup BTN_INV_D = new ResourceGroup(BTN_INV_D_N);
    public static final ResourceGroup BTN_META_N = new ResourceGroup(BTN_META_N_N, BTN_META_D);
    public static final ResourceGroup BTN_CAP_N = new ResourceGroup(BTN_CAP_N_N, BTN_CAP_D);
    public static final ResourceGroup BTN_NBT_N = new ResourceGroup(BTN_NBT_N_N, BTN_NBT_D);
    public static final ResourceGroup BTN_META_F = new ResourceGroup(BTN_META_F_N, BTN_META_D);
    public static final ResourceGroup BTN_CAP_F = new ResourceGroup(BTN_CAP_F_N, BTN_CAP_D);
    public static final ResourceGroup BTN_NBT_F = new ResourceGroup(BTN_NBT_F_N, BTN_NBT_D);
    public static final ResourceGroup BTN_EDIT = new ResourceGroup(BTN_EDIT_N, BTN_EDIT_D);
    public static final ResourceGroup BTN_PICK = new ResourceGroup(BTN_PICK_N);
    public static final ResourceGroup BTN_DELETE_N = new ResourceGroup(BTN_DELETE_N_N, BTN_DELETE_N_D);
    public static final ResourceGroup BTN_DELETE_1 = new ResourceGroup(BTN_DELETE_1_N, BTN_DELETE_1_D);
    public static final ResourceGroup BTN_DOWN_N = new ResourceGroup(BTN_DOWN_N_N);
    public static final ResourceGroup BTN_DOWN_A = new ResourceGroup(BTN_DOWN_A_N);
    public static final ResourceGroup BTN_UP_N = new ResourceGroup(BTN_UP_N_N);

    public static final ResourceGroup WGT_ARR_L = new ResourceGroup(WGT_ARR_L_N, WGT_ARR_L_D);
    public static final ResourceGroup WGT_ARR_R = new ResourceGroup(WGT_ARR_R_N, WGT_ARR_R_D);
    public static final ResourceGroup ICN_RECENT = new ResourceGroup(ICN_RECENT_N, ICN_RECENT_F);
    public static final ResourceGroup ICN_INPUT = new ResourceGroup(ICN_INPUT_N, ICN_INPUT_F);
    public static final ResourceGroup ICN_OUTPUT = new ResourceGroup(ICN_OUTPUT_N, ICN_OUTPUT_F);
    public static final ResourceGroup ICN_CATALYST = new ResourceGroup(ICN_CATALYST_N, ICN_CATALYST_F);
    public static final ResourceGroup ICN_LIST = new ResourceGroup(ICN_LIST_N, ICN_LIST_F);
    public static final ResourceGroup ICN_LABEL = new ResourceGroup(ICN_LABEL_N, ICN_LABEL_F);
    public static final ResourceGroup ICN_TEXT = new ResourceGroup(ICN_TEXT_N, ICN_TEXT_F);
    public static final ResourceGroup ICN_HELP = new ResourceGroup(ICN_HELP_N, ICN_HELP_F);

    public static final Resource WGT_DRAG_N = new Resource(0, 120, 16, 16);
    public static final Resource WGT_DRAG_F = new Resource(16, 120, 16, 16);
    public static final Resource WGT_DRAG_A = new Resource(32, 120, 16, 16);

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

    public Resource sub(int xPos, int yPos, int xSize, int ySize) {
        return new Resource(this.xPos + xPos, this.yPos + yPos, xSize, ySize);
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
        return new Resource(x * 16, 104, 16, 16);
    }

    private static Resource getWidget(int x) {
        return new Resource(x * 20, 84, 20, 20);
    }

    public static class ResourceGroup {
        public Resource one;
        public Resource two;

        public ResourceGroup(Resource one, Resource two) {
            this.one = one;
            this.two = two;
        }

        public ResourceGroup(Resource one) {
            this.one = one;
        }
    }
}
