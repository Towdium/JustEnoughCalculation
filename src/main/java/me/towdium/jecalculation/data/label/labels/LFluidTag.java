package me.towdium.jecalculation.data.label.labels;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LFluidTag extends LTag<Fluid> {
    public static final String IDENTIFIER = "fluidTag";

    public LFluidTag(ResourceLocation name) {
        super(name);
    }

    public LFluidTag(ResourceLocation name, long amount) {
        super(name, amount);
    }

    public LFluidTag(LTag<Fluid> lt) {
        super(lt);
    }

    public LFluidTag(CompoundNBT nbt) {
        super(nbt);
    }

    @Override
    protected void drawLabel(JecaGui gui) {
        Object o = getRepresentation();
        gui.drawResource(Resource.LBL_FLUID, 0, 0);
        if (o instanceof FluidStack) gui.drawFluid(((FluidStack) o).getFluid(), 2, 2, 12, 12);
        gui.drawResource(Resource.LBL_FRAME, 0, 0);
    }

    @Override
    public LTag copy() {
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
}
