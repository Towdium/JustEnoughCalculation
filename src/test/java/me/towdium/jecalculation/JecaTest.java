package me.towdium.jecalculation;

import net.minecraft.client.util.SuffixArray;
import org.junit.jupiter.api.Test;

public class JecaTest {
    @Test
    public void test() {
        SuffixArray<Integer> sa = new SuffixArray<>();
        sa.add(1, "banana");
        sa.add(4, "banana");
        sa.add(2, "apple");
        sa.add(3, "pineapple");
        sa.generate();
        int i = 1;
    }
}
