package org.virutor.chess.standard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

import org.apache.commons.lang.StringUtils;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;

/**
 * TODO check Game for not null 
 * @author vaclav
 *
 */
public class PgnGame {

	/*
	private static enum Result {
		DRAW,
		BLACK_WINS,
		WHITE_WINS,
		UNRESOLVED
	}
	
	*/
	private static final Map<String, Game.Result> STRING_RESULTS = new HashMap<String, Game.Result>();
	

	private Map<String, Object> properties = new HashMap<String, Object>();

	
	private static final Map<String, Class> SEVEN_TAG_ROSTER = new HashMap<String, Class>();


	public static final String PROPERTY_EVENT = "Event";
	public static final String PROPERTY_SITE = "Site";
	public static final String PROPERTY_DATE = "Date";
	public static final String PROPERTY_ROUND = "Round";
	public static final String PROPERTY_BLACK = "Black";
	public static final String PROPERTY_WHITE = "White";
	public static final String PROPERTY_RESULT = "Result";
	

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
		
		SEVEN_TAG_ROSTER.put(PROPERTY_EVENT, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_SITE, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_DATE, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_ROUND, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_BLACK , String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_WHITE, String.class);
		SEVEN_TAG_ROSTER.put(PROPERTY_RESULT, String.class);
		
		STRING_RESULTS.put("1-0", Game.Result.WHITE_WINS);
		STRING_RESULTS.put("0-1", Game.Result.BLACK_WINS);
		STRING_RESULTS.put("1/2-1/2", Game.Result.DRAW);
		STRING_RESULTS.put("*", Game.Result.UNRESOLVED);
	}
	

	private Game game;

	private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\[\\s*(\\w+)\\s+\"(.*)\"\\]");
	
	public static PgnGame parse(InputStream istream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(istream));
		return parse(bufferedReader);		
	}
	
	public static PgnGame parse(String string) throws IOException {		
		BufferedReader bufferedReader = new BufferedReader(new StringReader(string));		
		return parse(bufferedReader);
	}
	
	private static PgnGame parse(BufferedReader bufferedReader) throws IOException {

		PgnGame ret = new PgnGame();
		String line = null;

		boolean isFirstBlank = true;
		while((line = bufferedReader.readLine()) != null) {
			if(StringUtils.isBlank(line)) {
				if(isFirstBlank) {
					continue;
				} else {
					break;
				}
			} else {
				isFirstBlank = false;
			}
			Matcher matcher = PROPERTY_PATTERN.matcher(line);
			if(!matcher.find()) {
				break;
			}
			String key = matcher.group(1);
			String value = matcher.group(2);
			
			ret.setProperty(key, value);
			
		}
		
		if(line == null) {
			throw new IllegalArgumentException("no moves in pgn source");
		}
		
		StringBuilder movesStringBuilder = new StringBuilder();
		
		//no blank line between properties and moves
		if(!StringUtils.isBlank(line)) {
			movesStringBuilder.append(line);
		}
					
		isFirstBlank = true;
		while((line = bufferedReader.readLine()) != null) {
			if(StringUtils.isBlank(line)) {
				if(isFirstBlank) {
					continue;
				} else {
					break;
				}
			} else {
				isFirstBlank = false;
			}
			movesStringBuilder.append(line + " ");
		}
		
		String movesString = movesStringBuilder.toString(); 
		if(StringUtils.isBlank(movesString)) {
			throw new IllegalArgumentException("no moves in pgn source");
		}

		
		//parse the moves
		Game game = new Game(new Position().setStartPosition());

		String[] words = movesString.split("\\s+");

		for(String word : words) {
			word = word.trim();
			Integer i = null;
			try {
				int index = word.indexOf(".");
				if(index != -1) {
					i = Integer.parseInt(word.substring(0,index));
				}
			} catch (NumberFormatException e) {}
			if(i != null) {
				word = word.substring(String.valueOf(i).length() + 1).trim(); //expecting '.'
			}			
			if(STRING_RESULTS.containsKey(word)) {
				game.setResult(STRING_RESULTS.get(word));
				break;
			}
			
			Move move = SanMove.parse(word, game.getLastPosition(), game.getLastGeneratedMoves());
			game.doMove(move);
		}
		
		ret.setGame(game);
		return ret;
	}
	

	private void appendProperty(String key, StringBuilder stringBuilder) {
		stringBuilder.append("[" + key + " \"" + properties.get(key) + "\"]\n");
	}
	
	public String format() {

		for(String key : SEVEN_TAG_ROSTER.keySet()) {
			if(!properties.containsKey(key)) {
				throw new IllegalStateException("Mandatory property " + key + " not set");
			}
		}
		if(game == null) {
			throw new IllegalStateException("Game not set");
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		for(String key : SEVEN_TAG_ROSTER.keySet()) {
			appendProperty(key, stringBuilder);
		}
		
		for(String key : properties.keySet()) {
			if(!SEVEN_TAG_ROSTER.containsKey(key)) {
				appendProperty(key, stringBuilder);
			}
		}
		
		stringBuilder.append("\n");
		
		List<Move> moves = game.getMoves();		
		List<Position> positions = game.getPositions();		
		for(int i = 0; i < moves.size(); i++) {
			if(positions.get(i).colorToMove == Position.COLOR_WHITE) {
				stringBuilder.append("" + (i/2 + 1) + ". ");
			}
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

	/*
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
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

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}*/

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}	

	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public void setProperty(String key, Object value) {
		if(SEVEN_TAG_ROSTER.keySet().contains(key)) {
			if(!SEVEN_TAG_ROSTER.get(key).isAssignableFrom(value.getClass())) {
				throw new IllegalArgumentException("Wrong type for property " + key + ". Expected " + SEVEN_TAG_ROSTER.get(key) + ", got " + value.getClass());
			}
		}
		properties.put(key, value);		
	}

	
}
