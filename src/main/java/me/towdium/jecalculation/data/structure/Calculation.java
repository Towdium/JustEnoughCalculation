package me.towdium.jecalculation.data.structure;

import java.util.Collections;
import java.util.List;

public interface Calculation<LabelT> {

    List<LabelT> getCatalysts();

    List<LabelT> getInputs();

    List<LabelT> getOutputs(List<LabelT> ignore);

    default List<LabelT> getSteps() {
        return getSteps(Collections.emptyList());
    }

    List<LabelT> getSteps(List<LabelT> startingInventory);
}
