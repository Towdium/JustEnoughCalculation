package me.towdium.jecalculation.utils.wrappers;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Author: Towdium
 * Date:   2016/7/1.
 */
public class Wrapper<T> {
    public T value;
    public Predicate<T> predicate = t -> true;

    public Wrapper(T value) {
        this.value = value;
    }

    public Wrapper<T> push(@Nullable T value) {
        if (value != null && predicate.test(value))
            this.value = value;
        return this;
    }

    public Wrapper<T> ifPresent(Consumer<T> c) {
        if (value != null)
            c.accept(value);
        return this;
    }

    public Wrapper<T> ifPresentNot(Runnable r) {
        if (value == null)
            r.run();
        return this;
    }
}
