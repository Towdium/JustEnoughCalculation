package pers.towdium.justEnoughCalculation.util.function;

/**
 * Author: Towdium
 * Date:   2016/6/27.
 */
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
