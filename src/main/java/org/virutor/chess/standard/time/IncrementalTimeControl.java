package org.virutor.chess.standard.time;

public class IncrementalTimeControl implements TimeControl {

    private final int base;
    private final int increment;

    public IncrementalTimeControl(int base, int increment) {
        this.base = base;
        this.increment = increment;
    }

    public int getBase() {
        return base;
    }

    public int getIncrement() {
        return increment;
    }

    @Override
    public long getTotalFirstTime() {
        return base * 1000;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IncrementalTimeControl that = (IncrementalTimeControl) o;

        if (base != that.base) {
            return false;
        }
        if (increment != that.increment) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = base;
        result = 31 * result + increment;
        return result;
    }

    @Override
    public String toString() {
        return "IncrementalTimeControl{" +
                "base=" + base +
                ", increment=" + increment +
                '}';
    }
}
