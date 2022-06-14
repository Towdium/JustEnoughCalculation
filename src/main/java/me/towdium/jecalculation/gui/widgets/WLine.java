package me.towdium.jecalculation.gui.widgets;

import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Author: towdium
 * Date:   17-8-18.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class WLine implements IWidget {
    int xPos, yPos, xSize, ySize;

    public WLine(int y) {
        this(7, y, 162, true);
    }

    public WLine(int xPos, int yPos, int size, boolean horizontal) {
        this.yPos = yPos;
        this.xPos = xPos;
        if (horizontal) {
            xSize = size;
            ySize = 2;
        } else {
            xSize = 2;
            ySize = size;
        }
    }

    @Override
    public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
        gui.drawResourceContinuous(Resource.WGT_SLOT, xPos, yPos, xSize, ySize, 1);
        return false;
    }

    public static class Joint implements IWidget {
        int xPos, yPos;
        boolean up, down, left, right;
        public static Resource u = Resource.WGT_SLOT.sub(9, 0, 2, 1);
        public static Resource d = Resource.WGT_SLOT.sub(9, 19, 2, 1);
        public static Resource l = Resource.WGT_SLOT.sub(0, 9, 1, 2);
        public static Resource r = Resource.WGT_SLOT.sub(19, 9, 1, 2);
        public static Resource ul = Resource.WGT_SLOT.sub(0, 0, 1, 1);
        public static Resource ur = Resource.WGT_SLOT.sub(19, 0, 1, 1);
        public static Resource ll = Resource.WGT_SLOT.sub(0, 19, 1, 1);
        public static Resource lr = Resource.WGT_SLOT.sub(19, 19, 1, 1);

        public Joint(int xPos, int yPos, boolean up, boolean down, boolean left, boolean right) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
            if (!up) gui.drawResource(u, xPos, yPos);
            if (!down) gui.drawResource(d, xPos, yPos + 1);
            if (!left) gui.drawResource(l, xPos, yPos);
            if (!right) gui.drawResource(r, xPos + 1, yPos);
            if (left == up) gui.drawResource(ul, xPos, yPos);
            if (right == up) gui.drawResource(ur, xPos + 1, yPos);
            if (left == down) gui.drawResource(ll, xPos, 1 + yPos);
            if (right == down) gui.drawResource(lr, xPos + 1, yPos + 1);
            return false;
        }
    }
}
