package pers.towdium.just_enough_calculation.util.wrappers;

import javax.annotation.Nullable;

/**
 * Author: Towdium
 * Date:   2016/7/1.
 */
public class Singleton<T> {
    public T value;

    public Singleton(T value) {
        this.value = value;
    }

    public T push(@Nullable T value) {
        T ret = this.value;
        if(value != null)
            this.value = value;
        return ret;
    }
}
