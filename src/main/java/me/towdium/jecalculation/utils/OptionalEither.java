package me.towdium.jecalculation.utils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@ParametersAreNonnullByDefault
final public class OptionalEither<L, R> {
    public static <L, R> OptionalEither<L, R> left(L value) {
        return new OptionalEither<>(Optional.of(value), Optional.empty());
    }

    public static <L, R> OptionalEither<L, R> right(R value) {
        return new OptionalEither<>(Optional.empty(), Optional.of(value));
    }

    public static <L, R> OptionalEither<L, R> empty() {
        return new OptionalEither<>(Optional.empty(), Optional.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<L> left;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<R> right;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private OptionalEither(Optional<L> l, Optional<R> r) {
        left = l;
        right = r;
    }

    public <T> Optional<T> map(Function<? super L, ? extends T> lFunc, Function<? super R, ? extends T> rFunc) {
        if(left.isPresent()){
            return left.map(lFunc);
        } else if(right.isPresent()){
            return right.map(rFunc);
        } else {
            return Optional.empty();
        }
    }

    public <T> OptionalEither<T, R> mapLeft(Function<? super L, ? extends T> lFunc) {
        return new OptionalEither<>(left.map(lFunc), right);
    }

    public <T> OptionalEither<L, T> mapRight(Function<? super R, ? extends T> rFunc) {
        return new OptionalEither<>(left, right.map(rFunc));
    }

    public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
        left.ifPresent(lFunc);
        right.ifPresent(rFunc);
    }

    public boolean isPresent() {
        return left.isPresent() || right.isPresent();
    }
}
