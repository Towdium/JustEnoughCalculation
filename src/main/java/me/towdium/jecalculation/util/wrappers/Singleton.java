package me.towdium.jecalculation.util.wrappers;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Author: Towdium
 * Date:   2016/7/1.
 */
public class Singleton<T> {
    public T value;
    public Predicate<T> predicate = t -> true;

    public Singleton(T value) {
        this.value = value;
    }

    public Singleton<T> push(@Nullable T value) {
        if (value != null && predicate.test(value))
            this.value = value;
        return this;
    }
}
