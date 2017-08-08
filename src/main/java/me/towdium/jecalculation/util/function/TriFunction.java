package me.towdium.jecalculation.util.function;

/**
 * Author: Towdium
 * Date:   2016/6/27.
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
