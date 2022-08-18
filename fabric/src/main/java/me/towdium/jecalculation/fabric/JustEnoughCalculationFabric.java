package me.towdium.jecalculation.fabric;

import me.towdium.jecalculation.fabric_like.JustEnoughCalculationFabricLike;
import net.fabricmc.api.ModInitializer;

public class JustEnoughCalculationFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new JustEnoughCalculationFabricLike();
    }
}
