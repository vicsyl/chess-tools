package org.virutor.chess.standard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.model.Game.ResultExplanation;
import org.virutor.chess.model.GameNode;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.ui.GameData;
import org.virutor.chess.standard.time.TimeControl;

/**
 * TODO check Game for not null 
 * @author vaclav
 *
 */
public class PgnGame {

	private static final Logger LOG = Logger.getLogger(PgnGame.class); 
	
	static final Map<String, Game.Result> STRING_RESULTS = new HashMap<String, Game.Result>();
	static final Map<Result, String> RESULT_STRING = new HashMap<Game.Result, String>();


	
	
	private static final Map<String, PropertyHandler> PROPERTY_HANDLERS = new HashMap<String, PropertyHandler>();
	public static final List<String> SEVEN_TAG_ROOSTER_PROPERTY_NAMES;

	public static final String PROPERTY_EVENT = "Event";
	public static final String PROPERTY_SITE = "Site";
	public static final String PROPERTY_DATE = "Date";
	public static final String PROPERTY_ROUND = "Round";
	public static final String PROPERTY_BLACK = "Black";
	public static final String PROPERTY_WHITE = "White";
	public static final String PROPERTY_RESULT = "Result";
	public static final String PROPERTY_WHITE_TYPE = "WhiteType";
	public static final String PROPERTY_BLACK_TYPE = "BlackType";
	public static final String PROPERTY_PLAYER_TYPE_HUMAN = "human";
	public static final String PROPERTY_PLAYER_TYPE_PROGRAM = "program";
		
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\[\\s*(\\w+)\\s+\"(.*)\"\\]");

	static {

		List<String> sevenTagRooster = new ArrayList<String>();
		
		sevenTagRooster.addAll(Arrays.asList(				
				PROPERTY_EVENT,
				PROPERTY_SITE,
				PROPERTY_DATE,
				PROPERTY_ROUND,
				PROPERTY_WHITE,
				PROPERTY_BLACK,
				PROPERTY_RESULT));
		
		SEVEN_TAG_ROOSTER_PROPERTY_NAMES = Collections.unmodifiableList(sevenTagRooster);
		
		STRING_RESULTS.put("1-0", Game.Result.WHITE_WINS);
		STRING_RESULTS.put("0-1", Game.Result.BLACK_WINS);
		STRING_RESULTS.put("1/2-1/2", Game.Result.DRAW);
		STRING_RESULTS.put("*", Game.Result.UNRESOLVED);
		
		RESULT_STRING.put(Result.WHITE_WINS, "1-0");
		RESULT_STRING.put(Result.BLACK_WINS, "0-1");
		RESULT_STRING.put(Result.DRAW, "1/2-1/2");
		RESULT_STRING.put(Result.UNRESOLVED, "*");
		
		PROPERTY_HANDLERS.put(PgnTimeControlHandler.TIME_CONTROL, PgnTimeControlHandler.INSTANCE);
		PROPERTY_HANDLERS.put(PROPERTY_RESULT, ResultHandler.INSTANCE);
		PROPERTY_HANDLERS.put(PROPERTY_DATE, new DateHandler());
		PROPERTY_HANDLERS.put(PROPERTY_ROUND, new PgnRoundHandler());
		PROPERTY_HANDLERS.put(SetUpHandler.SET_UP, SetUpHandler.INSTANCE);
		PROPERTY_HANDLERS.put(FenPropertyHandler.FEN, FenPropertyHandler.INSTANCE);
	}
	
	private Game game;
	private GameData gameData;
	Map<String, Object> properties = new HashMap<String, Object>();
	
	//seven tag rooster
	private PgnDate pgnDate; 
	private PgnRound pgnRound;
	//result delegated to game.result
	
	public void setDefaultSevenTagRoosterProperty() {
		setBlack("Black");
		setWhite("White");
		setDate(new PgnDate(new Date()));
		setEvent("?");
		setStringProperty(PgnGame.PROPERTY_SITE, "?");
		setResult(Result.UNRESOLVED);
		setPgnRound(PgnRound.getInstance("1"));
	}
	
	public void setGame(Game game) {
		this.game = game;
	}

	public void setProgramTypePlayer(byte color) {
		String property = color == Position.COLOR_WHITE ? PROPERTY_WHITE_TYPE : PROPERTY_BLACK_TYPE;
		setStringProperty(property, PROPERTY_PLAYER_TYPE_PROGRAM);
	}

	
	public PgnRound getPgnRound() {
		return pgnRound;
	}

	public void setPgnRound(PgnRound pgnRound) {
		this.pgnRound = pgnRound;
		properties.put(PROPERTY_ROUND, pgnRound.toString());
	}

	public static PgnGameSuite parse(File file) throws FileNotFoundException, IOException {
		return parse(new FileInputStream(file));
	}
	
	public static PgnGameSuite parse(InputStream istream) throws IOException {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(istream));
			PgnGameSuite ret = parse(bufferedReader);
			return ret;
		} finally {			
			IOUtils.closeQuietly(istream);
		} 		
	}
	
	private static PgnGameSuite parse(BufferedReader bufferedReader) throws IOException {
	
		PgnGameSuite ret = new PgnGameSuite();
		while(!parse(bufferedReader, ret.pgnGames)) {
			//TODO check??
		}
		return ret;
		
	}
	
	/**
	 * 
	 * @param bufferedReader
	 * @param games
	 * @return true if it's at the end of the reader's underlying stream, false otherwise 
	 * 			(more games might follow) 
	 * @throws IOException
	 */
	private static boolean parse(BufferedReader bufferedReader, List<PgnGame> games) throws IOException {
		
		String line = null;

		Game game = Game.newGameFromStartingPosition();
		PgnGame toAdd = new PgnGame(game);
		
		boolean isFirstBlankSection = true;
		while((line = bufferedReader.readLine()) != null) {
			if(StringUtils.isBlank(line)) {
				if(isFirstBlankSection) {					
					continue;
				} else {
					break;
				}
			} else {
				isFirstBlankSection = false;
			}
			Matcher matcher = PROPERTY_PATTERN.matcher(line);
			if(!matcher.find()) {
				LOG.warn("Line [" + line + "] didn't match property pattern, skipping");
				continue;
			}
			String key = matcher.group(1);
			String value = matcher.group(2);
			toAdd.setStringProperty(key, value);
			
		}
		
		if(line == null) {
			return true;
		}
		
		toAdd.assertAllSevenTagsPresent();				
		
		StringBuilder movesStringBuilder = new StringBuilder();

		//skip new lines between properties and moves
		while((line = bufferedReader.readLine()) != null) {			
			if(!StringUtils.isBlank(line)) {
				break;
			}
		}
		if(line == null) {
			return true;
		}
		
		if(!StringUtils.isBlank(line)) {
			movesStringBuilder.append(line);
		}
			
		while((line = bufferedReader.readLine()) != null) {
			if(StringUtils.isBlank(line)) {
				break;
			}
			if(movesStringBuilder.length() > 0) {
				movesStringBuilder.append("\n");
			}
			movesStringBuilder.append(line);
		}

		toAdd.fillGameData();
		
		MoveTextParser moveTextParser = new MoveTextParser(movesStringBuilder, game);
		moveTextParser.parse();
		
		games.add(toAdd);
		return false;		
	}
	
	private void fillGameData() {

		//TODO rethink
		if(gameData == null) {
			gameData = new GameData();
		}
		
		gameData.setWhite((String)properties.get(PROPERTY_WHITE));
		gameData.setBlack((String)properties.get(PROPERTY_BLACK));

		//TODO one time control only 
		List<TimeControl> timeControls = (List<TimeControl>)properties.get(PgnTimeControlHandler.TIME_CONTROL);
		if(timeControls != null) {
			gameData.setTimeControls(timeControls);	
		}
		
	}
	
	
	public PgnGame(Game game) {
		this.game = game;

	}

	private void appendProperty(String key, StringBuilder sb) {
		
		if(PROPERTY_HANDLERS.containsKey(key)) {
			PropertyHandler propertyHandler = PROPERTY_HANDLERS.get(key);
			propertyHandler.format(key, this, sb);			
		} else {
			appendProperty(key, properties.get(key), sb);
		}
	}
	
	
	void appendProperty(String key, Object value, StringBuilder stringBuilder) {
		stringBuilder.append("[" + key + " \"" + value + "\"]\n");
	}
	
	public static void saveToFile(File file, PgnGame game) throws IOException {
		
		//TODO redo !!!!
		
		OutputStreamWriter outputStreamWriter = null;

		try {
			outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
			outputStreamWriter.append(game.format());
			
		} finally {
			IOUtils.closeQuietly(outputStreamWriter); 
		}
		
	}
	
	private void assertAllSevenTagsPresent() {
		
		if(!properties.keySet().containsAll(SEVEN_TAG_ROOSTER_PROPERTY_NAMES)) {
			Set<String> missing = new HashSet<String>(SEVEN_TAG_ROOSTER_PROPERTY_NAMES);
			missing.removeAll(properties.keySet());			
			throw new IllegalStateException("Doesn't contain all seven tag roster properties. Missing ones: " + missing);			
		}
		
	}
	
	
	public String format() {

		assertAllSevenTagsPresent();
		
		//TODO not in proper order!!!! #implementation
		for(PropertyHandler propertyHandler : PROPERTY_HANDLERS.values()) {
			propertyHandler.chechBeforeParse(this);
		}
		
		if(game == null) {
			throw new IllegalStateException("Game not set");
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		for(String key : SEVEN_TAG_ROOSTER_PROPERTY_NAMES) {
			appendProperty(key, stringBuilder);
		}
		
		for(String key : properties.keySet()) {
			if(SEVEN_TAG_ROOSTER_PROPERTY_NAMES.contains(key)) {
				continue;
			}
			appendProperty(key, stringBuilder);
		}
		
		stringBuilder.append("\n");
		
		appendMoveText(stringBuilder, game.getHeadGameNode(), true);
		
		
		//TODO use something else!!!
		stringBuilder.append(ResultHandler.format(this));
		
		return stringBuilder.toString();
	}
	
	

	public void appendMoveText(StringBuilder stringBuilder, GameNode startingNode, boolean breakLines) {
		appendMoveText(stringBuilder, startingNode, breakLines, null);
	}
	
	
	public void appendMoveText(StringBuilder stringBuilder, GameNode startingNode, boolean breakLines, FormatNodeListener formatNodeListener) {
		
		GameNode gameNode = startingNode;
		
		boolean placeBlackOrdinal = false;
		
		while(gameNode.getNextMove() != null) {
			
			boolean blackToMove = gameNode.getPosition().colorToMove == Position.COLOR_BLACK;
			
			if(gameNode.getComment() != null) {
				stringBuilder.append("{" + gameNode.getComment() + "} ");
				placeBlackOrdinal = placeBlackOrdinal || blackToMove;
			}
			
			placeBlackOrdinal = placeBlackOrdinal || (gameNode == startingNode && blackToMove); 
			
			if(!blackToMove) {
			
				if(gameNode != startingNode && breakLines) {
					//TODO rather System.getProperty("line.seperator"); ??
					stringBuilder.append("\n");
				}
				
				if(formatNodeListener != null) {
					formatNodeListener.beforeNode(stringBuilder);
				}				
				stringBuilder.append("" + gameNode.getOrdinalNumber() + ".");
			
			} else {
				if(formatNodeListener != null) {
					formatNodeListener.beforeNode(stringBuilder);
				}			
				if(placeBlackOrdinal) {
					stringBuilder.append("" + gameNode.getOrdinalNumber() + "...");
					placeBlackOrdinal = false;
				}
				
			}
			
			//TODO unidentified bug....
			if(gameNode.getSanMove() == null) {
				LOG.error("san move == null! ");
				if(gameNode.getPrevious() != null) {
					LOG.error(" move that lead here: " + gameNode.getPrevious().getNextMove());	
				}
			} else {
				stringBuilder.append(gameNode.getSanMove().toString() + " ");
			}				
			
			if(formatNodeListener != null) {
				formatNodeListener.afterNode(stringBuilder, gameNode);
			}	
			
			
			if(!CollectionUtils.isEmpty(gameNode.getVariations())) {
				handleVariation(stringBuilder, gameNode, formatNodeListener);
				placeBlackOrdinal = !blackToMove;
			}
			
			//TODO unidentified bug2 
			if(gameNode.getNext() == null) {
				LOG.error("game node == null! ");
				LOG.error(" move that lead here: " + gameNode.getNextMove());				
				break;
			}
			
			gameNode = gameNode.getNext();

				
		}
		
		if(gameNode.getComment() != null) {
			stringBuilder.append("{" + gameNode.getComment() + "} ");			
		}
		
	}
	
	private void handleVariation(StringBuilder stringBuilder, GameNode startingGameNode, FormatNodeListener formatNodeListener) {
		
		for(GameNode gameNode : startingGameNode.getVariations()) {
			stringBuilder.append("\n");
			for(int i = 0; i < startingGameNode.getVariationDepth() + 1; i++) {
				stringBuilder.append("  ");
			}		
			stringBuilder.append("(");		
			appendMoveText(stringBuilder, gameNode, false, formatNodeListener);			
			stringBuilder.append(") ");
		}
		
	}
	
	public String getEvent() {
		return (String)properties.get(PROPERTY_EVENT);
	}

	public void setEvent(String event) {
		properties.put(PROPERTY_EVENT, event);
	}


	public PgnDate getDate() {
		return pgnDate;
	}

	public void setDate(PgnDate date) {
		pgnDate = date;
		properties.put(PROPERTY_DATE, pgnDate);
	}

	public Result getResult() {
		return game.getResult();
	}

	public void setResult(Result result) {
		game.setResult(result);
		properties.put(PROPERTY_RESULT, pgnDate);
	}

	public ResultExplanation getResultExplanation() {
		return game.getResultExplanation();
	}

	public void setResultExplanation(ResultExplanation resultExplanation) {
		game.setResultExplanation(resultExplanation);
	}

	public String getWhite() {
		return (String)properties.get(PROPERTY_WHITE);
	}

	public void setWhite(String white) {
		properties.put(PROPERTY_WHITE, white);
	}

	public String getBlack() {
		return (String)properties.get(PROPERTY_BLACK);
	}

	public void setBlack(String black) {
		properties.put(PROPERTY_BLACK, black);
	}

	public Game getGame() {
		return game;
	}

	public GameData getGameData() {
		return gameData;
	}

	public void setGameData(GameData gameData) {
		this.gameData = gameData;
	}


	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public void setStringProperty(String key, String value) {
		
		if(PROPERTY_HANDLERS.containsKey(key)) {
			PROPERTY_HANDLERS.get(key).parse(key, value, this);
			return;
		}
		
		properties.put(key, value);		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PgnGame other = (PgnGame) obj;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else {
			
			//my code.... Map.equals doesn't work.... has to compare string representation of values
			if(other.properties == null) {
				return false;
			}
			if(properties.size() != other.properties.size()) {
				return false;
			}
			for(String key : properties.keySet()) {
				//this checks for null entries, but this check is skipped for this.properties 
				if(other.properties.get(key) == null) {
					return false;
				}
				if(!other.properties.get(key).toString().equals(properties.get(key).toString())) {
					return false;
				}
			}
			
		}
		return true;
	}	
	
}
