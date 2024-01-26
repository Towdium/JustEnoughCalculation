package me.towdium.jecalculation.data.label.labels;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Context<T> {
    LStack<T> create(T t);

    Stream<Pair<TagKey<T>, Stream<T>>> tags();

    Registry<T> registry();

    LTag<T> create(TagKey<T> rl);

    LTag<T> create(TagKey<T> rl, long amount);

    default Collection<TagKey<T>> discover(LStack<T> s) {
        return tags()
                .filter(pair -> pair.getTwo().anyMatch(t -> Objects.equals(t, s.get())))
                .map(Pair::getOne)
                .toList();
    }

    default Stream<LStack<T>> discover(TagKey<T> tag) {
        return tags()
                .filter(pair -> Utilities.equals(pair.getOne(), tag))
                .flatMap(Pair::getTwo)
                .map(this::create);
    }

    default boolean matches(TagKey<?> tag, LStack<?> s) {
        if (s.getContext() != this)
            return false;

        Optional<HolderSet.Named<T>> tagEntry = registry().getTag((TagKey<T>) tag);
        if (tagEntry.isEmpty())
            return false;

        return tagEntry.get().stream()
            .map(Holder::value)
            .anyMatch(t -> t.equals(s.get()));
    }

    Context<Item> ITEM = new Context<>() {
        @Override
        public LStack<Item> create(Item item) {
            return new LItemStack(new ItemStack(item));
        }

        @Override
        public Registry<Item> registry() {
            return Registry.ITEM;
        }

        @Override
        public Stream<Pair<TagKey<Item>, Stream<Item>>> tags() {
            return Utilities.getTags(registry());
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
            return new LFluidStack(FluidStack.create(fluid, 1000));
        }

        @Override
        public Registry<Fluid> registry() {
            return Registry.FLUID;
        }

        @Override
        public Stream<Pair<TagKey<Fluid>, Stream<Fluid>>> tags() {
            return Utilities.getTags(registry());
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
