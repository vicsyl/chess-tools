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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	public void setGame(Game game) {
		this.game = game;
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
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(istream));
		PgnGameSuite ret = parse(bufferedReader);
		IOUtils.closeQuietly(istream);
		return ret;
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

		Game game = new Game(new Position().setStartPosition());
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
	
	public static void saveToFile(File file, Game game) throws IOException {
		
		PgnGame pgnGame = new PgnGame(game);		
		OutputStreamWriter outputStreamWriter = null;

		try {
			outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
			outputStreamWriter.append(pgnGame.format());
			
		} finally {
			IOUtils.closeQuietly(outputStreamWriter); 
		}
		
	}
	
	private void assertAllSevenTagsPresent() {
		
		for(String key : SEVEN_TAG_ROOSTER_PROPERTY_NAMES) {
			if(!properties.containsKey(key)) {
				throw new IllegalStateException("Doesn't contain seven tag roster property: " + key);
			}
		}
		//TODO special fields
		
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
			
			//TODO what about SanMove(GameNode n) ??!!
			SanMove san = new SanMove(gameNode.getNextMove(), gameNode.getPosition(), gameNode.getGeneratedMoves());
			stringBuilder.append(san.toString() + " ");
			
			if(formatNodeListener != null) {
				formatNodeListener.afterNode(stringBuilder, gameNode);
			}	
			
			
			if(!CollectionUtils.isEmpty(gameNode.getVariations())) {
				handleVariation(stringBuilder, gameNode, formatNodeListener);
				placeBlackOrdinal = !blackToMove;
			}
			
			
			gameNode = gameNode.getNext();
				
		}
		
		if(gameNode.getComment() != null) {
			stringBuilder.append("{" + gameNode.getComment() + "} ");			
		}
		
	}
	
	private void handleVariation(StringBuilder stringBuilder, GameNode startingGameNode, FormatNodeListener formatNodeListener) {
		
		stringBuilder.append("(");
		
		boolean firstFlag = true;
		for(GameNode gameNode : startingGameNode.getVariations()) {
			if(firstFlag) {
				firstFlag = false;
			} else {
				//TODO experimental!!! #implementation #standard
				stringBuilder.append("; "); 
				LOG.debug("Don't know yet how to handle multiple variations from one node at once - see the standard");
			}
			appendMoveText(stringBuilder, gameNode, false, formatNodeListener);			
		}
		
		stringBuilder.append(") ");
		
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

	
	/*
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}


	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}

	*/

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

	
}
