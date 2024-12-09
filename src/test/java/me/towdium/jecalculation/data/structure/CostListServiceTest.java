package me.towdium.jecalculation.data.structure;

import static me.towdium.jecalculation.data.structure.TestLbl.lbl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CostListServiceTest extends AbstractCostListServiceTest {

    @Test
    void oneCobblestone() {
        request(lbl("cobblestone"));
        assertInputs(lbl("cobblestone"));
        assertExcessOutputs();
        assertCatalysts();
        assertSteps();
    }

    @Test
    public void threeStone() {
        recipe(lst(lbl("stone")), lst(lbl("furnace")), lst(lbl("cobblestone")));
        inventory(lbl("stone"));
        request(lbl("stone", 3));
        assertInputs(lbl("cobblestone", 2));
        assertExcessOutputs();
        assertCatalysts(lbl("furnace"));
        assertSteps(lbl("stone", 2));
    }

    @Test
    void tntPure() {
        recipe(Arrays.asList(lbl("tnt")), Arrays.asList(lbl("crafting-table")), Arrays.asList(lbl("gunpowder", 5), lbl("sand", 4)));
        request(lbl("tnt"));
        assertInputs(lbl("gunpowder", 5), lbl("sand", 4));
        assertExcessOutputs(Collections.emptyList());
        assertCatalysts(lbl("crafting-table"));
        assertSteps(lbl("tnt"));
    }

    @Test
    void tntPartialInventory() {
        recipe(Arrays.asList(lbl("tnt")), Arrays.asList(lbl("crafting-table")), Arrays.asList(lbl("gunpowder", 5), lbl("sand", 4)));
        inventory(lbl("sand"));
        request(lbl("tnt"));

        // The reason the sand is listed before the gunpowder, instead of the order it is in the recipe, is because adding
        // 3 sand to your inventory when you've already got one won't increase the size of your inventory, while
        // adding 5 gunpowder when you have none actually will.
        assertInputs(lbl("sand", 3), lbl("gunpowder", 5));

        assertExcessOutputs(Collections.emptyList());
        assertCatalysts(lbl("crafting-table"));
        assertSteps(lbl("tnt"));
    }

    @Test
    void minimalInventory1() {
        // This request requires 14 iron blocks and 1000 stone.
        // We should make the iron blocks before making the stone, to minimize the amount of inventory space we take up.
        recipe(lst(lbl("iron-block")), WORKBENCH, lst(lbl("iron-ingot", 9)));
        recipe(lst(lbl("stone")), lst(lbl("furnace")), lst(lbl("cobblestone")));
        recipe(lst(lbl("mega-block")), lst(), lst(lbl("stone", 1000), lbl("iron-block", 14)));
        request(lbl("mega-block"));

        assertInputs(lbl("cobblestone", 1000), lbl("iron-ingot", 126));
        assertExcessOutputs();
        assertCatalysts(lbl("furnace"), lbl("crafting-table"));
        assertSteps(lbl("iron-block", 14), lbl("stone", 1000), lbl("mega-block"));
    }

    @Test
    void minimalInventory2() {
        // Identical to minimalInventory1, except that the mega-block recipe requests iron-blocks before stone. The
        // Calculator should choose to make stone before iron-blocks regardless
        // of which order they are specified in the recipe.
        recipe(lst(lbl("iron-block")), WORKBENCH, lst(lbl("iron-ingot", 9)));
        recipe(lst(lbl("stone")), lst(lbl("furnace")), lst(lbl("cobblestone")));
        recipe(lst(lbl("mega-block")), lst(), lst(lbl("iron-block", 14), lbl("stone", 1000)));
        request(lbl("mega-block"));

        assertInputs(lbl("iron-ingot", 126), lbl("cobblestone", 1000));
        assertExcessOutputs();
        assertCatalysts(lbl("crafting-table"), lbl("furnace"));
        assertSteps(lbl("iron-block", 14), lbl("stone", 1000), lbl("mega-block"));
    }

    @Test
    void basicLoop() {
        recipe(lst(lbl("stone")), lst(lbl("furnace")), lst(lbl("cobblestone")));
        recipe(lst(lbl("cobblestone")), lst(lbl("hammer")), lst(lbl("stone")));
        request(lbl("cobblestone", 1));

        assertExcessOutputs();
        assert calculation.getSteps().size() <= 2000;
    }

    @Test
    void infiniteLoop() {
        recipe(lst(lbl("stone")), lst(lbl("furnace")), lst(lbl("cobblestone")));
        recipe(lst(lbl("cobblestone", 100)), lst(lbl("hammer")), lst(lbl("stone", 101)));
        request(lbl("cobblestone", 1));

        assertCatalysts(lbl("hammer"), lbl("furnace"));
        assert calculation.getSteps().size() <= 2000;
    }

    @Test
    void surplus() {
        recipe(lst(lbl("motor")), WORKBENCH, lst(lbl("iron-rod", 2), lbl("magnetic-iron-rod")));
        recipe(lst(lbl("iron-rod", 64), lbl("iron-dust", 128)), lst(lbl("lathe")), lst(lbl("iron-ingot", 64)));
        recipe(lst(lbl("magnetic-iron-rod", 64)), lst(lbl("magnetizer")), lst(lbl("iron-rod", 64)));

        request(lbl("motor"));

        assertInputs(lbl("iron-ingot", 128));
        assertExcessOutputs(lbl("iron-rod", 62), lbl("magnetic-iron-rod", 63), lbl("iron-dust", 256));
        assertCatalysts(lbl("crafting-table"), lbl("lathe"), lbl("magnetizer"));
        assertSteps(lbl("iron-rod", 128), lbl("magnetic-iron-rod", 64), lbl("motor"));
    }

    // The next two tests demonstrate the problem with trying to merge repeated crafting steps in a naive
    // post-processing step. If the steps are merged in one direction, one of the tests fails. If the steps are merged
    // in the other direction, the other test fails.

    @Test
    void wiresAndCables1() {
        recipe(lst(lbl("superWireAndCable")), WORKBENCH, lst(lbl("cable"), lbl("wire")));
        recipe(lst(lbl("wire", 2)), WORKBENCH, lst(lbl("tin-ingot")));
        recipe(lst(lbl("cable")), WORKBENCH, lst(lbl("wire")));

        request(lbl("superWireAndCable"));

        assertInputs(lbl("tin-ingot", 1));
        assertExcessOutputs();
        assertCatalysts(WORKBENCH);
        assertSteps(lbl("wire", 2), lbl("cable"), lbl("superWireAndCable"));
    }

    @Test
    void wiresAndCables2() {
        recipe(lst(lbl("tv")), WORKBENCH, lst(lbl("cable"), lbl("antenna")));
        recipe(lst(lbl("cable")), WORKBENCH, lst(lbl("wire")));
        recipe(lst(lbl("wire", 2)), WORKBENCH, lst(lbl("tin-ingot")));
        recipe(lst(lbl("antenna")), WORKBENCH, lst(lbl("cable")));

        request(lbl("tv"));

        assertInputs(lbl("tin-ingot", 1));
        assertExcessOutputs();
        assertCatalysts(WORKBENCH);

        // Cables are made from wires, not the other way around, so it is important that wires appear before cables.
        assertSteps(lbl("wire", 2), lbl("cable", 2), lbl("antenna"), lbl("tv"));
    }

    private static List<TestLbl> WORKBENCH = lst(lbl("crafting-table"));

    private static <T> List<T> lst(T... args) {
        return Arrays.asList(args);
    }
}
