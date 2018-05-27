package me.towdium.jecalculation.network;

import me.towdium.jecalculation.data.label.ILabel;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
public class ProxyServer implements IProxy {
    @Override
    public void initPost() {
        ILabel.initServer();
    }
}
