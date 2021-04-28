package me.towdium.jecalculation.client.gui.drawables;

import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.core.labels.ILabel;
import me.towdium.jecalculation.utils.Utilities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Author: towdium
 * Date:   17-8-19.
 */
@ParametersAreNonnullByDefault
public class DPager extends DContainer {
    public DPager() {
        // TODO
        //add(new DPage(0, ILabel.DESERIALIZER.getRecords().stream().filter(r -> r.representation != null).findFirst().get()));
    }

    public class DPage implements IDrawable {
        protected int index;
        protected ILabel.RegistryEditor.Record record;

        public DPage(int index, ILabel.RegistryEditor.Record record) {
            this.index = index;
            this.record = record;
        }

        @Override
        public void onDraw(JecGui gui, int xMouse, int yMouse) {
            gui.drawResourceContinuous(Resource.WGT_PAGER_B, index * 24, -21, 24, 24, 4, 4, 4, 4);
        }

        @Override
        public boolean onClicked(JecGui gui, int xMouse, int yMouse, int button) {
            return false;
        }
    }
}
