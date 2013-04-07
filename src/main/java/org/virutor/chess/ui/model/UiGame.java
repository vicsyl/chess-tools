package org.virutor.chess.ui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.GameNode;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.model.ui.GameData;
import org.virutor.chess.server.TimerController;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.standard.time.SuddenDeathTimeControl;
import org.virutor.chess.standard.time.TimeControl;
import org.virutor.chess.uci.GameServerTemp;
import org.virutor.chess.ui.model.UiGameListener.UiGameChange;


/**
 * TODO: synchronization
 * @author vaclav
 *
 */
public class UiGame implements GameServerTemp {
	
	private static final Logger LOG = Logger.getLogger(UiGame.class);

	public static UiGame instance = new UiGame();	
	
	//TODO align with pgnGame properties...
	private GameData gameData;

	public final TimerController TIMER_CONTROL_FOR_WHITE = new TimerController(Position.COLOR_WHITE);
	public final TimerController TIMER_CONTROL_FOR_BLACK = new TimerController(Position.COLOR_BLACK);
	

	private static GameData getDefaultData() {
		
		GameData gameData = new GameData();
		
		gameData.setBlack("Black Player...");
		gameData.setWhite("White Player...");
		gameData.setTimeControls(Arrays.<TimeControl>asList(new SuddenDeathTimeControl(6)));
		
		return gameData;
	}
	
	private List<UiGameListener> listeners = new ArrayList<UiGameListener>();

	//TODO go back to Game !!! 
	private PgnGame pgnGame = new PgnGame(null);	
			
	public GameData getGameData() {
		return gameData;
	}
	
	public void addListener(UiGameListener uiGameListener) {
		
			listeners.add(uiGameListener);
		
	} 
	
	private UiGame() {
		gameData = getDefaultData();
		addListener(TIMER_CONTROL_FOR_WHITE);
		addListener(TIMER_CONTROL_FOR_BLACK);		
	}
	
	
	/*
	//or use UiGameListener???
	private void setupTime() {
		TIMER_CONTROL_FOR_WHITE.resetTimeControl();
		TIMER_CONTROL_FOR_BLACK.resetTimeControl();

	}*/
	
	
	
	public void notifyListeners(UiGameChange change) {
		
		for(UiGameListener listener : listeners) {
			listener.onGenericChange(change);
		}
				
	}
	
	
	//TODO delegate / join thwo methods (play / doMove) 
	@Override
	public void play(LongAlgebraicMove laMove) throws InvalidMoveException {

		GeneratedMoves moves = pgnGame.getGame().getCurrentGameNode().getGeneratedMoves();
		for(Move move : moves.moves) {
			if(new LongAlgebraicMove(move).equals(laMove)) {
				pgnGame.getGame().doMove(move);
				return;
			}
		}
		throw new InvalidMoveException();
	}

	
	public void doMove(Move move) throws InvalidMoveException {
		
		pgnGame.getGame().doMove(move);
		
		
			for(UiGameListener listener : listeners) {
				listener.onDoMove(move);
			}
		
	}

	//move somewhere else???
	@Override
	public void notifyReady() {
		// TODO Auto-generated method stub
		
	}
	
	//TODO ??
	public PgnGame getPgnGame() {
		return pgnGame;
	}

	public void setPgnGame(PgnGame pgnGame) {
		this.pgnGame = pgnGame;
		notifyListeners(UiGameChange.getCompleteChange());
	}


	public Game getGame() {
		return pgnGame.getGame();
	}

	public void setGame(Game game) {
		this.pgnGame.setGame(game);
		notifyListeners(UiGameChange.getCompleteChange());
	}
	
	public void setNode(GameNode gameNode) {
		pgnGame.getGame().setCurrentGameNode(gameNode);
		notifyListeners(UiGameChange.getCompleteChange());
	}

}
