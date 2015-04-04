package com.virutor.chess.tournament;

import com.virutor.chess.ui.config.UciEngine;
import org.virutor.chess.model.Game.Result;

import java.util.ArrayList;
import java.util.List;

public class TournamentGame {

    final UciEngine white;
    final UciEngine black;
    int round;
    Result result = Result.UNRESOLVED;

    public TournamentGame(UciEngine white, UciEngine black, int round) {
        this.white = white;
        this.black = black;
        this.round = round;
    }

    public UciEngine getWhite() {
        return white;
    }

    public UciEngine getBlack() {
        return black;
    }

    public int getRound() {
        return round;
    }

    public Result getResult() {
        return result;
    }

    public static List<TournamentGame> getTournamentGames(TournamentData tournamentData) {

        List<UciEngine> uciEngines = tournamentData.getUciEngines();
        int enginesCount = uciEngines.size();
        List<TournamentGame> tournamentGames = new ArrayList<TournamentGame>();

        for (int round = 1; round <= tournamentData.getRoundCount(); round++) {
            for (int i = 0; i < enginesCount - 1; i++) {
                for (int j = i + 1; j < enginesCount; j++) {
                    UciEngine engineWhite = round % 2 == 1 ? uciEngines.get(i) : uciEngines.get(j);
                    UciEngine engineBlack = round % 2 == 1 ? uciEngines.get(j) : uciEngines.get(i);
                    TournamentGame game = new TournamentGame(engineWhite, engineBlack, round);
                    tournamentGames.add(game);
                }
            }
        }

        return tournamentGames;
    }
}
