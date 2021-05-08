package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   17-9-27.
 */
@ParametersAreNonnullByDefault
public class LFluidStack extends ILabel.Impl {
    public static final String IDENTIFIER = "fluidStack";
    public static final String KEY_FLUID = "fluid";
    public static final String KEY_NBT = "nbt";

    Fluid fluid;
    NBTTagCompound nbt;
    FluidStack temp;

    @Override
    public FluidStack getRepresentation() {
        return temp;
    }

    @Override
    public boolean acceptPercent() {
        return false;
    }

    public LFluidStack(FluidStack fs) {
        this(fs.amount, fs.getFluid(), fs.tag);
    }

    public LFluidStack(long amount, Fluid fluid) {
        this(amount, fluid, null);
    }

    public LFluidStack(long amount, Fluid fluid, @Nullable NBTTagCompound nbt) {
        super(amount, false);
        init(fluid, nbt);
    }

    public LFluidStack(NBTTagCompound nbt) {
        super(nbt);
        String id = nbt.getString(KEY_FLUID);
        Fluid f = FluidRegistry.getFluid(id);
        if (f == null)
            throw new Serializer.SerializationException("Fluid " + id + " cannot be resolved, ignoring");
        init(f, nbt.hasKey(KEY_NBT) ? nbt.getCompoundTag(KEY_NBT) : null);
    }

    private void init(Fluid fluid, @Nullable NBTTagCompound nbt) {
        this.fluid = fluid;
        this.nbt = nbt;
        temp = new FluidStack(fluid, 1, nbt);
    }

    public LFluidStack(LFluidStack lfs) {
        super(lfs);
        fluid = lfs.fluid;
        nbt = lfs.nbt;
        temp = lfs.temp;
    }

    @Override
    protected int getMultiplier() {
        return 100;
    }

    @Override
    public String getAmountString(boolean round) {
        return amount >= 1000 ? Utilities.cutNumber(amount / 1000f, 4) + "B" : amount + "mB";
    }


    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return temp.getLocalizedName();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        if (l instanceof LFluidStack) {
            LFluidStack lfs = (LFluidStack) l;
            return (Objects.equals(nbt, lfs.nbt)) && fluid == lfs.fluid;
        } else
            return false;
    }

    @Override
    public LFluidStack copy() {
        return new LFluidStack(this);
    }

    @Override
    public NBTTagCompound toNbt() {
        NBTTagCompound ret = super.toNbt();
        ret.setString(KEY_FLUID, FluidRegistry.getFluidName(fluid));
        if (nbt != null)
            ret.setTag(KEY_NBT, nbt);
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getToolTip(List<String> existing, boolean detailed) {
        if (detailed)
            existing.add(FORMAT_GREY + Utilities.I18n.get("label.common.amount", Long.toString(getAmount())) + "mB");
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(fluid));
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawResource(Resource.LBL_FLUID, 0, 0);
        gui.drawFluid(fluid, 2, 2, 12, 12);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ fluid.getUnlocalizedName().hashCode() ^ (nbt == null ? 0 : nbt.hashCode());
    }

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable Class<?> context) {
        return new ArrayList<>();
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LFluidStack && b instanceof LFluidStack)
            return a.matches(b);
        else
            return false;
    }
}
