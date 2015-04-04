package com.virutor.chess.tournament;

import java.util.List;

public interface TournamentEndListener {
    void onTournamentEnd(List<TournamentGame> tournamentGames, String resultsFileName);
}
