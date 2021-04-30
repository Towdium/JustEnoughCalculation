package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Author: towdium
 * Date:   17-9-27.
 */
@ParametersAreNonnullByDefault
public class LFluidStack implements ILabel {
    public static final String IDENTIFIER = "fluidStack";
    public static final String KEY_FLUID = "name";

    FluidStack fluid;

    public LFluidStack(FluidStack fluid) {
        this.fluid = fluid;
    }

    public LFluidStack(Fluid fluid, int amount) {
        this.fluid = new FluidStack(fluid, amount);
    }

    public LFluidStack(NBTTagCompound nbt) {
        fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(KEY_FLUID));
    }

    @Override
    public ILabel increaseAmount() {
        return ILabel.EMPTY;
    }

    @Override
    public ILabel increaseAmountLarge() {
        return ILabel.EMPTY;
    }

    @Override
    public ILabel decreaseAmount() {
        return ILabel.EMPTY;
    }

    @Override
    public ILabel decreaseAmountLarge() {
        return ILabel.EMPTY;
    }

    @Override
    public ILabel invertAmount() {
        fluid.amount *= -1;
        return this;
    }

    @Override
    public String getAmountString() {
        return fluid.amount >= 1000 ? Utilities.cutNumber(fluid.amount / 1000f, 4) + "B"
                                    : Integer.toString(fluid.amount) + "mB";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return fluid.getLocalizedName();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public ILabel copy() {
        return new LFluidStack(fluid);
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag(KEY_FLUID, fluid.writeToNBT(new NBTTagCompound())); // TODO check
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(fluid));
        return existing;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        gui.drawResource(Resource.LBL_FLUID, 0, 0);
        gui.drawFluid(fluid.getFluid(), 2, 2, 12, 12);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LFluidStack && fluid.equals(((LFluidStack) obj).fluid);
    }

    @Override
    public int hashCode() {
        return fluid.hashCode();
    }
}
