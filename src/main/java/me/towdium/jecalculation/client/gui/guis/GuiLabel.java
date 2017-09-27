package me.towdium.jecalculation.client.gui.guis;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.drawables.DContainer;
import me.towdium.jecalculation.client.gui.drawables.DPage;
import me.towdium.jecalculation.client.gui.drawables.DPanel;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.wrappers.Single;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuiLabel extends DContainer {
    DContainer container = new DContainer();
    Consumer<ILabel> callback;
    LoadingCache<Integer, IDrawable> cache = CacheBuilder.newBuilder().concurrencyLevel(1)
            .maximumWeight(16).weigher((Weigher<Integer, IDrawable>) (key, value) -> 1)
            .build(new CacheLoader<Integer, IDrawable>() {
                @Override
                public IDrawable load(Integer i) {
                    ILabel.RegistryEditor.Record record = ILabel.EDITOR.getRecords().get(i);
                    return record.editor.setCallback(callback);
                }
            });

    public GuiLabel(Consumer<ILabel> callback) {
        this.callback = callback;
        Single<Integer> index = new Single<>(0);
        ILabel.EDITOR.getRecords().forEach(r -> {
            int i = index.value;
            add(new DPage(i, r, false).setListener(() -> refresh(i)));
            index.value += 1;
        });
        add(new DPanel());
        add(container);
        refresh(0);
    }

    protected void refresh(int index) {
        container.clear();
        container.add(cache.getUnchecked(index));
        container.add(new DPage(index, ILabel.EDITOR.getRecords().get(index), true));
    }
}
