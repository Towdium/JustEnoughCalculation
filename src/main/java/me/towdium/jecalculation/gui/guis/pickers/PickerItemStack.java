package me.towdium.jecalculation.gui.guis.pickers;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.labels.LItemStack;
import me.towdium.jecalculation.gui.IWPicker;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.drawables.WButton;
import me.towdium.jecalculation.gui.drawables.WButtonIcon;
import me.towdium.jecalculation.gui.drawables.WLabel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: Towdium
 * Date: 18-9-18
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class PickerItemStack extends IWPicker.Impl {
    WLabel label = new WLabel(7, 7, 20, 20, WLabel.enumMode.SELECTOR).setLsnrUpdate(this::update);
    WButton bConfirm = new WButtonIcon(149, 7, 20, 20, Resource.BTN_YES).setLsnrLeft(() -> callback.accept(label.getLabel()));
    WButton bNbtF = new WButtonIcon(49, 7, 20, 20, Resource.BTN_NBT_F).setLsnrLeft(() -> setFNbt(false));
    WButton bBbtN = new WButtonIcon(49, 7, 20, 20, Resource.BTN_NBT_N).setLsnrLeft(() -> setFNbt(true));
    WButton bCapF = new WButtonIcon(68, 7, 20, 20, Resource.BTN_CAP_F).setLsnrLeft(() -> setFCap(false));
    WButton bCapN = new WButtonIcon(68, 7, 20, 20, Resource.BTN_CAP_N).setLsnrLeft(() -> setFCap(true));
    WButton bMetaF = new WButtonIcon(30, 7, 20, 20, Resource.BTN_META_F).setLsnrLeft(() -> setFMeta(false));
    WButton bMetaN = new WButtonIcon(30, 7, 20, 20, Resource.BTN_META_N).setLsnrLeft(() -> setFMeta(true));

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
