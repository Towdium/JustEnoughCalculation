package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
public class LString extends LabelSimpleAmount {
    public static final String KEY_NAME = "name";
    public static final String IDENTIFIER = "string";

    String name;

    public LString(NBTTagCompound tag) {
        super(tag);
        this.name = tag.getString(KEY_NAME);
    }

    public LString(String name, int amount) {
        super(amount);
        this.name = name;
    }

    public LString(LString label) {
        super(label);
        name = label.name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.format("label.universal.name", name);
    }

    @Override
    public String getIdentifier() {
        return "string";
    }

    @Override
    public ILabel copy() {
        return new LString(this);
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound nbt = super.toNBTTagCompound();
        nbt.setString(KEY_NAME, name);
        return nbt;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawResource(Resource.LBL_UNIV_B, 0, 0);
        gui.drawResource(Resource.LBL_UNIV_F, 0, 0, (name.hashCode() * 0x131723) & 0xFFFFFF);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LString
               && name.equals(((LString) obj).name) && amount == ((LString) obj).amount;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ amount;
    }
}
