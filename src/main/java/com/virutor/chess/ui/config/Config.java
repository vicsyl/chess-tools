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
	
	public PgnGame getLastGame() {
		return lastGame;
	}
	
	public HumanPlayer getHumanPlayer() {
		return humanPlayer;
	}
	
	public Map<String, UciEngine> getUciEngines() {
		return uciEngines;
	}
		
}
