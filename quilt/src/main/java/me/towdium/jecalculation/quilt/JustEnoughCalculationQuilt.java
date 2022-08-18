package me.towdium.jecalculation.quilt;

import me.towdium.jecalculation.fabric_like.JustEnoughCalculationFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class JustEnoughCalculationQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        new JustEnoughCalculationFabricLike();
    }
}
