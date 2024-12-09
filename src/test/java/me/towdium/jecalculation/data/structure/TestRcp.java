package me.towdium.jecalculation.data.structure;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

class TestRcp {

    final List<TestLbl> outputs;
    final List<TestLbl> catalysts;
    final List<TestLbl> inputs;

    TestRcp(List<TestLbl> outputs, List<TestLbl> catalysts, List<TestLbl> inputs) {
        this.outputs = Collections.unmodifiableList(outputs);
        this.catalysts = Collections.unmodifiableList(catalysts);
        this.inputs = Collections.unmodifiableList(inputs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestRcp)) return false;
        TestRcp testRcp = (TestRcp) o;
        return Objects.equals(outputs, testRcp.outputs) && Objects.equals(catalysts, testRcp.catalysts)
            && Objects.equals(inputs, testRcp.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputs, catalysts, inputs);
    }

    static TestRcp rcp(List<TestLbl> outputs, List<TestLbl> catalysts, List<TestLbl> inputs) {
        return new TestRcp(outputs, catalysts, inputs);
    }

    static TestRcp rcp(List<TestLbl> outputs, List<TestLbl> inputs) {
        return new TestRcp(outputs, Collections.emptyList(), inputs);
    }
}
