package me.towdium.jecalculation.data.structure;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;

// positive => generate; negative => require
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CostList {

    List<ILabel> labels;

    // External code should probably be calling MainCostListService.INSTANCE.newPosCostList()
    // instead of calling this directly
    CostList(List<ILabel> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object obj) {
        return MainCostListService.INSTANCE.costListEquals(this, obj);
    }

    public List<ILabel> getLabels() {
        return labels;
    }

    public Calculation<ILabel> calculate() {
        return MainCostListService.INSTANCE.calculate(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (ILabel i : labels) hash ^= i.hashCode();
        return hash;
    }
}
