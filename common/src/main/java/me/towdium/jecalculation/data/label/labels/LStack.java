package me.towdium.jecalculation.data.label.labels;


import net.minecraft.nbt.CompoundTag;

public abstract class LStack<T> extends LContext<T> {
    public LStack(long amount, boolean percent) {
        super(amount, percent);
    }

    public LStack(Impl lsa) {
        super(lsa);
    }

    public LStack(CompoundTag nbt) {
        super(nbt);
    }

    public abstract T get();

    @Override
    public abstract Object getRepresentation();
}
