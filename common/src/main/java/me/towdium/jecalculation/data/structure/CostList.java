package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.data.structure.Recipe.IO.INPUT;
import static me.towdium.jecalculation.data.structure.Recipe.IO.OUTPUT;
import static me.towdium.jecalculation.utils.Utilities.stream;

// positive => generate; negative => require
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CostList {
    List<ILabel> labels;

    public CostList() {
        labels = new ArrayList<>();
    }

    public CostList(List<ILabel> labels) {
        this.labels = labels.stream()
                .filter(i -> i != ILabel.EMPTY)
                .map(i -> i.copy().multiply(-1))
                .collect(Collectors.toList());
    }

    public CostList(List<ILabel> positive, List<ILabel> negative) {
        this(positive);
        multiply(-1);
        merge(new CostList(negative), false, true);
    }

    @SuppressWarnings("UnusedReturnValue")
    public CostList merge(CostList costList, boolean strict, boolean inplace) {
        CostList ret = inplace ? this : copy();
        costList.labels.forEach(i -> ret.labels.add(i.copy()));
        for (int i = 0; i < ret.labels.size(); i++) {
            for (int j = i + 1; j < ret.labels.size(); j++) {
                if (strict) {
                    ILabel a = ret.labels.get(i);
                    ILabel b = ret.labels.get(j);
                    if (a.matches(b)) {
                        ret.labels.set(i, a.setAmount(Math.addExact(a.getAmount(), b.getAmount())));
                        ret.labels.set(j, ILabel.EMPTY);
                    }
                } else {
                    Optional<ILabel> l = ILabel.MERGER.merge(ret.labels.get(i), ret.labels.get(j));
                    if (l.isPresent()) {
                        ret.labels.set(i, l.get());
                        ret.labels.set(j, ILabel.EMPTY);
                    }
                }
            }
        }
        ret.labels = ret.labels.stream().filter(i -> i != ILabel.EMPTY).collect(Collectors.toList());
        return ret;
    }

    public CostList multiply(long i) {
        labels = labels.stream().map(j -> j.multiply(i)).collect(Collectors.toList());
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CostList c) {
            CostList m = c.copy().multiply(-1);
            return merge(m, true, false).labels.isEmpty();
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
        return new Calculator();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (ILabel i : labels) hash ^= i.hashCode();
        return hash;
    }

    public class Calculator {
        ArrayList<Pair<CostList, CostList>> procedure = new ArrayList<>();
        ArrayList<ILabel> catalysts = new ArrayList<>();
        Recipes.RecipeIterator iterator = Controller.recipeIterator();
        private int index;

        public Calculator() throws ArithmeticException {
            LocalPlayer player = Minecraft.getInstance().player;
            HashSet<CostList> set = new HashSet<>();
            set.add(CostList.this);
            Pair<Recipe, Long> next = find(true);
            int count = 0;
            while (next != null) {
                CostList original = getCurrent();
                List<ILabel> outL = next.one.getLabel(OUTPUT).stream()
                        .filter(i -> i != ILabel.EMPTY).collect(Collectors.toList());
                CostList outC = new CostList(outL);
                outC.multiply(-next.two);
                List<ILabel> inL = next.one.getLabel(INPUT).stream()
                        .filter(i -> i != ILabel.EMPTY).collect(Collectors.toList());
                CostList inC = new CostList(inL);
                inC.multiply(next.two);
                CostList result = original.merge(outC, false, false);
                result.merge(inC, false, true);
                if (set.contains(result)) next = find(false);
                else {
                    set.add(result);
                    procedure.add(new Pair<>(result, outC));
                    addCatalyst(next.one.getLabel(Recipe.IO.CATALYST));
                    next = find(true);
                }
                if (count++ > 1000 && player != null) {
                    player.displayClientMessage(Component.translatable("jecalculation.chat.max_loop"), false);
                    break;
                }
            }
        }

        @Nullable
        private Pair<Recipe, Long> find(boolean reset) {
            if (reset) {
                index = 0;
                iterator = Controller.recipeIterator();
            }
            List<ILabel> labels = getCurrent().labels;
            for (; index < labels.size(); index++) {
                ILabel label = labels.get(index);
                if (label.getAmount() >= 0) continue;
                while (iterator.hasNext()) {
                    Recipe r = iterator.next();
                    if (r.matches(label).isPresent()) return new Pair<>(r, r.multiplier(label));
                }
                iterator = Controller.recipeIterator();
            }
            return null;
        }

        private void addCatalyst(List<ILabel> labels) {
            labels.stream().filter(i -> i != ILabel.EMPTY)
                    .forEach(i -> catalysts.stream()
                            .filter(j -> j.matches(i)).findAny()
                            .map(j -> j.setAmount(Math.max(i.getAmount(), j.getAmount())))
                            .orElseGet(Utilities.fake(() -> catalysts.add(i))));
        }

        private CostList getCurrent() {
            return procedure.isEmpty() ? CostList.this : procedure.get(procedure.size() - 1).one;
        }

        public List<ILabel> getCatalysts() {
            return catalysts;
        }

        public List<ILabel> getInputs() {
            return getCurrent().labels.stream().filter(i -> i.getAmount() < 0)
                    .map(i -> i.copy().multiply(-1)).collect(Collectors.toList());
        }

        public List<ILabel> getOutputs(List<ILabel> ignore) {
            return getCurrent().labels.stream()
                    .map(i -> i.copy().multiply(-1))
                    .map(i -> ignore.stream().flatMap(j -> stream(ILabel.MERGER.merge(i, j))).findFirst().orElse(i))
                    .filter(i -> i != ILabel.EMPTY && i.getAmount() < 0)
                    .map(i -> i.multiply(-1))
                    .collect(Collectors.toList());
        }

        public List<ILabel> getSteps() {
            List<ILabel> ret = procedure.stream()
                    .map(i -> i.two.labels.get(0))
                    .collect(Collectors.toList());
            Collections.reverse(ret);
            CostList cl = new CostList(ret).multiply(-1);
            return new CostList().merge(cl, false, true).labels;
        }
    }
}
