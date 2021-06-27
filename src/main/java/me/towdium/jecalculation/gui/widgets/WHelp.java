package me.towdium.jecalculation.gui.widgets;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.utils.Utilities.I18n;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static me.towdium.jecalculation.gui.JecaGui.Font.PLAIN;
import static me.towdium.jecalculation.gui.JecaGui.Font.SHADOW;
import static me.towdium.jecalculation.gui.Resource.*;

/**
 * Author: Towdium
 * Date: 18-9-23
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WHelp extends WContainer {
    protected String key;

    public WHelp(String content) {
        key = content;
        if (I18n.search("gui." + WHelp.this.key + ".title").two
                && I18n.search("gui." + WHelp.this.key + ".help").two) {
            add(new Impl());
        }
    }

    private class Impl extends WTooltip {
        public Impl() {
            super("common.help");
        }

        @Override
        public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
            super.onDraw(gui, xMouse, yMouse);
            gui.drawResourceContinuous(WGT_PANEL_N, -21, 0, 25, 24, 4);
            gui.drawResource(WGT_HELP_N, -19, 2);
            return false;
        }

        @Override
        public boolean mouseIn(int xMouse, int yMouse) {
            return JecaGui.mouseIn(-21, 0, 24, 24, xMouse, yMouse);
        }

        @Override
        public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
            boolean ret = mouseIn(xMouse, yMouse);
            if (ret) gui.root.setOverlay(new Doc());
            return ret;
        }
    }

    private class Doc extends WContainer {
        Text text = new Text();
        WSwitcher switcher = new WSwitcher(7, 146, 162, text.amount()).setListener(i -> text.setPage(i.getIndex()));

        public Doc() {
            WText title = new WText(7, 7, SHADOW, I18n.get("gui." + key + ".title"));
            add(new WPanel(), new Icon(), title, text, switcher);
        }

        @Override
        public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
            if (!super.onMouseClicked(gui, xMouse, yMouse, button)) gui.root.remove(this);
            return true;
        }

        @Override
        public boolean onMouseScroll(JecaGui gui, int xMouse, int yMouse, int diff) {
            switcher.move(-diff);
            text.setPage(switcher.getIndex());
            return super.onMouseScroll(gui, xMouse, yMouse, diff);
        }

        public class Text implements IWidget {
            List<List<String>> pages = new ArrayList<>();
            int page;

            public Text() {
                List<String> ss = I18n.wrap(I18n.get("gui." + key + ".help"), 162);
                List<String> tmp = new ArrayList<>();
                int count = 0;
                for (String s : ss) {
                    if (s.endsWith("\f")) {
                        tmp.add(s.substring(0, s.length() - 1));
                        pages.add(tmp);
                        tmp = new ArrayList<>();
                        count = 0;
                    } else if (count == 11) {
                        tmp.add(s);
                        pages.add(tmp);
                        tmp = new ArrayList<>();
                        count = 0;
                    } else {
                        tmp.add(s);
                        count++;
                    }
                }
                if (!tmp.isEmpty()) pages.add(tmp);
            }

            @Override
            public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
                gui.drawSplitText(7, 21, PLAIN, pages.get(page));
                return false;
            }

            public int amount() {
                return pages.size();
            }

            public void setPage(int i) {
                page = i;
            }
        }

        public class Icon extends WTooltip {
            public Icon() {
                super("common.close");
            }

            @Override
            public boolean onDraw(JecaGui gui, int xMouse, int yMouse) {
                super.onDraw(gui, xMouse, yMouse);
                gui.drawResourceContinuous(WGT_HELP_B, -21, 0, 25, 24, 4);
                gui.drawResource(WGT_HELP_F, -19, 2);
                return false;
            }

            @Override
            public boolean onTooltip(JecaGui gui, int xMouse, int yMouse, List<String> tooltip) {
                super.onTooltip(gui, xMouse, yMouse, tooltip);
                return mouseIn(xMouse, yMouse);
            }

            @Override
            public boolean onMouseClicked(JecaGui gui, int xMouse, int yMouse, int button) {
                boolean ret = mouseIn(xMouse, yMouse);
                if (ret) gui.root.setOverlay(null);
                return ret;
            }

            @Override
            public boolean mouseIn(int xMouse, int yMouse) {
                return JecaGui.mouseIn(-21, 0, 24, 24, xMouse, yMouse);
            }

            @Override
            public boolean onKeyPressed(JecaGui gui, int key, int modifier) {
                if (super.onKeyPressed(gui, key, modifier)) return true;
                if (key == GLFW.GLFW_KEY_ESCAPE) {
                    gui.root.setOverlay(null);
                    return true;
                } else return false;
            }
        }
    }
}
