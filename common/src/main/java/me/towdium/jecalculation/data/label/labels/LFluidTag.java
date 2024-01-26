package me.towdium.jecalculation.data.label.labels;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.utils.Utilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LFluidTag extends LTag<Fluid> {
    public static final String IDENTIFIER = "fluidTag";

    public LFluidTag(TagKey<Fluid> name) {
        super(name);
    }

    public LFluidTag(TagKey<Fluid> name, long amount) {
        super(name, amount);
    }

    public LFluidTag(LTag<Fluid> lt) {
        super(lt);
    }

    public LFluidTag(CompoundTag nbt) {
        super(nbt);
    }

    @Override
    protected Registry<Fluid> getRegistry() {
        return BuiltInRegistries.FLUID;
    }

    @Override
    protected void drawLabel(int xPos, int yPos, JecaGui gui, boolean hand) {
        Object o = getRepresentation();
        gui.drawResource(Resource.LBL_FLUID, xPos, yPos);
        if (o instanceof FluidStack) gui.drawFluid(((FluidStack) o).getFluid(), xPos + 2, yPos + 2, 12, 12);
        gui.drawResource(Resource.LBL_FRAME, xPos, yPos);
    }

    @Override
    public String getAmountString(boolean round) {
        return LFluidStack.format(amount);
    }

    @Override
    public LTag<Fluid> copy() {
        return new LFluidTag(this);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Context<Fluid> getContext() {
        return Context.FLUID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.get("label.fluid_tag.name", name.location());
    }
}
