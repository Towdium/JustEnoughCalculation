package me.towdium.jecalculation.gui.widgets;

/**
 * Author: Towdium
 * Date: 18-9-25
 */
public interface IContainer extends IWidget {
    void add(IWidget w);

    void addAll(IWidget... w);

    void remove(IWidget w);

    void removeAll(IWidget... w);

    void clear();
}
