package me.towdium.jecalculation.algorithm;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.utils.wrappers.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CostList {
    List<ILabel> labels;

    public CostList() {
        labels = new ArrayList<>();
    }

    public CostList(List<ILabel> labels) {
        this.labels = labels.stream().map(ILabel::copy).collect(Collectors.toList());
    }

    public CostList(List<ILabel> positive, List<ILabel> negative) {
        labels = positive.stream().map(ILabel::copy).collect(Collectors.toList());
        negative.forEach(i -> {
            ILabel l = i.copy();
            labels.add(l.invertAmount());
        });
    }

    public CostList(Recipe recipe) {
        this(Arrays.asList(recipe.getLabel(Recipe.enumIoType.OUTPUT)),
                Arrays.asList(recipe.getLabel(Recipe.enumIoType.INPUT)));
    }

    @SuppressWarnings("UnusedReturnValue")
    public CostList merge(CostList costList, boolean add, boolean strict) {
        CostList ret = copy();
        costList.labels.forEach(i -> ret.labels.add(add ? i.copy() : i.copy().invertAmount()));
        ret.cancel(strict);
        return ret;
    }

    private void cancel(boolean strict) {
        for (int i = 0; i < labels.size(); i++) {
            for (int j = 0; j < labels.size(); j++) {
                if (i == j) continue;
                if (strict) {
                    ILabel a = labels.get(i);
                    ILabel b = labels.get(j);
                    if (a.matches(b)) {
                        labels.set(i, a.setAmount(a.getAmount() + b.getAmount()));
                        labels.set(j, ILabel.EMPTY);
                    }
                } else {
                    Optional<ILabel> l = ILabel.MERGER.merge(labels.get(i), labels.get(j), true);
                    if (l.isPresent()) {
                        labels.set(i, l.get());
                        labels.set(j, ILabel.EMPTY);
                    }
                }
            }
        }
        labels = labels.stream().filter(i -> i != ILabel.EMPTY).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CostList) {
            CostList c = (CostList) obj;
            CostList m = c.copy();
            return m.merge(this, false, true).labels.isEmpty();
        } else return false;
    }

    public CostList copy() {
        CostList ret = new CostList();
        ret.labels = labels.stream().map(ILabel::copy).collect(Collectors.toList());
        return ret;
    }

    public boolean isEmpty() {
        return labels.isEmpty();
    }

    public List<ILabel> getLabels() {
        return labels;
    }

    public Calculator calculate() {
        return new Calculator(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (ILabel i : labels) hash ^= i.hashCode();
        return hash;
    }

    public static class Calculator {
        CostList start;
        ArrayList<Pair<CostList, CostList>> procedure;
        List<ILabel> catalysts;
        private int index;

        public Calculator(CostList cl) {
            HashSet<CostList> set = new HashSet<>();
            start = cl;
            set.add(start);
            Recipe next = find(true);
            while (next != null) {
                CostList original = getCurrent();
                CostList difference = new CostList(next);
                CostList result = original.merge(difference, false, false);
                if (set.contains(result)) next = find(false);
                else {
                    set.add(result);
                    procedure.add(new Pair<>(result, difference));
                    addCatalyst(next.getLabel(Recipe.enumIoType.CATALYST));
                    next = find(true);
                }
            }
        }

        private Recipe find(boolean reset) {
            if (reset) index = 0;
            List<ILabel> labels = getCurrent().labels;
            for (; index < labels.size(); index++) {
                ILabel label = labels.get(index);
                if (label.getAmount() >= 0) continue;
                Optional<Recipe> recipe = Controller.getRecipe(label, Recipe.enumIoType.OUTPUT);
                if (recipe.isPresent()) return recipe.get();
            }
            return null;
        }

        private void addCatalyst(ILabel[] labels) {
            LOOP:
            for (ILabel i : labels) {
                for (ILabel j : catalysts) {
                    if (j.matches(i)) {
                        j.setAmount(j.getAmount() + i.getAmount());
                        continue LOOP;
                    }
                }
                catalysts.add(i.copy());
            }
        }

        private CostList getCurrent() {
            return procedure.isEmpty() ? start : procedure.get(procedure.size() - 1).one;
        }
    }
}
