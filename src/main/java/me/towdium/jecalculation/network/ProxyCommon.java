package me.towdium.jecalculation.network;


import me.towdium.jecalculation.JecaCapability;
import me.towdium.jecalculation.JecaCapability.Container;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
public class ProxyCommon implements IProxy {
    @Override
    public void initPre() {
        CapabilityManager.INSTANCE.register(Container.class, new JecaCapability.Storage(), Container::new);
    }

    @Override
    public void initPost() {
        ILabel.initServer();
    }
}
