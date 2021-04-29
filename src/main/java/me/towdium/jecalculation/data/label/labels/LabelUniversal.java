package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
public class LabelUniversal extends LabelSimpleAmount {
    public static final String KEY_NAME = "name";

    String name;

    public LabelUniversal(String name, int amount) {
        super(amount);
        this.name = name;
    }

    public LabelUniversal(LabelUniversal lu) {
        super(lu);
        name = lu.name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return Utilities.L18n.format("label.universal.name", name);
    }

    @Override
    public ILabel copy() {
        return new LabelUniversal(this);
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound nbt = super.toNBTTagCompound();
        nbt.setString(KEY_NAME, name);
        return nbt;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecGui gui) {
        gui.drawResource(Resource.LBL_UNIV_B, 0, 0);
        gui.drawResource(Resource.LBL_UNIV_F, 0, 0, (name.hashCode() * 0x131723) & 0xFFFFFF);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LabelUniversal
               && name.equals(((LabelUniversal) obj).name) && amount == ((LabelUniversal) obj).amount;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ amount;
    }
}
