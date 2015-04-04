package com.virutor.chess.tournament;

import com.virutor.chess.ui.config.UciEngine;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.Position.Continuation;
import org.virutor.chess.standard.PgnDate;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.standard.PgnRound;
import org.virutor.chess.uci.UciEngineAgent;
import org.virutor.chess.uci.UciEngineAgent.State;
import org.virutor.chess.uci.UciProtocolException;
import org.virutor.chess.ui.model.UiGame;
import org.virutor.chess.ui.model.UiGameListener;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentManager implements UiGameListener {

    private static final Logger LOG = LogManager.getLogger(TournamentManager.class);
    private final TournamentData tournamentData;
    private final Map<UciEngine, UciEngineAgent> enginesToAgents = new HashMap<UciEngine, UciEngineAgent>();
    private final File resultsFile;
    private final TournamentEndListener tournamentEndListener;
    private final List<TournamentGame> tournamentGames;
    private int currentMatchIndex = -1;

    public TournamentManager(TournamentData tournamentData, TournamentEndListener tournamentEndListener) {
        this.tournamentData = tournamentData;
        this.tournamentEndListener = tournamentEndListener;
        tournamentGames = TournamentGame.getTournamentGames(tournamentData);
        resultsFile = new File("results_" + System.currentTimeMillis() + ".pgn");
    }

    public void start() {

        UiGame.instance.addListener(this);

        for (UciEngine uciEngine : tournamentData.getUciEngines()) {
            //Position.COLOR_OFF_BOARD - hack!!
            UciEngineAgent uciEngineAgent = new UciEngineAgent(Position.COLOR_OFF_BOARD, uciEngine.getPath(), uciEngine.getName());
            enginesToAgents.put(uciEngine, uciEngineAgent);
            UiGame.instance.addUciEngineAgent(uciEngineAgent);
        }
        nextMatch();

    }

    private void saveCurrentMatch() {
        try {
            PgnGame.saveToFile(resultsFile, UiGame.instance.getPgnGame());
        } catch (IOException e) {
            throw new RuntimeException("Cannot save the pgn game", e);
        }
    }

    private void nextMatch() {

        if(currentMatchIndex != -1) {
            //TODO fix it somewhere else
            UiGame.instance.getPgnGame().setResult(tournamentGames.get(currentMatchIndex).result);
            saveCurrentMatch();
        }
        currentMatchIndex++;

        if (currentMatchIndex == tournamentGames.size()) {
            LOG.info("Quitting...");
            UiGame.instance.quit();
            //TODO this doesn't work because agents still play (3 fold repetition for example) - what do we do?
            /*
            for(UciEngineAgent uciEngineAgent : enginesToAgents.values()) {
                UiGame.instance.removeUciEngineAgent(uciEngineAgent);
            }
            UiGame.instance.removeListener(this);
            */
            if(tournamentEndListener != null) {
                tournamentEndListener.onTournamentEnd(tournamentGames, resultsFile.getPath());
            }
            LOG.info("Tournament finished");
            return;
        }

        TournamentGame currentGame = tournamentGames.get(currentMatchIndex);

        setAgentColorAndStart(Position.COLOR_WHITE, currentGame.getWhite());
        setAgentColorAndStart(Position.COLOR_BLACK, currentGame.getBlack());

        UiGame.instance.getGameData().setTimeControls(tournamentData.getTimeControls());

        PgnGame pgnGame = new PgnGame(Game.newGameFromStartingPosition());
        pgnGame.setWhite(currentGame.getWhite().getName());
        pgnGame.setBlack(currentGame.getBlack().getName());
        pgnGame.setDate(new PgnDate(new Date()));
        pgnGame.setEvent("Engine tournament");
        pgnGame.setPgnRound(PgnRound.getInstance(String.valueOf(currentGame.getRound())));
        pgnGame.setStringProperty(PgnGame.PROPERTY_SITE, "Headless Virutor Arena");

        UiGame.instance.setPgnGame(pgnGame);

    }

    private void setAgentColorAndStart(byte color, UciEngine uciEngine) {
        UciEngineAgent uciEngineAgent = enginesToAgents.get(uciEngine);
        if (uciEngineAgent == null) {
            throw new RuntimeException("Uci engine not found: " + uciEngine);
        }
        uciEngineAgent.setColor(color);
        if (uciEngineAgent.getState() != State.STARTED) {
            try {
                uciEngineAgent.start();
            } catch (UciProtocolException ex) {
                LOG.error("One of the uci agent couldn't be started", ex);
                throw new RuntimeException("One of the uci agent couldn't be started", ex);
            }
        }
    }

    @Override
    public void onDoMove(Move move) {

        //FIXME have UiGame call generic change with type ResultChange
        Result result = UiGame.instance.getGame().getResult();
        if (result != null && result != Result.UNRESOLVED) {
            tournamentGames.get(currentMatchIndex).result = result;
            nextMatch();
        }

        //FIXME - move generator has to take care of this and inGenericChange -> ResultChange will be called
        Position.Continuation continuation = UiGame.instance.getGame().getCurrentGameNode().getGeneratedMoves().continuation;

        if (continuation != Continuation.POSSIBLE_MOVES) {
            byte colorToMove = UiGame.instance.getGame().getCurrentPosition().colorToMove;
            Result forcedResult = Continuation.getForcedResult(colorToMove, continuation);
            tournamentGames.get(currentMatchIndex).result = forcedResult;
            nextMatch();
        }
    }

    @Override
    public void onUndoMove(Move move) { }

    @Override
    public void onGenericChange(GameChangeType gameChangeType) {   }

}
