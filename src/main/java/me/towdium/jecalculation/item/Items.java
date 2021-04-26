package me.towdium.jecalculation.item;

import me.towdium.jecalculation.item.items.ItemCalculator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
public class Items {
    public static final ArrayList<JecaItem> items;
    public static final ItemCalculator itemCalculator = new ItemCalculator();

    static {
        items = new ArrayList<>();
        items.add(itemCalculator);
    }
}
