package org.virutor.chess.tournament;

import com.google.common.base.Preconditions;
import org.virutor.chess.config.UciEngine;
import org.apache.commons.collections.CollectionUtils;
import org.virutor.chess.standard.time.TimeControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO - uci options
 * TODO - opening scheme
 * TODO - other options
 */
public class TournamentData {

    public static class Builder {

        private List<TimeControl> timeControls;
        private List<UciEngine> uciEngines;
        private int roundCount;

        public Builder setUciEngines(List<UciEngine> uciEngineOrder) {
            this.uciEngines = new ArrayList<UciEngine>(uciEngineOrder);
            return this;
        }

        public Builder setTimeControls(List<TimeControl> timeControls) {
            this.timeControls = timeControls;
            return this;
        }

        public Builder setRoundCount(int gamesCount) {
            this.roundCount = gamesCount;
            return this;
        }

        public TournamentData build() {
            Preconditions.checkNotNull(timeControls);
            Preconditions.checkState(CollectionUtils.isNotEmpty(uciEngines));
            Preconditions.checkState(CollectionUtils.isNotEmpty(timeControls));
            return new TournamentData(timeControls, uciEngines, roundCount);
        }

    }

    public static Builder createBuilder() {
        return new Builder();
    }

    private List<TimeControl> timeControls;
    private List<UciEngine> uciEngines;
    private int roundCount;

    public TournamentData(List<TimeControl> timeControls, List<UciEngine> uciEngines, int roundCount) {
        this.timeControls = timeControls;
        this.uciEngines = uciEngines;
        this.roundCount = roundCount;
    }

    public List<TimeControl> getTimeControls() {
        return Collections.unmodifiableList(timeControls);
    }

    public int getRoundCount() {
        return roundCount;
    }

    public List<UciEngine> getUciEngines() {
        return Collections.unmodifiableList(uciEngines);
    }
}
