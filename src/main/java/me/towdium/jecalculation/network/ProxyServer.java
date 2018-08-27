package me.towdium.jecalculation.network;


import me.towdium.jecalculation.data.capacity.JecaCapability;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
public class ProxyServer implements IProxy {
    @Override
    public void initPre() {
        CapabilityManager.INSTANCE.register(Recipes.class,
                new JecaCapability(), Recipes::new);
    }

    @Override
    public void initPost() {
        ILabel.initServer();
    }

    @Override
    public void runOnSide(Runnable r, Side s) {
        if (s == Side.SERVER) r.run();
    }
}
