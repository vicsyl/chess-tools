package com.virutor.chess.ui.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.virutor.chess.model.Position;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.standard.PgnTimeControlHandler;
import org.virutor.chess.standard.time.SuddenDeathTimeControl;
import org.virutor.chess.standard.time.TimeControl;
import org.virutor.chess.uci.UciEngineAgent;
import org.virutor.chess.uci.UciProtocolException;
import org.virutor.chess.ui.model.UiGame;

public abstract class ConfigStorage {

	private static final String UNKNOWN_PLAYER = "UNKNOWN_PLAYER"; 
	
	public abstract Config getConfig();
	public abstract void saveConfig(Config config) throws Exception;
	
	
	private List<TimeControl> getDefaultTimeControls() {
		return Arrays.<TimeControl>asList(new SuddenDeathTimeControl(600));
	}
	
	public void apply(Config config) {
		
		List<TimeControl> timeControls = (List<TimeControl>)config.lastGame.getProperties().get(PgnTimeControlHandler.TIME_CONTROL);
		if(CollectionUtils.isEmpty(timeControls)) {
			timeControls = getDefaultTimeControls();
		}		
		UiGame.instance.getGameData().setTimeControls(timeControls);		
		UiGame.instance.getGameData().setWhite(config.lastGame.getWhite());		
		UiGame.instance.getGameData().setBlack(config.lastGame.getBlack());

		String whiteType = (String)config.lastGame.getProperties().get(PgnGame.PROPERTY_WHITE_TYPE);
		applyPlayer(config, whiteType, config.lastGame.getWhite(), Position.COLOR_WHITE);
		String blackType = (String)config.lastGame.getProperties().get(PgnGame.PROPERTY_BLACK_TYPE);
		applyPlayer(config, blackType, config.lastGame.getBlack(), Position.COLOR_BLACK);

		UiGame.instance.setPgnGame(config.lastGame);
		
	}
	
	private void applyPlayer(Config config, String type, String name, byte color) {

		 
		if(PgnGame.PROPERTY_PLAYER_TYPE_HUMAN.equals(type)) {
			if(UNKNOWN_PLAYER.equals(name)) {
				//TODO 
			}			
		} else if(PgnGame.PROPERTY_PLAYER_TYPE_PROGRAM.equals(type)) {
			
			if(!config.uciEngines.containsKey(name)) {				
				//TODO unknown engine
			} else {
				UciEngine uciEngine = config.uciEngines.get(name);
				UciEngineAgent agent = new UciEngineAgent(color, uciEngine.getPath());
				UiGame.instance.addUciEngineAgent(agent);
				
				
				/**
				 * TODO 
				 * a) centralize 
				 * b) do not do this here -> there are other places (com.virutor.chess.ui.components.actions.EngineActions)
				 * 			that'd start the agent and we have to listen to the name centrally
				 */
				try {
					agent.start();
				} catch (UciProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
		
			}
			
		} else {			
			//unknown type
		}
		

		/*
		String name = player.getName();
		
		
		
		if(!player.isHuman()) {
			
			UciEngineAgent uciEngineAgent = new UciEngineAgent(color, player.getUciPath());
			UiGame.instance.addUciEngineAgent(uciEngineAgent);
			
			
			
			name = uciEngineAgent.getEngineInfo().name;
		}  
		

		if(color == Position.COLOR_WHITE) {
 			UiGame.instance.getGameData().setWhite(name);
		} else {
			UiGame.instance.getGameData().setBlack(name);			
		}
 		*/
	}
	
}
