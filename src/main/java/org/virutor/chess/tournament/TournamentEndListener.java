package org.virutor.chess.tournament;

import java.util.List;


/**
 * TODO allow for cancellation
 */
@FunctionalInterface
public interface TournamentEndListener {
    void onTournamentEnd(List<TournamentGame> tournamentGames, String resultsFileName);
}
