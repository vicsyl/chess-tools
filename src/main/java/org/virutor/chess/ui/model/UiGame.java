package org.virutor.chess.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.GameNode;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.Position.Continuation;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.model.ui.GameData;
import org.virutor.chess.server.TimerController;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.uci.GameServerTemp;
import org.virutor.chess.uci.InfoListener;
import org.virutor.chess.uci.UciEngineAgent;


/**
 * TODO: synchronization
 * @author vaclav
 *
 */
public class UiGame implements GameServerTemp {
	
	private static final Logger LOG = Logger.getLogger(UiGame.class);

	public static UiGame instance = new UiGame();	
	
	//TODO not very clean
	public boolean shouldAgentsPlayOnGenericChange = true;
	
	public boolean shouldAgentPlay(UciEngineAgent uciEngineAgent) {
		return getGame().getCurrentPosition().colorToMove == uciEngineAgent.getColor() &&
			   getGame().getCurrentGameNode().getGeneratedMoves().continuation == Continuation.POSSIBLE_MOVES; 
		
	}	
	
	//TODO align with pgnGame properties...
	private GameData gameData = new GameData();

	public final TimerController TIMER_CONTROL_FOR_WHITE = new TimerController(Position.COLOR_WHITE);
	public final TimerController TIMER_CONTROL_FOR_BLACK = new TimerController(Position.COLOR_BLACK);
	
	
	private List<UiGameListener> listeners = new ArrayList<UiGameListener>();
	private List<InfoListener> infoListeners = new ArrayList<InfoListener>();

	private List<UciEngineAgent> uciAgents = new ArrayList<UciEngineAgent>();
	
	public void addUciEngineAgent(UciEngineAgent uciEngineAgent) {
		if(!uciAgents.isEmpty()) {
			for(UciEngineAgent uciAgent : uciAgents) {
				uciAgent.quit();
			}
		}		
		uciAgents.add(uciEngineAgent);
		listeners.add(2, uciEngineAgent);
	}
	
	public void removeUciEngineAgent(UciEngineAgent uciEngineAgent) {
		uciAgents.remove(uciEngineAgent);
		listeners.remove(uciEngineAgent);
	}
	
	public List<UciEngineAgent> getUciAgents() {
		return Collections.unmodifiableList(uciAgents);
	}

	public void addInfoListener(InfoListener infoListener) {
		infoListeners.add(infoListener);
	}

	public List<InfoListener> getInfoListeners() {
		return Collections.unmodifiableList(infoListeners);
	}

	//TODO go back to Game !!! 
	private PgnGame pgnGame = new PgnGame(null);	
			
	public GameData getGameData() {
		return gameData;
	}
	
	public void addListener(UiGameListener uiGameListener) {
		
			listeners.add(uiGameListener);
		
	} 
	
	private UiGame() {		
		
		/**
		 * a) not compatible with android
		 * b) not working for defaultData() ?
		 */
		addListener(TIMER_CONTROL_FOR_WHITE);
		addListener(TIMER_CONTROL_FOR_BLACK);		
	}
	
	
	/*
	//or use UiGameListener???
	private void setupTime() {
		TIMER_CONTROL_FOR_WHITE.resetTimeControl();
		TIMER_CONTROL_FOR_BLACK.resetTimeControl();

	}*/
	
	
	
	public void notifyListeners(UiGameListener.GameChangeType change) {
		
		for(UiGameListener listener : listeners) {
			listener.onGenericChange(change);
		}
				
	}
	
	public void notifyListenersButAgents(UiGameListener.GameChangeType change) {
		
		for(UiGameListener listener : listeners) {
			if(!(listener instanceof UciEngineAgent)) {
				listener.onGenericChange(change);
			}
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
		
		//TODO again it's necessary to check all exceptions that can interrupt the course of the game
		for(UiGameListener listener : listeners) {
			try {
				listener.onDoMove(move);
			} catch(Exception e) {
				e.printStackTrace();
			} 
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
		notifyListeners(UiGameListener.GameChangeType.COMPLETE_CHANGE);
	}


	public Game getGame() {
		return pgnGame.getGame();
	}

	public void setGame(Game game) {
		this.pgnGame.setGame(game);
		notifyListeners(UiGameListener.GameChangeType.COMPLETE_CHANGE);
	}
	
	public void setNode(GameNode gameNode) {
		pgnGame.getGame().setCurrentGameNode(gameNode);
		notifyListeners(UiGameListener.GameChangeType.COMPLETE_CHANGE);
	}

}
