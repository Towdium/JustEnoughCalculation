package me.towdium.jecalculation.data.structure;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static me.towdium.jecalculation.data.structure.Recipe.enumIoType.INPUT;
import static me.towdium.jecalculation.data.structure.Recipe.enumIoType.OUTPUT;

// positive => generate; negative => require
@ParametersAreNonnullByDefault
public class CostList {
    List<ILabel> labels;

    public CostList() {
        labels = new ArrayList<>();
    }

    public CostList(ILabel label) {
        labels = Collections.singletonList(label.copy().multiply(-1));
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


    public CostList(Recipe recipe) {
        this(Arrays.stream(recipe.getLabel(OUTPUT)).filter(i -> i != ILabel.EMPTY).collect(Collectors.toList()),
             Arrays.stream(recipe.getLabel(INPUT)).filter(i -> i != ILabel.EMPTY).collect(Collectors.toList()));
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
        if (obj instanceof CostList) {
            CostList c = (CostList) obj;
            CostList m = c.copy().multiply(-1);
            return merge(m, true, false).labels.isEmpty();
        } else
            return false;
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
        for (ILabel i : labels)
            hash ^= i.hashCode();
        return hash;
    }

    public class Calculator {
        ArrayList<Pair<CostList, CostList>> procedure = new ArrayList<>();
        ArrayList<ILabel> catalysts = new ArrayList<>();
        private int index;

        public Calculator() throws ArithmeticException {
            HashSet<CostList> set = new HashSet<>();
            set.add(CostList.this);
            Pair<Recipe, Long> next = find(true);
            int count = 0;
            while (next != null) {
                CostList original = getCurrent();
                CostList difference = new CostList(next.one);
                difference.multiply(next.two);
                CostList result = original.merge(difference, false, false);
                if (set.contains(result))
                    next = find(false);
                else {
                    set.add(result);
                    procedure.add(new Pair<>(result, difference));
                    addCatalyst(next.one.getLabel(Recipe.enumIoType.CATALYST));
                    next = find(true);
                }
                if (count++ > 1000) {
                    Minecraft
                            .getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("jecalculation.chat.max_loop"));
                    break;
                }
            }
        }

        @Nullable
        private Pair<Recipe, Long> find(boolean reset) {
            if (reset)
                index = 0;
            List<ILabel> labels = getCurrent().labels;
            for (; index < labels.size(); index++) {
                ILabel label = labels.get(index);
                if (label.getAmount() >= 0)
                    continue;
                Optional<Recipe> recipe = Controller.getRecipe(label);
                if (recipe.isPresent()) return new Pair<>(recipe.get(), recipe.get().multiplier(label));
            }
            return null;
        }

        private void addCatalyst(ILabel[] labels) {
            LOOP:
            for (ILabel i : labels) {
                for (ILabel j : catalysts) {
                    if (j.matches(i)) {
                        j.setAmount(Math.max(i.getAmount(), j.getAmount()));
                        continue LOOP;
                    }
                }
                catalysts.add(i.copy());
            }
        }

        private CostList getCurrent() {
            return procedure.isEmpty() ? CostList.this : procedure.get(procedure.size() - 1).one;
        }

        public List<ILabel> getCatalysts() {
            return catalysts;
        }

        public List<ILabel> getInputs() {
            return getCurrent().labels.stream().filter(i -> i.getAmount() < 0).map(i -> i.copy().multiply(-1))
                                      .collect(Collectors.toList());
        }

        public List<ILabel> getOutputs(List<ILabel> ignore) {
            return getCurrent().labels.stream()
                                      .map(i -> i.copy().multiply(-1))
                                      .map(i -> {
                                          for (ILabel j : ignore) {
                                              Optional<ILabel> merged = ILabel.MERGER.merge(i, j);
                                              if (merged.isPresent()) i = merged.get();
                                          }
                                          return i;
                                      })
                                      .filter(i -> i != ILabel.EMPTY && i.getAmount() < 0)
                                      .map(i -> i.multiply(-1))
                                      .collect(Collectors.toList());
        }

        public List<ILabel> getSteps() {
            //noinspection OptionalGetWithoutIsPresent
            List<ILabel> ret = procedure.stream()
                                        .map(i -> i.two.labels.stream().filter(l -> l.getAmount() > 0).findFirst().get())
                                        .collect(Collectors.toList());
            Collections.reverse(ret);
            return ret;
        }
    }
}
