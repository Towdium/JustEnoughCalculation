package me.towdium.jecalculation.data.structure;

import java.util.Objects;

class TestLbl implements Cloneable {

    final String name;
    long amount;

    TestLbl(String name, long amount) {
        this.name = name;
        this.amount = amount;
    }

    @Override
    protected TestLbl clone() {
        return new TestLbl(this.name, this.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestLbl)) return false;
        TestLbl testLbl = (TestLbl) o;
        return amount == testLbl.amount && Objects.equals(name, testLbl.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount);
    }

    @Override
    public String toString() {
        return "{" + amount + " " + name + "}";
    }

    static TestLbl lbl(String name) {
        return lbl(name, 1);
    }

    static TestLbl lbl(String name, int count) {
        return new TestLbl(name, count);
    }
}
