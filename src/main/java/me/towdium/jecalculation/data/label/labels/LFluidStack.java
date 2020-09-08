package me.towdium.jecalculation.data.label.labels;

import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.ILabel.Serializer.SerializationException;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Author: towdium
 * Date:   17-9-27.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LFluidStack extends LStack<Fluid> {
    public static final String IDENTIFIER = "fluidStack";
    public static final String KEY_FLUID = "fluid";
    public static final String KEY_NBT = "nbt";

    Fluid fluid;
    CompoundNBT nbt;
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
        this(fs.getAmount(), fs.getFluid(), fs.getTag());
    }

    public LFluidStack(long amount, Fluid fluid) {
        this(amount, fluid, null);
    }

    public LFluidStack(long amount, Fluid fluid, @Nullable CompoundNBT nbt) {
        super(amount, false);
        init(fluid, nbt);
    }

    public LFluidStack(CompoundNBT nbt) {
        super(nbt);
        String id = nbt.getString(KEY_FLUID);
        Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(id));
        if (f == null) throw new SerializationException("Fluid " + id + " cannot be resolved, ignoring");
        init(f, nbt.contains(KEY_NBT) ? nbt.getCompound(KEY_NBT) : null);
    }

    @Override
    public Fluid get() {
        return fluid;
    }

    @Override
    public Context<Fluid> getContext() {
        return Context.FLUID;
    }

    private void init(Fluid fluid, @Nullable CompoundNBT nbt) {
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
        return format(amount);
    }

    public static String format(long amount) {
        return amount >= 1000 ? Utilities.cutNumber(amount / 1000f, 4) + "B"
                : amount + "mB";
    }

    @Override
    public String getDisplayName() {
        return temp.getDisplayName().getString(); //.getFormattedText();
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
        } else return false;
    }

    @Override
    public LFluidStack copy() {
        return new LFluidStack(this);
    }

    @Override
    public CompoundNBT toNbt() {
        CompoundNBT ret = super.toNbt();
        //noinspection ConstantConditions
        ret.putString(KEY_FLUID, ForgeRegistries.FLUIDS.getKey(fluid).toString());
        if (nbt != null) ret.put(KEY_NBT, nbt);
        return ret;
    }

    @Override
    public void getToolTip(List<String> existing, boolean detailed) {
        if (detailed) existing.add(FORMAT_GREY +
                Utilities.I18n.get("label.common.amount", Long.toString(getAmount())) + "mB");
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(fluid));
    }

    @Override
    public void drawLabel(MatrixStack matrixStack, JecaGui gui) {
        gui.drawResource(matrixStack, Resource.LBL_FLUID, 0, 0);
        gui.drawFluid(matrixStack, fluid, 2, 2, 12, 12);
    }

    @Override
    public int hashCode() {
        //noinspection ConstantConditions
        return super.hashCode() ^ fluid.getRegistryName().hashCode() ^ (nbt == null ? 0 : nbt.hashCode());
    }

    private static final String TIC_CLASS = "slimeknights.tconstruct.plugin.jei.casting.CastingRecipeCategory";

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable IRecipeLayout rl) {
        if (rl != null && rl.getRecipeCategory().getClass().getName().equals(TIC_CLASS) && iss.get(0) instanceof LFluidStack)
            return Collections.singletonList(iss.get(0).copy().multiply(0.5f));
        return new ArrayList<>();
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LFluidStack && b instanceof LFluidStack) return a.matches(b);
        else return false;
    }
}
