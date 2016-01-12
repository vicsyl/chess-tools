package org.virutor.chess.standard.time;

public class MovesInSecondsTimeControl implements TimeControl {

    private final int moves;
    private final int seconds;

    public MovesInSecondsTimeControl(int moves, int seconds) {
        this.moves = moves;
        this.seconds = seconds;
    }

    public int getMoves() {
        return moves;
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

        MovesInSecondsTimeControl that = (MovesInSecondsTimeControl) o;

        if (moves != that.moves) {
            return false;
        }
        if (seconds != that.seconds) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = moves;
        result = 31 * result + seconds;
        return result;
    }

    @Override
    public String toString() {
        return "MovesInSecondsTimeControl{" +
                "moves=" + moves +
                ", seconds=" + seconds +
                '}';
    }
}
