package me.towdium.jecalculation.network;


import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
public class ProxyServer implements IProxy {
    @Override
    public void initPre() {
        CapabilityManager.INSTANCE.register(Recipes.class, new JecaCapability.Storage(), Recipes::new);
    }

    @Override
    public void initPost() {
        ILabel.initServer();
    }
}
