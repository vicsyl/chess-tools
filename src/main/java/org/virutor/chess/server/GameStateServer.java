package org.virutor.chess.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;
import org.virutor.java.lang.Pair;

public class GameStateServer {

	private static GameStateServer instance = null;
	private static Object instanceLock = new Object();
	
	public static GameStateServer getInstance() {
		if(instance == null) {
			synchronized(instanceLock) {
				if(instance == null) {
					instance = new GameStateServer();
				}
			}			
		}
		return instance;
	} 
	
	//game
	private List<Position> positions;
	
	//TODO remove!!! 
	private boolean isFirstStartPosition;
	
	private List<Move> moves;
	private GeneratedMoves generatedMoves;
	private ServerGame game = new ServerGame();
	//end game
	
	public ServerGame getGame() {
		return game;
	}

	private Object lock = new Object();
	private Player[] players = new Player[2];
	private List<GameStateChangeListener> gameStateChanageListeners = new Vector<GameStateChangeListener>();

	
	
	//public boolean setPositions(List<Position> position) {}
	
	private GameStateServer() {
		init();		
	}
	
	public void setPositionsAndMoves(List<Position> positions, List<Move> moves) {
		synchronized (lock) {
			this.positions = positions;
			this.moves = moves;
			//TODO is first start position??
			updateGeneratedMoves();
		}
	}
	
	@Deprecated
	private void init() {
		positions = new ArrayList<Position>();
		moves = new ArrayList<Move>();
	}
	
	//TODO gradually isolate GameState....
	

	public synchronized Position getLastPosition() {
		if(positions != null && positions.size() != 0) {
			return positions.get(positions.size() - 1);
		}
		return null;
	}
	
	public List<Position> getPositions() {
		synchronized (lock) {
			return new ArrayList<Position>(positions);
		}
	}
	
	public Pair<List<Position>, List<Move>> getState() {
		synchronized (lock) {
			return new Pair<List<Position>, List<Move>>(new ArrayList<Position>(positions), new ArrayList<Move>(moves));			
		}
	}

	public boolean isFirstStartPosition() {
		return isFirstStartPosition;
	}

	public synchronized List<Move> getMoves() {
		return new ArrayList<Move>(moves);
	}

	//TODO synchronization??
	public synchronized List<Move> getGeneratedMoves() {
		return new ArrayList<Move>(generatedMoves.moves);		
	}

	
	public void addGameStateChangeListener(GameStateChangeListener listener) { 
		gameStateChanageListeners.add(listener);
	}
	public boolean removeGameStateChangeListener(GameStateChangeListener listener) {
		return gameStateChanageListeners.remove(listener);
	}
	
	
	private void updateGeneratedMoves() {
		synchronized (lock) {
				generatedMoves = MoveGenerator.generateMoves(positions.get(positions.size() - 1));	
		}
	}
	
	public boolean doMove(Move move, Player player) {
		
		synchronized (lock) {
			int index = generatedMoves.moves.indexOf(move);
			if(index == -1) {
				return false;
			}
			moves.add(move);
			positions.add(generatedMoves.position.get(index));
			updateGeneratedMoves();
			//generatedMoves = MoveGenerator.generateMoves(generatedMoves.position.get(index));
			game.startTimeForColor(positions.get(positions.size()-1).colorToMove);
		}

		notify(player, move);
		return true;
	}
	
	private void notify(Player exceptPlayer, Move move) {
		for(Player thisPlayer : players) {
			if(thisPlayer != exceptPlayer) {
				thisPlayer.moveDone(move);
			}
		}
		for(GameStateChangeListener listener : gameStateChanageListeners) {
			listener.moveDone(move);
		}
	}
	
	private void notifyChange() {
		for(Player thisPlayer : players) {
				thisPlayer.notifyChange();
		}
		for(GameStateChangeListener listener : gameStateChanageListeners) {
			listener.notifyChange();
		}
	}
	
	public void setPosition(Position position) {
		setPosition(position, false);
	}
	
	public void setStartPosition() {
		setPosition(new Position().setStartPosition(), true);
	}
	
	/**
	 * NOTE: means set FIRST position!!!
	 * @param position
	 * @param isStart
	 */
	private void setPosition(Position position, boolean isStart) {

		synchronized(lock) {
			isFirstStartPosition = isStart;
			positions.clear();
			moves.clear();
			positions.add(position);
			generatedMoves = MoveGenerator.generateMoves(position);
			game.restart();
			stop();
		}
		notifyChange();
		
	}
	
	public void setPlayer(int color, Player player) {
		//TODO assert (color : 0-1)
		players[color] = player;
	}
	
	public void stop() {
		for(Player player : players) {
			if(player != null && player.isStarted()) {
				player.stop();
			}
		}
	}
	
	public void start() {
		if(players[0] != null) {
			players[0].start();
		} 
		if(players[1] != null) {
			players[1].start();
		} 
		
		System.out.println("Both players started");
		
		boolean player0Started = false;
		boolean player1Started = false;
		
		//TODO timeout
		while(!player0Started || !player1Started) {
			if(!player0Started) {
				player0Started = players[0].isStarted();
			} 
			if(!player1Started) {
				player1Started = players[1].isStarted();
			}
		}
		
		/*
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		players[positions.get(positions.size()-1).colorToMove].play();
		
		//TODO
	}
	
	
	
}
