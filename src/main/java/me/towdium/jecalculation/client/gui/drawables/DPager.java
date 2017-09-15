package me.towdium.jecalculation.client.gui.drawables;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.client.gui.IDrawable;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.Resource;
import me.towdium.jecalculation.core.labels.ILabel;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: towdium
 * Date:   17-9-15.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
