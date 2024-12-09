package me.towdium.jecalculation.data.structure;

import static me.towdium.jecalculation.data.structure.TestRcp.rcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCostListServiceTest {

    private static TestLbl EMPTY = new TestLbl("THE_EMPTY_LABEL", 0);

    Calculation<TestLbl> calculation;
    private List<TestLbl> inventory = Collections.emptyList();
    private final List<TestRcp> recipes = new ArrayList<>();
    private boolean maxLoopTriggered = false;

    private AbstractCostListService.Dependencies<TestLbl, TestRcp> dependencies = new AbstractCostListService.Dependencies<TestLbl, TestRcp>() {

        @Override
        public TestLbl copyLabel(TestLbl label) {
            return label.clone();
        }

        @Override
        public TestLbl getEmptyLabel() {
            return EMPTY;
        }

        @Override
        public long getLabelAmount(TestLbl label) {
            return label.amount;
        }

        @Override
        public boolean isNotEmptyLabel(TestLbl label) {
            return !"THE_EMPTY_LABEL".equals(label.name);
        }

        @Override
        public boolean labelMatches(TestLbl self, TestLbl that) {
            return self.name.equals(that.name);
        }

        @Override
        public Optional<TestLbl> mergeLabels(TestLbl a, TestLbl b) {
            // For some reason this is way more complicated in ILabel, involving multiplying by 100
            // adding 99, and then dividing by 100. Not sure why. Obviously something to do with
            // percents, but weirdly, this is what happens when isPercent() returns false.
            // I think my naive implementation is still valid for the tests at least.
            if (!a.name.equals(b.name)) {
                return Optional.empty();
            }
            long sum = a.amount + b.amount;
            if (sum == 0) {
                return Optional.of(EMPTY);
            }
            return Optional.of(new TestLbl(a.name, sum));
        }

        @Override
        public TestLbl multiplyLabel(TestLbl label, float i) {
            float amount = i * label.amount;
            if (amount > Long.MAX_VALUE) throw new ArithmeticException("Multiply overflow");
            return setLabelAmount(label, (long) amount);
        }

        @Override
        public TestLbl setLabelAmount(TestLbl label, long amount) {
            if (amount == 0) return EMPTY; // consistent with actual implementation, but this feels like a bug in
                                           // waiting to me, since it never actually mutates the input!
            label.amount = amount;
            return label;
        }

        @Override
        public List<TestLbl> getRecipeCatalyst(TestRcp recipe) {
            return recipe.catalysts;
        }

        @Override
        public List<TestLbl> getRecipeInput(TestRcp recipe) {
            return recipe.inputs;
        }

        @Override
        public List<TestLbl> getRecipeOutput(TestRcp recipe) {
            return recipe.outputs;
        }

        @Override
        public Optional<TestLbl> recipeOutputMatches(TestRcp recipe, TestLbl label) {
            for (TestLbl output : recipe.outputs) {
                if (mergeLabels(label, output).isPresent()) {
                    return Optional.of(output);
                }
            }
            return Optional.empty();
        }

        @Override
        public long multiplier(TestRcp recipe, TestLbl label) {
            for (TestLbl output : recipe.outputs) {
                if (mergeLabels(label, output).isPresent()) {
                    long amountA = Math.multiplyExact(label.amount, 100L);
                    long amountB = Math.multiplyExact(output.amount, 100L);
                    return (amountB + Math.abs(amountA) - 1) / amountB;
                }
            }
            return 0L;
        }

        @Override
        public Iterator<TestRcp> recipeIterator() {
            return recipes.iterator();
        }

        @Override
        public void addMaxLoopChatMessage() {
            maxLoopTriggered = true;
        }
    };

    private AbstractCostListService.CostLists<TestLbl, List<TestLbl>> costLists = new AbstractCostListService.CostLists<TestLbl, List<TestLbl>>() {

        @Override
        public List<TestLbl> newCostList(List<TestLbl> labels) {
            return labels;
        }

        @Override
        public List<TestLbl> getLabels(List<TestLbl> self) {
            return self;
        }

        @Override
        public void setLabels(List<TestLbl> self, List<TestLbl> labels) {
            self.clear();
            self.addAll(labels);
        }
    };

    private CostListService service = new CostListService();

    private class CostListService extends AbstractCostListService<TestLbl, TestRcp, List<TestLbl>> {

        CostListService() {
            super(dependencies, costLists, (Class) List.class);
        }
    }

    void inventory(TestLbl... inventory) {
        inventory(Arrays.asList(inventory));
    }

    void inventory(List<TestLbl> inventory) {
        this.inventory = inventory;
    }

    void recipe(List<TestLbl> outputs, List<TestLbl> catalysts, List<TestLbl> inputs) {
        recipes.add(rcp(outputs, catalysts, inputs));
    }

    void request(TestLbl label) {
        calculation = service.calculate(service.newPosNegCostList(inventory, Collections.singletonList(label)));
    }

    void assertInputs(TestLbl... inputs) {
        assertInputs(Arrays.asList(inputs));
    }

    void assertInputs(List<TestLbl> inputs) {
        List<TestLbl> actualInputs = calculation.getInputs();
        if (!inputs.equals(actualInputs)) {
            throw new AssertionError("expectedInputs = " + inputs + ", actualInputs = " + actualInputs);
        }
    }

    void assertExcessOutputs(TestLbl... outputs) {
        assertExcessOutputs(Arrays.asList(outputs));
    }

    void assertExcessOutputs(List<TestLbl> outputs) {
        List<TestLbl> actualOutputs = calculation.getOutputs(inventory);
        if (!outputs.equals(actualOutputs)) {
            throw new AssertionError("expectedOutputs = " + outputs + ", actualOutputs = " + actualOutputs);
        }
    }

    void assertCatalysts(TestLbl... outputs) {
        assertCatalysts(Arrays.asList(outputs));
    }

    void assertCatalysts(List<TestLbl> catalysts) {
        List<TestLbl> actualCatalysts = calculation.getCatalysts();
        if (!catalysts.equals(actualCatalysts)) {
            throw new AssertionError("expectedCatalysts = " + catalysts + ", actualCatalysts = " + actualCatalysts);
        }
    }

    void assertSteps(TestLbl... steps) {
        assertSteps(Arrays.asList(steps));
    }

    void assertSteps(List<TestLbl> steps) {
        List<TestLbl> actualSteps = calculation.getSteps();
        if (!steps.equals(actualSteps)) {
            throw new AssertionError("expectedSteps = " + steps + ", actualSteps = " + actualSteps);
        }
    }

    void assertMaxLoopTriggered() {
        assert maxLoopTriggered;
    }

    void printCalculation() {
        System.out.println("inputs: " + calculation.getInputs());
        System.out.println("excess outputs: " + calculation.getOutputs(inventory));
        System.out.println("catalysts: " + calculation.getCatalysts());
        System.out.println("steps: " + calculation.getSteps());
    }
}
