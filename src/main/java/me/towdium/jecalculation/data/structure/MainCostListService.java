package me.towdium.jecalculation.data.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;

public class MainCostListService extends AbstractCostListService<ILabel, Recipe, CostList> {

    private static final Dependencies<ILabel, Recipe> DEFAULT_DEPENDENCIES = new Dependencies<ILabel, Recipe>() {

        @Override
        public ILabel copyLabel(ILabel label) {
            return label.copy();
        }

        @Override
        public ILabel getEmptyLabel() {
            return ILabel.EMPTY;
        }

        @Override
        public long getLabelAmount(ILabel label) {
            return label.getAmount();
        }

        @Override
        public boolean isNotEmptyLabel(ILabel label) {
            return label != ILabel.EMPTY;
        }

        @Override
        public boolean labelMatches(ILabel self, ILabel that) {
            return self.matches(that);
        }

        @Override
        public Optional<ILabel> mergeLabels(ILabel a, ILabel b) {
            return ILabel.MERGER.merge(a, b);
        }

        @Override
        public ILabel multiplyLabel(ILabel label, float i) {
            return label.multiply(i);
        }

        @Override
        public ILabel setLabelAmount(ILabel label, long amount) {
            return label.setAmount(amount);
        }

        @Override
        public List<ILabel> getRecipeCatalyst(Recipe recipe) {
            return recipe.getCatalyst();
        }

        @Override
        public List<ILabel> getRecipeInput(Recipe recipe) {
            return recipe.getInput();
        }

        @Override
        public List<ILabel> getRecipeOutput(Recipe recipe) {
            return recipe.getOutput();
        }

        @Override
        public Optional<ILabel> recipeOutputMatches(Recipe recipe, ILabel label) {
            return recipe.matches(label);
        }

        @Override
        public long multiplier(Recipe recipe, ILabel label) {
            return recipe.multiplier(label);
        }

        @Override
        public Iterator<Recipe> recipeIterator() {
            return Controller.recipeIterator();
        }

        @Override
        public void addMaxLoopChatMessage() {
            Utilities.addChatMessage(Utilities.ChatMessage.MAX_LOOP);
        }
    };

    private static final CostLists<ILabel, CostList> DEFAULT_COST_LISTS = new CostLists<ILabel, CostList>() {

        @Override
        public CostList newCostList(List<ILabel> labels) {
            return new CostList(labels);
        }

        @Override
        public List<ILabel> getLabels(CostList costList) {
            return costList.getLabels();
        }

        @Override
        public void setLabels(CostList self, List<ILabel> labels) {
            self.labels = labels;
        }
    };

    // This MUST be defined after DEFAULT_DEPENDENCIES and DEFAULT_COST_LISTS.
    // Otherwise, you will get a NullPointerException!
    public static MainCostListService INSTANCE = new MainCostListService();

    private MainCostListService() {
        super(DEFAULT_DEPENDENCIES, DEFAULT_COST_LISTS, CostList.class);
    }

}
