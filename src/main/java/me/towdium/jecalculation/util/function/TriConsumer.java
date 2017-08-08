package me.towdium.jecalculation.util.function;

/**
 * Author: Towdium
 * Date:   2016/6/27.
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
