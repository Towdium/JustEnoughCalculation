package me.towdium.jecalculation.algorithm;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.wrappers.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CostList {
    ArrayList<ILabel> labels;

    public CostList() {
        labels = new ArrayList<>();
    }

    public CostList(List<ILabel> labels) {
        this.labels = new ArrayList<>(labels);
    }

    public CostList(List<ILabel> positive, List<ILabel> negative) {
        labels = new ArrayList<>(positive);
        negative.forEach(i -> {
            ILabel l = i.copy();
            labels.add(l.invertAmount());
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    public CostList merge(CostList costList, boolean add) {
        costList.labels.forEach(i -> labels.add(add ? i.copy() : i.copy().invertAmount()));
        return cancel();
    }

    private CostList cancel() {
        ArrayList<ILabel> cancelled = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            for (int j = i + 1; j < labels.size(); j++) {
                Optional<Pair<ILabel, ILabel>> l = ILabel.MERGER.merge(labels.get(i), labels.get(j), true);
                if (l.isPresent()) {
                    labels.set(i, l.get().one);
                    labels.set(j, ILabel.EMPTY);
                    cancelled.add(l.get().two);
                }
            }
        }
        labels = labels.stream().filter(i -> i != ILabel.EMPTY).collect(Collectors.toCollection(ArrayList::new));
        return new CostList(cancelled);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CostList) {
            CostList c = (CostList) obj;
            CostList m = c.copy();
            m.merge(this, false);
            return m.labels.isEmpty();
        } else return false;
    }

    public CostList copy() {
        CostList ret = new CostList();
        ret.labels = labels.stream().map(ILabel::copy).collect(Collectors.toCollection(ArrayList::new));
        return ret;
    }

    public boolean isEmpty() {
        return labels.isEmpty();
    }

    public ArrayList<ILabel> getLabels() {
        return labels;
    }
}
