package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
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
public class LabelFluidStack implements ILabel {
    public static final String KEY_FLUID = "name";
    public static final String KEY_AMOUNT = "amount";

    FluidStack fluid;
    int amount;

    public LabelFluidStack(FluidStack fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public LabelFluidStack(Fluid fluid, int amount) {
        this(new FluidStack(fluid, 1000), amount);
    }

    public LabelFluidStack(NBTTagCompound nbt) {
        fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(KEY_FLUID));
        amount = nbt.getInteger(KEY_AMOUNT);
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
    public String getAmountString() {
        return amount >= 1000 ? Utilities.cutNumber(amount / 1000f, 4) + "B" : Integer.toString(amount) + "mB";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return fluid.getLocalizedName();
    }

    @Override
    public ILabel copy() {
        return new LabelFluidStack(fluid, amount);
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag(KEY_AMOUNT, fluid.writeToNBT(new NBTTagCompound()));
        ret.setInteger(KEY_AMOUNT, amount);
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
    public void drawLabel(JecGui gui) {
        gui.drawResource(Resource.LBL_FLUID, 0, 0);
        gui.drawFluid(fluid.getFluid(), 2, 2, 12, 12);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LabelFluidStack
               && fluid.equals(((LabelFluidStack) obj).fluid)
               && amount == ((LabelFluidStack) obj).amount;
    }
}
