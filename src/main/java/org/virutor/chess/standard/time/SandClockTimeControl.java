package org.virutor.chess.standard.time;

public class SandClockTimeControl implements TimeControl {

    private final int sandclockSeconds;

    public SandClockTimeControl(int sandclockSeconds) {
        this.sandclockSeconds = sandclockSeconds;
    }

    public int getSandclockSeconds() {
        return sandclockSeconds;
    }

    @Override
    public long getTotalFirstTime() {
        return sandclockSeconds * 1000;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SandClockTimeControl that = (SandClockTimeControl) o;

        if (sandclockSeconds != that.sandclockSeconds) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return sandclockSeconds;
    }

    @Override
    public String toString() {
        return "SandClockTimeControl{" +
                "sandclockSeconds=" + sandclockSeconds +
                '}';
    }
}
