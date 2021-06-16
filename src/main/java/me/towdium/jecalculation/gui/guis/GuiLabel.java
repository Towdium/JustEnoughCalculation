package me.towdium.jecalculation.gui.guis;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.widgets.*;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class GuiLabel extends Gui {
    WContainer container = new WContainer();
    Consumer<ILabel> callback;
    LoadingCache<Integer, IWidget> cache = CacheBuilder.newBuilder().concurrencyLevel(1)
            .maximumWeight(16).weigher((Weigher<Integer, IWidget>) (key, value) -> 1)
            .build(new CacheLoader<Integer, IWidget>() {
                @Override
                public IWidget load(Integer i) {
                    ILabel.RegistryEditor.Record record = ILabel.EDITOR.getRecords().get(i);
                    return record.editor.get().setCallback(callback);
                }
            });

    public GuiLabel(Consumer<ILabel> callback) {
        this.callback = callback;
        Wrapper<Integer> index = new Wrapper<>(0);
        ILabel.EDITOR.getRecords().forEach(r -> {
            int i = index.value;
            add(new WPage(i, r, false).setListener(j -> refresh(i)));
            index.value += 1;
        });
        add(new WHelp("label"), new WPanel());
        add(container);
        refresh(0);
    }

    protected void refresh(int index) {
        container.clear();
        container.add(cache.getUnchecked(index));
        container.add(new WPage(index, ILabel.EDITOR.getRecords().get(index), true));
    }
}
