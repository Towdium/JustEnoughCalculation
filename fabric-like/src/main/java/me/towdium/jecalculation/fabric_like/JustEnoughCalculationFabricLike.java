package me.towdium.jecalculation.fabric_like;

import dev.architectury.platform.Platform;
import me.towdium.jecalculation.JecaCommand;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class JustEnoughCalculationFabricLike {

    public JustEnoughCalculationFabricLike() {
        //noinspection InstantiationOfUtilityClass
        new JustEnoughCalculation();
        if (Platform.getEnv() == EnvType.CLIENT)
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> JecaCommand.register(dispatcher));
        JecaConfig.load();
    }

}
