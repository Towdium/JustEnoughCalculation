package me.towdium.jecalculation.gui.guis.pickers;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.WButton;
import me.towdium.jecalculation.gui.widgets.WButtonIcon;
import me.towdium.jecalculation.gui.widgets.WLabel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: Towdium
 * Date: 18-9-18
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class PickerItemStack extends IPicker.Impl implements IGui {
    WLabel label = new WLabel(7, 7, 20, 20, WLabel.Mode.SELECTOR).setListener((i, v) -> update(v));
    WButton bConfirm = new WButtonIcon(149, 7, 20, 20, BTN_YES, "item_stack.confirm").setListener(i -> callback.accept(label.getLabel()));
    WButton bNbtN = new WButtonIcon(49, 7, 20, 20, BTN_NBT_N, "item_stack.nbt_normal").setListener(i -> setFNbt(true));
    WButton bNbtF = new WButtonIcon(49, 7, 20, 20, BTN_NBT_F, "item_stack.nbt_fuzzy").setListener(i -> setFNbt(false));
    WButton bCapN = new WButtonIcon(68, 7, 20, 20, BTN_CAP_N, "item_stack.capability_normal").setListener(i -> setFCap(true));
    WButton bCapF = new WButtonIcon(68, 7, 20, 20, BTN_CAP_F, "item_stack.capability_fuzzy").setListener(i -> setFCap(false));
    WButton bMetaN = new WButtonIcon(30, 7, 20, 20, BTN_META_N, "item_stack.meta_normal").setListener(i -> setFMeta(true));
    WButton bMetaF = new WButtonIcon(30, 7, 20, 20, BTN_META_F, "item_stack.meta_fuzzy").setListener(i -> setFMeta(false));
    ILabel raw = ILabel.EMPTY;
    boolean fMeta, fNbt, fCap = false;

    public PickerItemStack() {
        add(label, bConfirm);
        update(ILabel.EMPTY);
    }

    public void update(ILabel l) {
        raw = l;
        setFCap(false);
        setFMeta(false);
        setFNbt(false);
        boolean b = l == ILabel.EMPTY;
        bNbtN.setDisabled(b);
        bCapN.setDisabled(b);
        bMetaN.setDisabled(b);
        bConfirm.setDisabled(b);
    }

    private void setFNbt(boolean b) {
        setF(b, bNbtN, bNbtF, () -> fNbt = b);
    }

    private void setFMeta(boolean b) {
        setF(b, bMetaN, bMetaF, () -> fMeta = b);
    }

    private void setFCap(boolean b) {
        setF(b, bCapN, bCapF, () -> fCap = b);
    }

    private void setF(boolean b, WButton be, WButton bd, Runnable r) {
        remove(be, bd);
        if (b) add(bd);
        else add(be);
        r.run();
        if (raw instanceof LItemStack) {
            LItemStack lis = (LItemStack) raw;
            label.setLabel(lis.copy().setFCap(fCap).setFMeta(fMeta).setFNbt(fNbt));
        }
    }
}
