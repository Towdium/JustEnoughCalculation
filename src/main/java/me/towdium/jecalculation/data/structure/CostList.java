package me.towdium.jecalculation.data.structure;

import static me.towdium.jecalculation.utils.Utilities.stream;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;

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
        mergeInplace(new CostList(negative), false);
    }

    public static CostList merge(CostList a, CostList b, boolean strict) {
        CostList ret = a.copy();
        ret.mergeInplace(b, strict);
        return ret;
    }

    /**
     * Merge that to this
     *
     * @param that   cost list to merge
     * @param strict if true, only merge same label
     */
    public void mergeInplace(CostList that, boolean strict) {
        that.labels.forEach(i -> this.labels.add(i.copy()));
        for (int i = 0; i < this.labels.size(); i++) {
            for (int j = i + 1; j < this.labels.size(); j++) {
                if (strict) {
                    ILabel a = this.labels.get(i);
                    ILabel b = this.labels.get(j);
                    if (a.matches(b)) {
                        this.labels.set(i, a.setAmount(Math.addExact(a.getAmount(), b.getAmount())));
                        this.labels.set(j, ILabel.EMPTY);
                    }
                } else {
                    Optional<ILabel> l = ILabel.MERGER.merge(this.labels.get(i), this.labels.get(j));
                    if (l.isPresent()) {
                        this.labels.set(i, l.get());
                        this.labels.set(j, ILabel.EMPTY);
                    }
                }
            }
        }
        this.labels = this.labels.stream().filter(i -> i != ILabel.EMPTY).collect(Collectors.toList());
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
            return CostList.merge(this, m, true).labels.isEmpty();
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
            HashSet<CostList> set = new HashSet<>();
            set.add(CostList.this);

            // reset index & iterator
            reset();
            Pair<Recipe, Long> next = find();
            int count = 0;
            while (next != null) {
                CostList original = getCurrent();
                List<ILabel> outL = next.one.getOutput().stream()
                        .filter(i -> i != ILabel.EMPTY)
                        .collect(Collectors.toList());
                CostList outC = new CostList(outL);
                outC.multiply(-next.two);
                List<ILabel> inL = next.one.getInput().stream()
                        .filter(i -> i != ILabel.EMPTY)
                        .collect(Collectors.toList());
                CostList inC = new CostList(inL);
                inC.multiply(next.two);
                CostList result = CostList.merge(original, outC, false);
                result.mergeInplace(inC, false);
                if (!set.contains(result)) {
                    set.add(result);
                    procedure.add(new Pair<>(result, outC));
                    addCatalyst(next.one.getCatalyst());
                    reset();
                }
                next = find();
                if (count++ > 1000) {
                    Utilities.addChatMessage(Utilities.ChatMessage.MAX_LOOP);
                    break;
                }
            }
        }

        private void reset() {
            index = 0;
            iterator = Controller.recipeIterator();
        }

        /**
         * Find next recipe and its amount
         *
         * @return pair of the next recipe and its amount
         */
        @Nullable
        private Pair<Recipe, Long> find() {
            List<ILabel> labels = getCurrent().labels;
            for (; index < labels.size(); index++) {
                ILabel label = labels.get(index);
                // Only negative label is required to calculate
                if (label.getAmount() >= 0) continue;
                // Find the recipe for the label.
                // Reset or not reset the iterator is a question
                while (iterator.hasNext()) {
                    Recipe r = iterator.next();
                    if (r.matches(label).isPresent()) return new Pair<>(r, r.multiplier(label));
                }
                iterator = Controller.recipeIterator();
            }
            return null;
        }

        private void addCatalyst(List<ILabel> labels) {
            labels.stream().filter(i -> i != ILabel.EMPTY).forEach(i -> catalysts.stream()
                    .filter(j -> j.matches(i))
                    .findAny()
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
            return getCurrent().labels.stream()
                    .filter(i -> i.getAmount() < 0)
                    .map(i -> i.copy().multiply(-1))
                    .collect(Collectors.toList());
        }

        public List<ILabel> getOutputs(List<ILabel> ignore) {
            return getCurrent().labels.stream()
                    .map(i -> i.copy().multiply(-1))
                    .map(i -> ignore.stream()
                            .flatMap(j -> stream(ILabel.MERGER.merge(i, j)))
                            .findFirst()
                            .orElse(i))
                    .filter(i -> i != ILabel.EMPTY && i.getAmount() < 0)
                    .map(i -> i.multiply(-1))
                    .collect(Collectors.toList());
        }

        public List<ILabel> getSteps() {
            List<ILabel> ret = procedure.stream().map(i -> i.two.labels.get(0)).collect(Collectors.toList());
            Collections.reverse(ret);
            CostList cl = new CostList(ret).multiply(-1);
            CostList temp = new CostList();
            temp.mergeInplace(cl, false);
            return temp.labels;
        }
    }
}
