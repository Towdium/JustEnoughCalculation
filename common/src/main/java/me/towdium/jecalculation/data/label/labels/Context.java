package me.towdium.jecalculation.data.label.labels;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public interface Context<T> {
    LStack<T> create(T t);

    Stream<Pair<TagKey<T>, Stream<T>>> tags();

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
        return s.getContext() == this && tags()
                .filter(pair -> Utilities.equals(pair.getOne(), tag))
                .flatMap(Pair::getTwo)
                .anyMatch(t -> t.equals(s.get()));

    }

    Context<Item> ITEM = new Context<>() {
        @Override
        public LStack<Item> create(Item item) {
            return new LItemStack(new ItemStack(item));
        }

        @Override
        public Stream<Pair<TagKey<Item>, Stream<Item>>> tags() {
            return Utilities.getTags(Registry.ITEM);
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
        public Stream<Pair<TagKey<Fluid>, Stream<Fluid>>> tags() {
            return Utilities.getTags(Registry.FLUID);
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
