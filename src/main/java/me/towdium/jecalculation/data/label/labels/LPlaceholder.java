package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
public class LPlaceholder extends ILabel.Impl {
    public static final String KEY_NAME = "name";
    public static final String IDENTIFIER = "string";
    static Utilities.Recent<LPlaceholder> recentClient = new Utilities.Recent<>(100);
    static Utilities.Recent<LPlaceholder> recentServer = new Utilities.Recent<>(100);

    String name;

    public LPlaceholder(NBTTagCompound tag) {
        this(tag.getString(KEY_NAME), tag.getInteger(KEY_AMOUNT));
    }

    public LPlaceholder(String name, int amount) {
        this(name, amount, false);
    }

    public LPlaceholder(String name, int amount, boolean silent) {
        super(amount);
        this.name = name;
        if (!silent) getRecord().push(new LPlaceholder(name, 1, true));
    }

    public LPlaceholder(LPlaceholder label) {
        super(label);
        name = label.name;
    }

    public static void onLogOut() {
        recentServer.clear();
    }

    @Override
    public String getIdentifier() {
        return "string";
    }

    public static List<ILabel> getRecent() {
        return getRecord().toList().stream().map(LPlaceholder::copy).collect(Collectors.toList());
    }

    private static Utilities.Recent<LPlaceholder> getRecord() {
        return JustEnoughCalculation.side == JustEnoughCalculation.enumSide.CLIENT
                ? recentClient : recentServer;
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
    public int hashCode() {
        return name.hashCode() ^ amount;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.format("label.placeholder.name", name);
    }

    @Override
    public boolean matches(Object l) {
        return l instanceof LPlaceholder && name.equals(((LPlaceholder) l).name);
    }

    @Override
    public ILabel copy() {
        return new LPlaceholder(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.Reference.MODNAME);
    }
}
