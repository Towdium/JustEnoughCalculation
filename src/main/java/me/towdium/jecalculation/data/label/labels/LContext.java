package me.towdium.jecalculation.data.label.labels;

import me.towdium.jecalculation.data.label.ILabel;
import net.minecraft.nbt.CompoundNBT;

public abstract class LContext<T> extends ILabel.Impl {
    public LContext(long amount, boolean percent) {
        super(amount, percent);
    }

    public LContext(Impl lsa) {
        super(lsa);
    }

    public LContext(CompoundNBT nbt) {
        super(nbt);
    }

    public abstract Context<T> getContext();
}
