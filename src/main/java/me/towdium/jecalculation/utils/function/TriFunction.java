package me.towdium.jecalculation.utils.function;

public interface TriFunction <T, U, V, R> {
    R apply(T t, U u, V v);
}

