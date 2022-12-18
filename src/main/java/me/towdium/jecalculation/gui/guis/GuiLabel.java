package me.towdium.jecalculation.gui.guis;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.guis.pickers.IPicker;
import me.towdium.jecalculation.gui.widgets.WContainer;
import me.towdium.jecalculation.gui.widgets.WHelp;
import me.towdium.jecalculation.gui.widgets.WPage;
import me.towdium.jecalculation.gui.widgets.WPanel;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.wrappers.Wrapper;

/**
 * Author: towdium
 * Date:   17-9-14.
 */
@SuppressWarnings("UnstableApiUsage")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiLabel extends Gui {
    WContainer container = new WContainer();
    IPicker current;
    Consumer<ILabel> callback;
    LoadingCache<Integer, IPicker> cache = CacheBuilder.newBuilder().build(new CacheLoader<Integer, IPicker>() {
        @Override
        public IPicker load(Integer i) {
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

    @Override
    public boolean acceptsLabel() {
        return current.acceptsLabel();
    }

    protected void refresh(int index) {
        container.clear();
        current = cache.getUnchecked(index);
        container.add(current);
        container.add(new WPage(index, ILabel.EDITOR.getRecords().get(index), true));
    }
}
