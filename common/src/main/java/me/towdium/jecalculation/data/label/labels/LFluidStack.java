package me.towdium.jecalculation.data.label.labels;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.platform.Platform;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.label.ILabel.Serializer.SerializationException;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

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
    CompoundTag nbt;
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

    public LFluidStack(long amount, Fluid fluid, @Nullable CompoundTag nbt) {
        super(amount, false);
        init(fluid, nbt);
    }

    public LFluidStack(CompoundTag nbt) {
        super(nbt);
        String id = nbt.getString(KEY_FLUID);
        Optional<Fluid> f = BuiltInRegistries.FLUID.getOptional(new ResourceLocation(id));
        if (f.isEmpty()) throw new SerializationException("Fluid " + id + " cannot be resolved, ignoring");
        init(f.get(), nbt.contains(KEY_NBT) ? nbt.getCompound(KEY_NBT) : null);
    }

    @Override
    public Fluid get() {
        return fluid;
    }

    @Override
    public Context<Fluid> getContext() {
        return Context.FLUID;
    }

    private void init(Fluid fluid, @Nullable CompoundTag nbt) {
        this.fluid = fluid;
        this.nbt = nbt;
        temp = FluidStack.create(fluid, 1, nbt);
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
        float bucket = FluidStackHooks.bucketAmount();
        return amount >= bucket ? Utilities.cutNumber(amount / bucket, 4) + "B"
                : amount + (Platform.isForgeLike() ? "mB" : "U");
    }

    @Override
    public String getDisplayName() {
        return temp.getName().getString();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        if (l instanceof LFluidStack lfs) {
            return (Objects.equals(nbt, lfs.nbt)) && fluid == lfs.fluid;
        } else return false;
    }

    @Override
    public LFluidStack copy() {
        return new LFluidStack(this);
    }

    @Override
    public CompoundTag toNbt() {
        CompoundTag ret = super.toNbt();
        //noinspection ConstantConditions
        ret.putString(KEY_FLUID, BuiltInRegistries.FLUID.getKey(fluid).toString());
        if (nbt != null) ret.put(KEY_NBT, nbt);
        return ret;
    }

    @Override
    public void getToolTip(List<String> existing, boolean detailed) {
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + Utilities.getModName(fluid));
    }

    @Override
    public void drawLabel(int xPos, int yPos, JecaGui gui, boolean hand) {
        gui.drawResource(Resource.LBL_FLUID, xPos, yPos);
        gui.drawFluid(fluid, xPos + 2, yPos + 2, 12, 12);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ BuiltInRegistries.FLUID.getKey(fluid).hashCode() ^ (nbt == null ? 0 : nbt.hashCode());
    }

    private static final String TIC_CLASS = "slimeknights.tconstruct.plugin.jei.casting.CastingRecipeCategory";

    public static List<ILabel> suggest(List<ILabel> iss, @Nullable Class<?> context) {
        if (context != null && context.getName().equals(TIC_CLASS) && iss.get(0) instanceof LFluidStack)
            return Collections.singletonList(iss.get(0).copy().multiply(0.5f));
        return new ArrayList<>();
    }

    public static boolean merge(ILabel a, ILabel b) {
        if (a instanceof LFluidStack && b instanceof LFluidStack) return a.matches(b);
        else return false;
    }
}
