package com.virutor.chess.ui.config;

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
	
	public HumanPlayer getHumanPlayer() {
		return humanPlayer;
	}
	
	public Map<String, UciEngine> getUciEngines() {
		return uciEngines;
	}

	public PgnGame getLastGame() {
		return lastGame;
	}

	public void setLastGame(PgnGame lastGame) {
		this.lastGame = lastGame;
	}

	public void setHumanPlayer(HumanPlayer humanPlayer) {
		this.humanPlayer = humanPlayer;
	}

	public void setUciEngines(Map<String, UciEngine> uciEngines) {
		this.uciEngines = uciEngines;
	}		
	
}
