package me.towdium.jecalculation.item;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.item.items.ItemCalculator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Items {
    public static final ArrayList<JecItem> items;

    public static final ItemCalculator itemCalculator = new ItemCalculator();

    static {
        items = new ArrayList<>();
        items.add(itemCalculator);
    }
}
