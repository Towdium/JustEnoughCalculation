package me.towdium.jecalculation.utils.wrappers;

/**
 * Author: Towdium
 * Date:   2016/7/1.
 */
public class Pair<K, V> {
    public K one;
    public V two;

    public Pair(K one, V two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public int hashCode() {
        return one.hashCode() + two.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair<?, ?>) {
            Pair<?, ?> p = (Pair<?, ?>) obj;
            return one.equals(p.one) && two.equals(p.two);
        }
        return false;
    }

    public Pair<K, V> setOne(K one) {
        this.one = one;
        return this;
    }

    public Pair<K, V> setTwo(V two) {
        this.two = two;
        return this;
    }
}
