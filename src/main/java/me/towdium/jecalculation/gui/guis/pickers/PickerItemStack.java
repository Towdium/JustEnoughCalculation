package me.towdium.jecalculation.gui.guis.pickers;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.gui.guis.IGui;
import me.towdium.jecalculation.gui.widgets.WButton;
import me.towdium.jecalculation.gui.widgets.WButtonIcon;
import me.towdium.jecalculation.gui.widgets.WLabel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: Towdium
 * Date: 18-9-18
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class PickerItemStack extends IPicker.Impl implements IGui {
    WLabel label = new WLabel(7, 7, 20, 20, WLabel.Mode.SELECTOR).setListener((i, v) -> update());
    WButton bConfirm = new WButtonIcon(149, 7, 20, 20, BTN_YES).setListener(i -> callback.accept(label.getLabel()));
    WButton bBbtN = new WButtonIcon(49, 7, 20, 20, BTN_NBT_N).setListener(i -> setFNbt(true));
    WButton bNbtF = new WButtonIcon(49, 7, 20, 20, BTN_NBT_F).setListener(i -> setFNbt(false));
    WButton bCapN = new WButtonIcon(68, 7, 20, 20, BTN_CAP_N).setListener(i -> setFCap(true));
    WButton bCapF = new WButtonIcon(68, 7, 20, 20, BTN_CAP_F).setListener(i -> setFCap(false));
    WButton bMetaN = new WButtonIcon(30, 7, 20, 20, BTN_META_N).setListener(i -> setFMeta(true));
    WButton bMetaF = new WButtonIcon(30, 7, 20, 20, BTN_META_F).setListener(i -> setFMeta(false));

    public PickerItemStack() {
        addAll(label, bConfirm);
        add(bConfirm);
        update();
    }

    public void update() {
        setFCap(false);
        setFMeta(false);
        setFNbt(false);
        boolean b = label.getLabel() == ILabel.EMPTY;
        bBbtN.setDisabled(b);
        bCapN.setDisabled(b);
        bMetaN.setDisabled(b);
        bConfirm.setDisabled(b);
    }

    private void setFNbt(boolean b) {
        setF(b, bBbtN, bNbtF, i -> i.setFNbt(b));
    }

    private void setFMeta(boolean b) {
        setF(b, bMetaN, bMetaF, i -> i.setFMeta(b));
    }

    private void setFCap(boolean b) {
        setF(b, bCapN, bCapF, i -> i.setFCap(b));
    }

    private void setF(boolean b, WButton be, WButton bd, Consumer<LItemStack> c) {
        if (b) {
            remove(be);
            add(bd);
        } else {
            remove(bd);
            add(be);
        }
        ILabel l = label.getLabel();
        if (l instanceof LItemStack) c.accept((LItemStack) l);
    }
}
