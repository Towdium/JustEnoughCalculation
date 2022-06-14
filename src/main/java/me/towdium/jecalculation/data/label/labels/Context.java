package me.towdium.jecalculation.data.label.labels;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.Collection;
import java.util.stream.Stream;

public interface Context<T extends IForgeRegistryEntry<T>> {
    LStack<T> create(T t);

    ITagManager<T> tags();

    LTag<T> create(TagKey<T> rl);

    LTag<T> create(TagKey<T> rl, long amount);

    default Collection<TagKey<T>> discover(LStack<T> s) {
        return tags().stream().filter(i -> i.contains(s.get())).map(ITag::getKey).toList();
    }

    default Stream<LStack<T>> discover(TagKey<T> tag) {
        ITag<T> records = tags().getTag(tag);
        return records == null ? Stream.empty() :
                records.stream().map(this::create);
    }

    default boolean matches(TagKey<?> tag, LStack<?> t) {
        try {
            ITag<T> records = tags().getTag((TagKey<T>) tag);
            //noinspection unchecked
            return records != null && t.getContext() == this && records.contains((T) t.get());
        }catch (ClassCastException e){
            return false;
        }
    }

    Context<Item> ITEM = new Context<>() {
        @Override
        public LStack<Item> create(Item item) {
            return new LItemStack(new ItemStack(item));
        }

        @Override
        public ITagManager<Item> tags() {
            return ForgeRegistries.ITEMS.tags();
        }

        @Override
        public LTag<Item> create(TagKey<Item> rl) {
            return new LItemTag(rl);
        }

        @Override
        public LTag<Item> create(TagKey<Item> rl, long amount) {
            return new LItemTag(rl, amount);
        }
    };

    Context<Fluid> FLUID = new Context<>() {
        @Override
        public LStack<Fluid> create(Fluid fluid) {
            return new LFluidStack(new FluidStack(fluid, 1000));
        }

        @Override
        public ITagManager<Fluid> tags() {
            return ForgeRegistries.FLUIDS.tags();
        }

        @Override
        public LTag<Fluid> create(TagKey<Fluid> rl) {
            return new LFluidTag(rl);
        }

        @Override
        public LTag<Fluid> create(TagKey<Fluid> rl, long amount) {
            return new LFluidTag(rl, amount);
        }
    };
}
