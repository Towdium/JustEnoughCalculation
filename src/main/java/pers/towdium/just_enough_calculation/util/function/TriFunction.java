package pers.towdium.just_enough_calculation.util.function;

/**
 * Author: Towdium
 * Date:   2016/6/27.
 */
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
