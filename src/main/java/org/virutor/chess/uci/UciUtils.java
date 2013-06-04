package org.virutor.chess.uci;

import org.apache.commons.lang.NullArgumentException;
import org.virutor.chess.application.Services;
import org.virutor.chess.model.Position;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.ui.model.UiGame;

import com.virutor.chess.ui.config.Config;
import com.virutor.chess.ui.config.UciEngine;

public class UciUtils {

	private UciUtils() {}
	
	public static void uninstallEngine(String engineName) throws Exception  {

		
		Config config = Services.configStorage.getConfig();
		if(!config.getUciEngines().containsKey(engineName)) {
			throw new IllegalArgumentException("Unknown name"); 
		}
		if(!config.getUciEngines().get(engineName).isUninstallable()) {
			throw new IllegalArgumentException("Engine uninstallable");
		}
				
		removeEngine(engineName);

		
		config.getUciEngines().remove(engineName);
		Services.configStorage.saveConfig(config);
		
	}
	
	public static void removeEngine(String uciEngineAgentName) { 
		
		if(uciEngineAgentName == null) {
			throw new NullArgumentException("uciEngineAgentName");
		}

		UciEngineAgent agentToRemove = null;
		for(UciEngineAgent agent : UiGame.instance.getUciAgents()) {
			if(uciEngineAgentName.equals(agent.getName())) {
				agentToRemove = agent;
				break;
			}
		}		
		if(agentToRemove != null) {
			removeEngine(agentToRemove);
		}

		
	}

	
	public static void removeEngine(UciEngineAgent uciEngineAgent) {
		
		uciEngineAgent.quit();		
		UiGame.instance.removeUciEngineAgent(uciEngineAgent);		
		
	}
	
	public static boolean isEngineToMove() {

		byte colorToMove = UiGame.instance.getGame().getCurrentPosition().colorToMove;
		for(UciEngineAgent uciEngineAgent : UiGame.instance.getUciAgents()) {
			if(uciEngineAgent.getColor() == colorToMove) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isHumanPlaying() {
		
		//TODO dummy
		return whichEngineIsPlaying() == null;
	}
	
	public static boolean isEngineThinking() {
		
		//TODO dummy
		return whichEngineIsPlaying() != null;
	}
	
	public static UciEngineAgent whichEngineIsPlaying() {
		
		//TODO dummy
		
		for(UciEngineAgent uciEngineAgent : UiGame.instance.getUciAgents()) {
			if(uciEngineAgent.isThinking()) {
				return uciEngineAgent;
			}
		}
		return null;
	}
	
	
	//TODO think about setting the color
	public static void loadEngine(String name, byte color) throws UciProtocolException {
		
		Config config = Services.configStorage.getConfig();
		
		UciEngine uciEngine = config.getUciEngines().get(name);
		UciEngineAgent agent = new UciEngineAgent(color, uciEngine.getPath(), name);
		agent.start();
		UiGame.instance.addUciEngineAgent(agent);
		String property = color == Position.COLOR_WHITE ? PgnGame.PROPERTY_WHITE_TYPE : PgnGame.PROPERTY_BLACK_TYPE;
		UiGame.instance.getPgnGame().setStringProperty(property, PgnGame.PROPERTY_PLAYER_TYPE_PROGRAM);

		
	}
	
}
