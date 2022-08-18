package me.towdium.jecalculation.compat;

import me.towdium.jecalculation.compat.jei.JecaJEIPlugin;
import me.towdium.jecalculation.compat.rei.JecaREIPlugin;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.utils.wrappers.Trio;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModCompat {

    public static boolean isJEILoaded = false;
    public static boolean isREILoaded = false;

    public static boolean isRecipeScreen(Screen screen) {
        return (isJEILoaded && JecaJEIPlugin.isRecipeScreen(screen)) || (isREILoaded && JecaREIPlugin.isRecipeScreen(screen));
    }

    public static void showRecipe(ILabel l) {
        if (l == null || ILabel.EMPTY == l)
            return;
        if (isJEILoaded && JecaJEIPlugin.showRecipe(l))
            return;
        if (isREILoaded)
            JecaREIPlugin.showRecipe(l);
    }

    public static ILabel getLabelUnderMouse() {
        ILabel label;
        if (isJEILoaded && (label = JecaJEIPlugin.getLabelUnderMouse()) != ILabel.EMPTY) {
            return label;
        }
        if (isREILoaded)
            return JecaREIPlugin.getLabelUnderMouse();
        return ILabel.EMPTY;
    }

    public static void merge(EnumMap<Recipe.IO, List<Trio<ILabel, CostList, CostList>>> dst, List<?> objs, Class<?> context, Recipe.IO type) {
        List<ILabel> list = objs.stream().map(ILabel.Converter::from).collect(Collectors.toList());
        if (list.isEmpty()) return;
        ILabel rep = list.get(0).copy();
        if (type == Recipe.IO.INPUT && list.size() != 1) rep = ILabel.CONVERTER.first(list, context);
        ILabel fin = rep;

        dst.computeIfAbsent(type, i -> new ArrayList<>()).stream().filter(p -> {
            CostList cl = new CostList(list);
            if (p.three.equals(cl)) {
                ILabel.MERGER.merge(p.one, fin).ifPresent(i -> p.one = i);
                p.two = p.two.merge(cl, true, false);
                return true;
            } else return false;
        }).findAny().orElseGet(() -> {
            Trio<ILabel, CostList, CostList> ret = new Trio<>(fin, new CostList(list), new CostList(list));
            dst.get(type).add(ret);
            return ret;
        });
    }

}
