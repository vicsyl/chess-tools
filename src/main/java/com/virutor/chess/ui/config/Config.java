package com.virutor.chess.ui.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.virutor.chess.standard.PgnGame;


/**
 * The idea is to have here everything that could be persisted from run to run 
 * @author vaclav
 *
 */
public class Config {
	
	PgnGame lastGame;
	HumanPlayer humanPlayer;
	Map<String, UciEngine> uciEngines = new HashMap<String, UciEngine>();
	
	public PgnGame getLastGame() {
		return lastGame;
	}
	
	public HumanPlayer getHumanPlayer() {
		return humanPlayer;
	}
	
	public Map<String, UciEngine> getUciEngines() {
		return uciEngines;
	}
	
	
	
	/*
	private Player whitePlayer;
	private Player blackPlayer;
	private Position startingPosition;
	private List<TimeControl> timeControls;
	
	public List<TimeControl> getTimeControls() {
		return timeControls;
	}
	public void setTimeControls(List<TimeControl> timeControls) {
		this.timeControls = timeControls;
	
	}
	
	public Position getStartingPosition() {
		return startingPosition;
	}
	public void setStartingPosition(Position startingPosition) {
		this.startingPosition = startingPosition;
	}
	public Player getWhitePlayer() {
		return whitePlayer;
	}
	public void setWhitePlayer(Player whitePlayer) {
		this.whitePlayer = whitePlayer;
	}
	public Player getBlackPlayer() {
		return blackPlayer;
	}
	public void setBlackPlayer(Player blackPlayer) {
		this.blackPlayer = blackPlayer;
	}*/
	
}
