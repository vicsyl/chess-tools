package org.virutor.chess.config;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.standard.PgnGameSuite;
import org.virutor.chess.ui.model.UiGame;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DefaultConfigStorageImpl extends ConfigStorage {

	private static final Logger LOG = LogManager.getLogger(DefaultConfigStorageImpl.class);

		
	private static final String ENGINE_PLAYERS_PROPS_PATH_FILE = "config/EnginesPlayers.txt";
	private static final String LAST_GAME_PATH_FILE = "config/LastGame.pgn";
	
	private static final String PLAYER_KEY = "player";
	private static final String ENGINE_KEY = "engine";

	
	private PgnGame getLastPgnGame() throws IOException {
		PgnGameSuite pgnGameSuite = PgnGame.parse(new File(LAST_GAME_PATH_FILE));
		if(CollectionUtils.isEmpty(pgnGameSuite.pgnGames)) {
			throw new IllegalArgumentException("no games found");
		}
		return pgnGameSuite.pgnGames.get(0);
	}
	
	@Override
	public Config getConfig() {
		
		Config ret = new Config();
		
		try {			 
			ret.lastGame = getLastPgnGame();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MultiProperties multiProperties = new MultiProperties(ENGINE_PLAYERS_PROPS_PATH_FILE);
		
		Map<String, List<Map<String, String>>> data = multiProperties.load();
		if(data == null) {
			//TODO 
			return ret;
		}
		
		if(CollectionUtils.isEmpty(data.get(PLAYER_KEY))) {
			//TODO 
			//return ret;			
		} else {
			ret.humanPlayer = getHumanPlayer(data.get(PLAYER_KEY).get(0));
		}
		
		if(CollectionUtils.isEmpty(data.get(ENGINE_KEY))) {
			//no engines
			//TODO			
		} else {
			ret.uciEngines = getUciEngines(data.get(ENGINE_KEY));
		}
		
		return ret;
		
	}

	private HumanPlayer getHumanPlayer(Map<String, String> props) {
		
		HumanPlayer humanPlayer = new HumanPlayer();		
		humanPlayer.setName(props.get(PLAYER_KEY));
		//TODO check for null ??
		return humanPlayer;
	}
	
	private Map<String, UciEngine> getUciEngines(List<Map<String, String>> props) {
		
		Map<String, UciEngine> ret = new HashMap<String, UciEngine>();
		
		for(Map<String, String> map : props) {
			
			UciEngine uciEngine = new UciEngine();
			uciEngine.properties = map;
			
			if(uciEngine.getName() == null || uciEngine.getPath() == null) {
				//TODO
				continue;
			}
			ret.put(uciEngine.getName(), uciEngine);
		}
		
		return ret;
	}
	
	@Override
	public void saveConfig(Config config) throws Exception {
		
		//will now save the config part only (i.e. not the last game)
		
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(ENGINE_PLAYERS_PROPS_PATH_FILE));
			writer.write("player=" + config.humanPlayer.getName() + "\n");
			for(UciEngine uciEngine : config.uciEngines.values()) {
				for(Map.Entry<String, String> entry : uciEngine.properties.entrySet()) {
					writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
				}
			}
		
		} finally {
			IOUtils.closeQuietly(writer);
		}
		
		
	}
	
	private Properties loadProperties(String path) {
		
		Properties properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
		
	} 
	
	@Override
	public void saveLastGame() throws Exception {

		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(LAST_GAME_PATH_FILE));
			writer.write(UiGame.instance.getPgnGame().format());
		
		} finally {
			IOUtils.closeQuietly(writer);
		}
		
	}
	
	
	
}
