package com.virutor.chess.ui.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.standard.PgnGameSuite;

public class DefaultConfigStorageImpl extends ConfigStorage {

	private static final Logger LOG = LogManager.getLogger(DefaultConfigStorageImpl.class);

	private static Config CONFIG = null;
	
	
	/*
	private static final String VIRUTOR_PATH = "C:\\devel\\C++\\EclipseCppWorkspace\\git_clone\\ChessCppSvn\\bin\\Debug\\VirutorChessUci_1.1.exe";
	private static final String RYBKA_PATH = "C:\\Program Files\\Arena\\Engines\\Rybka\\Rybka v2.2n2.mp.w32.exe";
	*/
	
	private static final String ENGINE_PLAYERS_PROPS_PATH_FILE = "config/EnginesPlayers.txt";
	private static final String LAST_GAME_PATH_FILE = "config/LastGame.pgn";
	
	private static final String PLAYER_KEY = "player";
	private static final String ENGINE_KEY = "engine";

	//well maybe this still comes in handy for super fallback	
	Config getDefaultDefaultConfig() {
		
		return null;
		
		/*
		Properties enginesPlayersProps = loadProperties(ENGINE_PLAYERS_PROPS_PATH);
		Properties gameProps = loadProperties(GAME_PROPS_PATH);
		
		Config ret = new Config();
		
		Player white = new Player();
		white.setHuman(true);
		white.setName("Vasa Hardcoded");
	
		Player black = new Player();
		black.setName(" unknown virutor ");
		black.setHuman(false);
		
		//TODO !!!! - also emply finally relative path!!!
		black.setUciPath(RYBKA_PATH);
		
		ret.setWhitePlayer(white);
		ret.setBlackPlayer(black);
		
		ret.setStartingPosition(Position.getStartPosition());
		ret.setTimeControls(Arrays.<TimeControl>asList(new SuddenDeathTimeControl(300)));
		
		return ret;
		*/
		
	}
	
	@Override
	public Config getConfig() {
		
		if(CONFIG != null) {
			return CONFIG;
		}
		
		CONFIG = new Config();
		
		try {
			 PgnGameSuite pgnGameSuite = PgnGame.parse(new File(LAST_GAME_PATH_FILE));
			 if(CollectionUtils.isEmpty(pgnGameSuite.pgnGames)) {
				 throw new IllegalArgumentException("no games found");
			 }
			 CONFIG.lastGame = pgnGameSuite.pgnGames.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MultiProperties multiProperties = new MultiProperties(ENGINE_PLAYERS_PROPS_PATH_FILE);
		
		Map<String, List<Map<String, String>>> data = multiProperties.load();
		if(data == null) {
			//TODO 
			return CONFIG;
		}
		
		if(CollectionUtils.isEmpty(data.get(PLAYER_KEY))) {
			//TODO 
			//return ret;			
		} else {
			CONFIG.humanPlayer = getHumanPlayer(data.get(PLAYER_KEY).get(0));
		}
		
		if(CollectionUtils.isEmpty(data.get(ENGINE_KEY))) {
			//no engines
			//TODO			
		} else {
			CONFIG.uciEngines = getUciEngines(data.get(ENGINE_KEY));
		}
		
		return CONFIG;
		
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
		
		LOG.warn("Not implemented");
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
	
	private File getAppDataFolder() {
		
		String path = System.getenv("APPDATA");
		if(StringUtils.isBlank(path)) {
			//TODO problem;
		}
		
		File file = new File(path);
		
		if(file.exists() && file.isFile()) {
			//TODO problem
		}
		
		if(!file.exists()) {
			//TODO copy default config
		}
		
		return file;
		
	}
	
	
	
}
