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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.model.Game.ResultExplanation;
import org.virutor.chess.model.Position.Continuation;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;

/**
 * TODO check Game for not null 
 * @author vaclav
 *
 */
public class PgnGame {

	private static final Logger LOG = Logger.getLogger(PgnGame.class); 
	
	static final Map<String, Game.Result> STRING_RESULTS = new HashMap<String, Game.Result>();
	static final Map<Result, String> RESULT_STRING = new HashMap<Game.Result, String>();

	


	static interface PropertyHandler {
		void parse(String key, String value, PgnGame pgnGame);
		String format(String key, PgnGame pgnGame);
		
	}
	
	private static final Map<String, PropertyHandler> PROPERTY_HANDLERS = new HashMap<String, PgnGame.PropertyHandler>();
	private static final Set<String> SEVEN_TAG_ROOSTER_PROPERTY_NAMES = new HashSet<String>();


	public static final String PROPERTY_EVENT = "Event";
	public static final String PROPERTY_SITE = "Site";
	public static final String PROPERTY_DATE = "Date";
	public static final String PROPERTY_ROUND = "Round";
	public static final String PROPERTY_BLACK = "Black";
	public static final String PROPERTY_WHITE = "White";
	public static final String PROPERTY_RESULT = "Result";
		
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\[\\s*(\\w+)\\s+\"(.*)\"\\]");

	static {

		/*
		SEVEN_TAG_ROSTER.put(PROPERTY_EVENT, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_SITE, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_DATE, Date.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_ROUND, Integer.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_BLACK , String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_WHITE, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_RESULT, Result.class);
	*/
		
		//private static final Map<String, Class<?>> KNOWN_TYPES_PROPERTIES = new HashMap<String, Class<?>>();
		/*
		KNOWN_TYPES_PROPERTIES.put(PROPERTY_EVENT, String.class);
		KNOWN_TYPES_PROPERTIES.put(PROPERTY_SITE, String.class);

		//TODO
		KNOWN_TYPES_PROPERTIES.put(PROPERTY_DATE, String.class);
		
		//TODO
		KNOWN_TYPES_PROPERTIES.put(PROPERTY_ROUND, String.class);
		
		KNOWN_TYPES_PROPERTIES.put(PROPERTY_BLACK , String.class);
		KNOWN_TYPES_PROPERTIES.put(PROPERTY_WHITE, String.class);
		
		
		KNOWN_TYPES_PROPERTIES.put(PROPERTY_RESULT, String.class);
		*/
		
		SEVEN_TAG_ROOSTER_PROPERTY_NAMES.addAll(Arrays.asList(				
				PROPERTY_BLACK,
				PROPERTY_DATE,
				PROPERTY_EVENT,
				PROPERTY_RESULT,
				PROPERTY_ROUND,
				PROPERTY_SITE,
				PROPERTY_WHITE));
		
		STRING_RESULTS.put("1-0", Game.Result.WHITE_WINS);
		STRING_RESULTS.put("0-1", Game.Result.BLACK_WINS);
		STRING_RESULTS.put("1/2-1/2", Game.Result.DRAW);
		STRING_RESULTS.put("*", Game.Result.UNRESOLVED);
		
		RESULT_STRING.put(Result.WHITE_WINS, "1-0");
		RESULT_STRING.put(Result.BLACK_WINS, "0-1");
		RESULT_STRING.put(Result.DRAW, "1/2-1/2");
		RESULT_STRING.put(Result.UNRESOLVED, "*");
		
		PROPERTY_HANDLERS.put(PROPERTY_RESULT, new ResultHandler());
		PROPERTY_HANDLERS.put(PROPERTY_DATE, new DateHandler());
		PROPERTY_HANDLERS.put(PROPERTY_ROUND, new PgnRoundHandler());
	}
	

	final Game game;
	
	Map<String, Object> properties = new HashMap<String, Object>();
	
	//seven tag rooster
	PgnDate pgnDate; 
	PgnRound pgnRound;
	//result delegated to game.result
	

	
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
		return parse(bufferedReader);		
	}
	
	
	/*
	public static PgnGame parse(String string) throws IOException {		
		BufferedReader bufferedReader = new BufferedReader(new StringReader(string));		
		return parse(bufferedReader);
	}*/
	
	private static PgnGameSuite parse(BufferedReader bufferedReader) throws IOException {
	
		PgnGameSuite ret = new PgnGameSuite();
		while(!parse(bufferedReader, ret.games)) {
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
			toAdd.setProperty(key, value);
			
		}
		
		if(line == null) {
			return true;
		}
		
		toAdd.assertAllSevenTagsPresent();
				
		
		StringBuilder movesStringBuilder = new StringBuilder();
		
		//no blank line between properties and moves
		if(!StringUtils.isBlank(line)) {
			movesStringBuilder.append(line);
		}
					
		isFirstBlankSection = true;
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
			
			//TODO really??
			movesStringBuilder.append(line + " ");
		}
		
		String movesString = movesStringBuilder.toString(); 
		if(StringUtils.isBlank(movesString)) {
			throw new IllegalArgumentException("no moves in pgn source");
		}

	
		//TODO use stack
		List<String> words = new LinkedList<String>(Arrays.asList(movesString.split("\\s+")));
	

		while(!words.isEmpty()) {
			
			String current = words.remove(0);

			//skipping periods
			int i = current.indexOf(".");
			if(i == 0) {
				while(i < current.length() && current.charAt(i) == '.') {
					i++;
				}
				if(i < current.length()) {
					words.add(0, current.substring(i));
				}
				continue;
			} else if(i > 0) {
				words.add(0, current.substring(i));
				current = current.substring(0, i);
			} 

			//TODO starts with??
			if(STRING_RESULTS.containsKey(current)) {
				//skip, should be already present in seven tag rooster
				continue;
			}
			
			if(Character.isDigit(current.charAt(0))) {
				
				i = 1;
				while(i < current.length() && Character.isDigit(current.charAt(i))) {
					i++;
				}
				if(i < current.length()) {
					words.add(current.substring(i));
				}
				continue;
				
			}			
			
			Move move = SanMove.parse(current, game.getLastPosition(), game.getLastGeneratedMoves());
			game.doMove(move);
						
			if(game.getLastPosition().getContinuation() == Continuation.CHECK_MATE) {
				game.setResultExplanation(ResultExplanation.MATE);
			} else if(game.getLastPosition().getContinuation() == Continuation.STALEMATE) {
				game.setResultExplanation(ResultExplanation.STALE_MATE);
			}
		}
		
	

		games.add(toAdd);
		

		return false;
		
	}
	
	
	public PgnGame(Game game) {
		this.game = game;

	}

	private void appendProperty(String key, StringBuilder sb) {
		
		if(PROPERTY_HANDLERS.containsKey(key)) {
			PropertyHandler propertyHandler = PROPERTY_HANDLERS.get(key);
			String stringValue = propertyHandler.format(key, this);
			appendProperty(key, stringValue, sb);
		} else {
			appendProperty(key, properties.get(key), sb);
		}
	}
	
	
	private void appendProperty(String key, Object value, StringBuilder stringBuilder) {
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
		
		List<Move> moves = game.getMoves();		
		List<Position> positions = game.getPositions();		
		for(int i = 0; i < moves.size(); i++) {
			if(positions.get(i).colorToMove == Position.COLOR_WHITE) {
				stringBuilder.append("" + (i/2 + 1) + ".");
			}
			
			/*Move mT = moves.get(i);
			if((mT.from == Position.B6 || mT.from == Position.B8) && mT.to == Position.D7 && mT.piece_moved == Piece.PIECE_KNIGHT) {
				String t = "";
			}*/
			
			SanMove san = new SanMove(moves.get(i), positions.get(i), game.getGeneratedMovesList().get(i));
			stringBuilder.append(san.toString() + " ");			
		} 
		
		return stringBuilder.toString();
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


	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public void setProperty(String key, String value) {
		
		
		if(PROPERTY_HANDLERS.containsKey(key)) {
			PROPERTY_HANDLERS.get(key).parse(key, value, this);
			return;
		}
		
		properties.put(key, value);		
	}

	
}
