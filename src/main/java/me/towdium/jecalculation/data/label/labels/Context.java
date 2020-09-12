package me.towdium.jecalculation.data.label.labels;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;
import java.util.stream.Stream;

public interface Context<T> {
    LStack<T> create(T t);

    TagCollection<T> tags();

    LTag<T> create(ResourceLocation rl);

    LTag<T> create(ResourceLocation rl, long amount);

    default Collection<ResourceLocation> discover(LStack<T> s) {
        return tags().getOwningTags(s.get());
    }

    default Stream<LStack<T>> discover(ResourceLocation tag) {
        Tag<T> records = tags().get(tag);
        return records == null ? Stream.empty() :
                records.getAllElements().stream().map(this::create);
    }

    @SuppressWarnings("unchecked")
    default boolean matches(ResourceLocation tag, LStack<?> t) {
        Tag<T> records = tags().get(tag);
        return records != null && t.getContext() == this && records.contains((T) t.get());
    }

    Context<Item> ITEM = new Context<Item>() {
        @Override
        public LStack<Item> create(Item item) {
            return new LItemStack(new ItemStack(item));
        }

        @Override
        public TagCollection<Item> tags() {
            return ItemTags.getCollection();
        }

        @Override
        public LTag<Item> create(ResourceLocation rl) {
            return new LItemTag(rl);
        }

        @Override
        public LTag<Item> create(ResourceLocation rl, long amount) {
            return new LItemTag(rl, amount);
        }
    };

    Context<Fluid> FLUID = new Context<Fluid>() {
        @Override
        public LStack<Fluid> create(Fluid fluid) {
            return new LFluidStack(new FluidStack(fluid, 1000));
        }

        @Override
        public TagCollection<Fluid> tags() {
            return FluidTags.getCollection();
        }

        @Override
        public LTag<Fluid> create(ResourceLocation rl) {
            return new LFluidTag(rl);
        }

        @Override
        public LTag<Fluid> create(ResourceLocation rl, long amount) {
            return new LFluidTag(rl, amount);
        }
    };
}
