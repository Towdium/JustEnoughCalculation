package me.towdium.jecalculation.data.structure;

import static me.towdium.jecalculation.utils.Utilities.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;

// positive => generate; negative => require
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractCostListService<LabelT, RecipeT, CostListT> {

    private final Dependencies<LabelT, RecipeT> d;
    private final CostLists<LabelT, CostListT> costLists;
    private final Class<CostListT> costListClass;

    AbstractCostListService(Dependencies<LabelT, RecipeT> dependencies, CostLists<LabelT, CostListT> costLists,
        Class<CostListT> costListClass) {
        this.d = dependencies;
        this.costLists = costLists;
        this.costListClass = costListClass;
    }

    public CostListT newNegatedCostList(List<LabelT> labels) {
        List<LabelT> negativeLabels = labels.stream()
            .filter(d::isNotEmptyLabel)
            .map(i -> d.multiplyLabel(d.copyLabel(i), -1))
            .collect(Collectors.toList());
        return costLists.newCostList(negativeLabels);
    }

    public CostListT newPosNegCostList(List<LabelT> positive, List<LabelT> negative) {
        CostListT ret = newNegatedCostList(positive);
        multiply(ret, -1);
        mergeInplace(ret, newNegatedCostList(negative), false);
        return ret;
    }

    public CostListT strictMergeCostList(CostListT a, CostListT b) {
        return mergeCostLists(a, b, false);
    }

    public List<LabelT> getLabels(CostListT costList) {
        return costLists.getLabels(costList);
    }

    public Calculation<LabelT> calculate(CostListT costList) {
        class ProcedureStep {

            CostListT stillNeeded; // mostly negative until the last step. may have some positive if there are excess
                                   // outputs
            RecipeT recipe;
            long multiplier;
            CostListT multipliedRecipeOutputs; // recipe.outputs * multiplier; 1st item is the main output
        }
        ArrayList<ProcedureStep> procedure = new ArrayList<>();
        ArrayList<LabelT> catalysts = new ArrayList<>();

        return new Calculation<LabelT>() {

            private Iterator<RecipeT> iterator = d.recipeIterator();
            private int index;

            {
                HashSet<CostListT> set = new HashSet<>();
                set.add(costList);

                // reset index & iterator
                reset();
                Pair<RecipeT, Long> next = find();
                int count = 0;
                while (next != null) {
                    ProcedureStep procedureStep = new ProcedureStep();
                    procedureStep.recipe = next.one;
                    procedureStep.multiplier = next.two;
                    CostListT original = getCurrent();
                    List<LabelT> outL = d.getRecipeOutput(next.one)
                        .stream()
                        .filter(d::isNotEmptyLabel)
                        .collect(Collectors.toList());
                    CostListT outC = newNegatedCostList(outL);
                    multiply(outC, -next.two);
                    procedureStep.multipliedRecipeOutputs = outC;
                    List<LabelT> inL = d.getRecipeInput(next.one)
                        .stream()
                        .filter(d::isNotEmptyLabel)
                        .collect(Collectors.toList());
                    CostListT inC = newNegatedCostList(inL);
                    multiply(inC, next.two);
                    CostListT result = mergeCostLists(original, outC, false);
                    mergeInplace(result, inC, false);
                    procedureStep.stillNeeded = result;
                    if (!set.contains(result)) {
                        set.add(result);
                        procedure.add(procedureStep);
                        addCatalyst(d.getRecipeCatalyst(next.one));
                        reset();
                    }
                    next = find();
                    if (count++ > 1000) {
                        d.addMaxLoopChatMessage();
                        break;
                    }
                }
            }

            @Override
            public List<LabelT> getCatalysts() {
                return catalysts;
            }

            @Override
            public List<LabelT> getInputs() {
                return getLabels(getCurrent()).stream()
                    .filter(i -> d.getLabelAmount(i) < 0)
                    .map(i -> d.multiplyLabel(d.copyLabel(i), -1))
                    .collect(Collectors.toList());
            }

            @Override
            public List<LabelT> getOutputs(List<LabelT> ignore) {
                return getLabels(getCurrent()).stream()
                    .map(i -> d.multiplyLabel(d.copyLabel(i), -1))
                    .map(
                        i -> ignore.stream()
                            .flatMap(j -> stream(d.mergeLabels(i, j)))
                            .findFirst()
                            .orElse(i))
                    .filter(i -> d.isNotEmptyLabel(i) && d.getLabelAmount(i) < 0)
                    .map(i -> d.multiplyLabel(i, -1))
                    .collect(Collectors.toList());
            }

            @Override
            public List<LabelT> getSteps(List<LabelT> givenInventory) {
                // First we try running a simulated inventory through the procedure backwards, greedily preferring steps
                // that keep a small inventory, although we prohibit steps that are impossible and merge steps that use
                // identical recipes whenever possible. There's no backtracking, so it's posssible to get stuck in a
                // corner, in which case we fall back to the simpler solution below. As long as the number of labels in
                // the simulated inventory stays relatively bounded, this algorithm is close to quadratic (like the
                // fallback). It could in theory bump up to cubic if there were lots of excess outputs of many distinct
                // labels, but that doesn't seem to be a problem in practice.
                {
                    List<LabelT> startingInventory = new ArrayList<>(givenInventory);
                    startingInventory.addAll(getInputs());

                    CostListT inventory = costLists.newCostList(startingInventory);
                    List<ProcedureStep> remainingProcedureSteps = new ArrayList<>(procedure);
                    List<Pair<RecipeT, Long>> optimizedSteps = new ArrayList<>();

                    while (!remainingProcedureSteps.isEmpty()) {
                        final RecipeT preferredRecipe = optimizedSteps.isEmpty() ? null
                            : optimizedSteps.get(optimizedSteps.size() - 1).one;
                        Integer indexOfBestStep = null;
                        CostListT inventoryAfterBestStep = null;
                        int sizeOfInventoryAfterBestStep = Integer.MAX_VALUE;
                        for (int i = remainingProcedureSteps.size() - 1; i >= 0; i--) {
                            ProcedureStep step = remainingProcedureSteps.get(i);
                            CostListT candidateInventory = mergeCostLists(
                                inventory,
                                recipeAsCostList(step.recipe, step.multiplier),
                                false);
                            if (!isAllPositive(candidateInventory)) {
                                // Don't give the user an impossible plan.
                                continue;
                            }
                            int inventorySize = estimatedNumSlotsTakenBy(candidateInventory);
                            if (indexOfBestStep == null) {
                                // First encounter of an option with a non-negative inventory
                                indexOfBestStep = i;
                                inventoryAfterBestStep = candidateInventory;
                                sizeOfInventoryAfterBestStep = inventorySize;
                                continue;
                            }
                            ProcedureStep bestStep = remainingProcedureSteps.get(indexOfBestStep);
                            if (step.recipe.equals(preferredRecipe) && !bestStep.recipe.equals(preferredRecipe)) {
                                // First encounter of an option that uses the same recipe as our most recent step; stop
                                // here
                                indexOfBestStep = i;
                                inventoryAfterBestStep = candidateInventory;
                                break;
                            }
                            if (inventorySize < sizeOfInventoryAfterBestStep) {
                                // No matching recipe found yet, but this step gives the smallest inventory
                                indexOfBestStep = i;
                                inventoryAfterBestStep = candidateInventory;
                                sizeOfInventoryAfterBestStep = inventorySize;
                            }
                        }
                        if (indexOfBestStep == null) {
                            // Stuck in a corner; give up
                            optimizedSteps = null;
                            break;
                        }

                        // Removal is linear time in the worst case, but
                        // 1. Java's LinkedList would require a re-traversal anyway
                        // 2. Most of the time, indexOfBestStep will be near the end.
                        // 3. Array copies are fast.
                        // 4. We just finished a linear scan, so this doesn't worsen our complexity.
                        ProcedureStep bestStep = remainingProcedureSteps.remove((int) indexOfBestStep);

                        inventory = inventoryAfterBestStep;
                        if (!optimizedSteps.isEmpty() && bestStep.recipe.equals(preferredRecipe)) {
                            // merge with previous step
                            Pair<RecipeT, Long> latest = optimizedSteps.get(optimizedSteps.size() - 1);
                            latest.two = latest.two + bestStep.multiplier;
                        } else {
                            // add new step
                            optimizedSteps.add(new Pair<>(bestStep.recipe, bestStep.multiplier));
                        }
                    }
                    if (optimizedSteps != null) {
                        List<LabelT> retLabels = new ArrayList<>(optimizedSteps.size());
                        for (Pair<RecipeT, Long> pair : optimizedSteps) {
                            List<LabelT> outL = d.getRecipeOutput(pair.one)
                                .stream()
                                .filter(d::isNotEmptyLabel)
                                .collect(Collectors.toList());
                            CostListT outC = newNegatedCostList(outL);
                            multiply(outC, -pair.two);
                            retLabels.add(
                                costLists.getLabels(outC)
                                    .get(0));
                        }
                        return retLabels;
                    }
                }

                // If we reach here, then the above approach got trapped in a corner where all paths forward led to
                // negative items in the simulated inventory. Here we fall back to a straightforward merge, which
                // occasionally gives steps out of order, but 99% of the time gives a right answer.
                {
                    //
                    List<LabelT> ret = procedure.stream()
                        .map(
                            i -> costLists.getLabels(i.multipliedRecipeOutputs)
                                .get(0))
                        .collect(Collectors.toList());
                    Collections.reverse(ret);
                    CostListT cl = multiply(newNegatedCostList(ret), -1);
                    CostListT temp = newNegatedCostList(new ArrayList<>());
                    mergeInplace(temp, cl, false);
                    return costLists.getLabels(temp);
                }
            }

            private boolean isAllPositive(CostListT candidateInventory) {
                for (LabelT label : costLists.getLabels(candidateInventory)) {
                    if (d.getLabelAmount(label) < 0) {
                        return false;
                    }
                }
                return true;
            }

            private void reset() {
                index = 0;
                iterator = d.recipeIterator();
            }

            /**
             * Find next recipe and its amount
             *
             * @return pair of the next recipe and its amount
             */
            @Nullable
            private Pair<RecipeT, Long> find() {
                List<LabelT> labels = getLabels(getCurrent());
                for (; index < labels.size(); index++) {
                    LabelT label = labels.get(index);
                    // Only negative label is required to calculate
                    if (d.getLabelAmount(label) >= 0) continue;
                    // Find the recipe for the label.
                    // Reset or not reset the iterator is a question
                    while (iterator.hasNext()) {
                        RecipeT r = iterator.next();
                        if (d.recipeOutputMatches(r, label)
                            .isPresent()) {
                            return new Pair<>(r, d.multiplier(r, label));
                        }
                    }
                    iterator = d.recipeIterator();
                }
                return null;
            }

            private void addCatalyst(List<LabelT> labels) {
                labels.stream()
                    .filter(d::isNotEmptyLabel)
                    .forEach(
                        i -> catalysts.stream()
                            .filter(j -> d.labelMatches(j, i))
                            .findAny()
                            .map(j -> d.setLabelAmount(j, Math.max(d.getLabelAmount(i), d.getLabelAmount(j))))
                            .orElseGet(Utilities.fake(() -> catalysts.add(i))));
            }

            private CostListT getCurrent() {
                return procedure.isEmpty() ? costList : procedure.get(procedure.size() - 1).stillNeeded;
            }
        };
    };

    interface Dependencies<LabelT, RecipeT> {

        // Labels
        LabelT copyLabel(LabelT label);

        LabelT getEmptyLabel();

        long getLabelAmount(LabelT label);

        boolean isNotEmptyLabel(LabelT label);

        boolean labelMatches(LabelT self, LabelT that);

        Optional<LabelT> mergeLabels(LabelT a, LabelT b);

        LabelT multiplyLabel(LabelT label, float i);

        LabelT setLabelAmount(LabelT label, long amount);

        // Recipes
        List<LabelT> getRecipeCatalyst(RecipeT recipe);

        List<LabelT> getRecipeInput(RecipeT recipe);

        List<LabelT> getRecipeOutput(RecipeT recipe);

        Optional<LabelT> recipeOutputMatches(RecipeT recipe, LabelT label);

        long multiplier(RecipeT recipe, LabelT label);

        Iterator<RecipeT> recipeIterator();

        // Utilities
        void addMaxLoopChatMessage();
    }

    interface CostLists<LabelT, CostListT> {

        CostListT newCostList(List<LabelT> labels);

        List<LabelT> getLabels(CostListT self);

        void setLabels(CostListT self, List<LabelT> labels);
    }

    private CostListT mergeCostLists(CostListT a, CostListT b, boolean strict) {
        CostListT ret = copyCostList(a);
        mergeInplace(ret, b, strict);
        return ret;
    }

    /**
     * Merge self to this
     *
     * @param that   cost list to merge
     * @param strict if true, only merge same label
     */
    private void mergeInplace(CostListT self, CostListT that, boolean strict) {
        List<LabelT> thisLabels = getLabels(self);
        getLabels(that).forEach(i -> thisLabels.add(d.copyLabel(i)));
        for (int i = 0; i < thisLabels.size(); i++) {
            for (int j = i + 1; j < thisLabels.size(); j++) {
                if (strict) {
                    LabelT a = thisLabels.get(i);
                    LabelT b = thisLabels.get(j);
                    if (d.labelMatches(a, b)) {
                        thisLabels.set(i, d.setLabelAmount(a, Math.addExact(d.getLabelAmount(a), d.getLabelAmount(b))));
                        thisLabels.set(j, d.getEmptyLabel());
                    }
                } else {
                    Optional<LabelT> l = d.mergeLabels(thisLabels.get(i), thisLabels.get(j));
                    if (l.isPresent()) {
                        thisLabels.set(i, l.get());
                        thisLabels.set(j, d.getEmptyLabel());
                    }
                }
            }
        }
        costLists.setLabels(
            self,
            thisLabels.stream()
                .filter(d::isNotEmptyLabel)
                .collect(Collectors.toList()));
    }

    private CostListT multiply(CostListT self, long i) {
        costLists.setLabels(
            self,
            costLists.getLabels(self)
                .stream()
                .map(j -> d.multiplyLabel(j, i))
                .collect(Collectors.toList()));
        return self;
    }

    boolean costListEquals(CostListT self, Object obj) {
        if (costListClass.isInstance(obj)) {
            CostListT c = (CostListT) obj;
            CostListT m = multiply(copyCostList(c), -1);
            return getLabels(mergeCostLists(self, m, true)).isEmpty();
        } else return false;
    }

    protected CostListT copyCostList(CostListT from) {
        CostListT ret = newNegatedCostList(Collections.emptyList());
        costLists.setLabels(
            ret,
            costLists.getLabels(from)
                .stream()
                .map(d::copyLabel)
                .collect(Collectors.toList()));
        return ret;
    }

    private CostListT recipeAsCostList(RecipeT recipe, long multiplier) {
        // todo: unify with body of calculate()
        List<LabelT> outL = d.getRecipeOutput(recipe)
            .stream()
            .filter(d::isNotEmptyLabel)
            .collect(Collectors.toList());
        CostListT outC = newNegatedCostList(outL);
        multiply(outC, -multiplier);
        List<LabelT> inL = d.getRecipeInput(recipe)
            .stream()
            .filter(d::isNotEmptyLabel)
            .collect(Collectors.toList());
        CostListT inC = newNegatedCostList(inL);
        multiply(inC, multiplier);
        return mergeCostLists(inC, outC, false);
    }

    private int estimatedNumSlotsTakenBy(CostListT costLisT) {
        int total = 0;
        for (LabelT label : costLists.getLabels(costLisT)) {
            // Assuming 64 is not valid for snowballs and unstackables, but good enough for an estimate
            total += (int) Math.ceil(d.getLabelAmount(label) / 64.0);
        }
        return total;
    }
}
