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
        return one.hashCode() ^ two.hashCode();
    }

    public boolean swap(Class<? extends K> ke, Class<? extends V> ve) {
        if (ke.isInstance(one) && ve.isInstance(two)) return true;
        else if (!(ke.isInstance(two) && ve.isInstance(one))) return false;
        else {
            //noinspection unchecked
            K o = (K) two;
            //noinspection unchecked
            two = (V) one;
            one = o;
            return true;
        }
    }
}
