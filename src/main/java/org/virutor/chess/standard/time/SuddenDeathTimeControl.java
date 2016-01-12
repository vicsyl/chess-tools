package org.virutor.chess.standard.time;

public class SuddenDeathTimeControl implements TimeControl {

    private final int seconds;

    public SuddenDeathTimeControl(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public long getTotalFirstTime() {
        return seconds * 1000;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SuddenDeathTimeControl that = (SuddenDeathTimeControl) o;

        if (seconds != that.seconds) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return seconds;
    }

    @Override
    public String toString() {
        return "SuddenDeathTimeControl{" +
                "seconds=" + seconds +
                '}';
    }
}
