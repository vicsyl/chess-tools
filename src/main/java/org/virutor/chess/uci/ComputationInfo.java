package org.virutor.chess.uci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.uci.ComputationInfoScore.ComputationInfoScorePropertyParser;



//TODO multipv
//TODO refutation
//TODO SCORE
	/*
	 * 
	 * * info
		the engine wants to send infos to the GUI. This should be done whenever one of the info has changed.
		The engine can send only selected infos and multiple infos can be send with one info command,
		e.g. "info currmove e2e4 currmovenumber 1" or
		     "info depth 12 nodes 123456 nps 100000".
		Also all infos belonging to the pv should be sent together
		e.g. "info depth 2 score cp 214 time 1242 nodes 2124 nps 34928 pv e2e4 e7e5 g1f3"
		I suggest to start sending "currmove", "currmovenumber", "currline" and "refutation" only after one second
		to avoid too much traffic.
		Additional info:
		* depth 
			search depth in plies
		* seldepth 
			selective search depth in plies,
			if the engine sends seldepth there must also a "depth" be present in the same string.
		* time 
			the time searched in ms, this should be sent together with the pv.
		* nodes 
			x nodes searched, the engine should send this info regularly
		* pv  ... 
			the best line found
		* multipv 
			this for the multi pv mode.
			for the best move/pv add "multipv 1" in the string when you send the pv.
			in k-best mode always send all k variants in k strings together.
		* score
			* cp 
				the score from the engine's point of view in centipawns.
			* mate 
				mate in y moves, not plies.
				If the engine is getting mated use negativ values for y.
			* lowerbound
		      the score is just a lower bound.
			* upperbound
			   the score is just an upper bound.
		* currmove 
			currently searching this move
		* currmovenumber 
			currently searching move number x, for the first move x should be 1 not 0.
		* hashfull 
			the hash is x permill full, the engine should send this info regularly
		* nps 
			x nodes per second searched, the engine should send this info regularly
		* tbhits 
			x positions where found in the endgame table bases
		* cpuload 
			the cpu usage of the engine is x permill.
		* string 
			any string str which will be displayed be the engine,
			if there is a string command the rest of the line will be interpreted as .
		* refutation   ... 
		   move  is refuted by the line  ... , i can be any number >= 1.
		   Example: after move d1h5 is searched, the engine can send
		   "info refutation d1h5 g6h5"
		   if g6h5 is the best answer after d1h5 or if g6h5 refutes the move d1h5.
		   if there is norefutation for d1h5 found, the engine should just send
		   "info refutation d1h5"
			The engine should only send this if the option "UCI_ShowRefutations" is set to true.
		* currline   ... 
		   this is the current line the engine is calculating.  is the number of the cpu if
		   the engine is running on more than one cpu.  = 1,2,3....
		   if the engine is just using one cpu,  can be omitted.
		   If  is greater than 1, always send all k lines in k strings together.
			The engine should only send this if the option "UCI_ShowCurrLine" is set to true.
	 * 
	 * 
	 * 
	 */

public class ComputationInfo {
	
	private static final Pattern PATTERN_WORD = Pattern.compile("^\\s*\\S+\\s*");
	
	static String getWordWhiteSpacePrefixed(StringBuilder sb) {
		Matcher matcher = PATTERN_WORD.matcher(sb);
		if(!matcher.find()) {
			return null;
		}
		return matcher.group();
	} 
	
	interface InfoPropertyParser<T> {
		T parse(StringBuilder restOfTheLine);
	}
	
	public static final InfoPropertyParser<Long> LONG_PROPERTY_PARSER = new InfoPropertyParser<Long>() {

		@Override
		public Long parse(StringBuilder restOfTheLine) {
			
			String originalWord = getWordWhiteSpacePrefixed(restOfTheLine);
			if(originalWord == null) {
				return null;
			}
			try { 
				long l = Long.parseLong(originalWord.trim());
				restOfTheLine.delete(0, originalWord.length());
				return l;
			} catch (NumberFormatException e) {
				System.out.println("NumverFE: " + e);
				return null;
			}
		
		}
	
	};    
	
	
	public static final InfoPropertyParser<Integer> INTEGER_PROPERTY_PARSER = new InfoPropertyParser<Integer>() {

		@Override
		public Integer parse(StringBuilder restOfTheLine) {
			Long l = LONG_PROPERTY_PARSER.parse(restOfTheLine);
			return l == null ? null : l.intValue();
		}
	
	};    
	
	
	public static final InfoPropertyParser<Integer> NON_NEGATIVE_INTEGER_PARSER = new InfoPropertyParser<Integer>() {

		@Override
		public Integer parse(StringBuilder restOfTheLine) {
			Integer i = INTEGER_PROPERTY_PARSER.parse(restOfTheLine);
			if(i != null && i < 0) {
				//TODO log warn
				return null;
			}
			return i;
		}
	
	};    
	
	public static final InfoPropertyParser<Integer> PERMILL_PROPERTY_PARSER = new InfoPropertyParser<Integer>() {

		@Override
		public Integer parse(StringBuilder restOfTheLine) {
			Integer i = NON_NEGATIVE_INTEGER_PARSER.parse(restOfTheLine);
			if(i != null && i > 1000) {
				//TODO  log warn
				return null;
			}
			return i;
		}
	
	};    
	
	public static final InfoPropertyParser<LongAlgebraicMove> LONG_ALGEBRAIC_MOVE_PARSER = new InfoPropertyParser<LongAlgebraicMove>() {

		@Override
		public LongAlgebraicMove parse(StringBuilder restOfTheLine) {
			
			String originalWord = getWordWhiteSpacePrefixed(restOfTheLine);
			String trimmedString = originalWord.trim();
			
			//TODO exception checking!!!!
			LongAlgebraicMove la = new LongAlgebraicMove(trimmedString);
			
			restOfTheLine.delete(0, originalWord.length());
			return la;
		}
	
	};    
	

	
	public static final InfoPropertyParser<List<LongAlgebraicMove>> LONG_ALGEBRAIC_MOVE_LIST_PARSER = new InfoPropertyParser<List<LongAlgebraicMove>>() {

		@Override
		public List<LongAlgebraicMove> parse(StringBuilder restOfTheLine) {
			
			List<LongAlgebraicMove> ret = new ArrayList<LongAlgebraicMove>();
			LongAlgebraicMove la = null;
			while((la = LONG_ALGEBRAIC_MOVE_PARSER.parse(restOfTheLine)) != null) {
				ret.add(la);
				if(StringUtils.isBlank(restOfTheLine.toString())) {
					break;
				}
			}
			if(ret.size() == 0) {
				ret = null;
			}
			return ret;
			 
		}
	
	};    
	
	public static final InfoPropertyParser<String> STRING_PARSER = new InfoPropertyParser<String>() {

		@Override
		public String parse(StringBuilder restOfTheLine) {
			String s = restOfTheLine.toString();
			restOfTheLine.delete(0, restOfTheLine.length());
			return s.trim();
		}
	
	};    
	
	
	

	private static final Map<String, InfoPropertyParser<?>> PARSERS = new HashMap<String, ComputationInfo.InfoPropertyParser<?>>();
	
	private static final String DEPTH = "depth";   
	private static final String SEL_DEPTH = "seldepth";   
	private static final String TIME = "time";   
	private static final String NODES = "nodes";   
	private static final String PV = "pv";   
	private static final String CURRMOVE = "currmove";   
	private static final String CURRMOVE_NUMBER = "currmovenumber";   
	private static final String HASH_FULL = "hashfull";   
	private static final String NPS = "nps";   
	private static final String TBHITS = "tbhits";   
	private static final String CURRLINE = "currline";   
	private static final String STRING = "string";   
	private static final String CPU_LOAD = "cpuload";   
	private static final String SCORE = "score";
	
	//TODO
	private static final String REFUTATION = "refutation";

	
	
	static {
		PARSERS.put(DEPTH, NON_NEGATIVE_INTEGER_PARSER);
		PARSERS.put(SEL_DEPTH, NON_NEGATIVE_INTEGER_PARSER);
		PARSERS.put(TIME, NON_NEGATIVE_INTEGER_PARSER);
		PARSERS.put(NODES, NON_NEGATIVE_INTEGER_PARSER);
		PARSERS.put(PV, LONG_ALGEBRAIC_MOVE_LIST_PARSER);
		PARSERS.put(CURRMOVE, LONG_ALGEBRAIC_MOVE_PARSER);
		PARSERS.put(CURRMOVE_NUMBER, NON_NEGATIVE_INTEGER_PARSER);
		PARSERS.put(HASH_FULL, PERMILL_PROPERTY_PARSER);
		PARSERS.put(NPS, LONG_PROPERTY_PARSER);
		PARSERS.put(TBHITS, LONG_PROPERTY_PARSER);
		PARSERS.put(CPU_LOAD, PERMILL_PROPERTY_PARSER);
		PARSERS.put(STRING, STRING_PARSER);
		PARSERS.put(CURRLINE, LONG_ALGEBRAIC_MOVE_LIST_PARSER);
		PARSERS.put(SCORE, new ComputationInfoScorePropertyParser());
	}
	
	
	
	public void copyFrom(ComputationInfo from) {
		
		for(Map.Entry<String, Object> entry : from.properties.entrySet()) {
			if(properties.containsKey(entry.getKey())) {
				properties.remove(entry.getKey());
			}
			properties.put(entry.getKey(), entry.getValue());
		}
		
	} 
	
	public static ComputationInfo parse(String line) { 
		return parse(line, new ComputationInfo());
	}
	
	
	public static ComputationInfo parse(String line, ComputationInfo computationInfo) {
		
		StringBuilder sb = new StringBuilder(line);
		
		String infoOriginal = getWordWhiteSpacePrefixed(sb);
		
		if(UciConstants.INFO.equals(infoOriginal.trim())) {
			sb.delete(0, infoOriginal.length());
		}
		
		//should be ok if sb ends with a whitespace - take a look at the pattern
		while(sb.length() > 0) {
			String origString = getWordWhiteSpacePrefixed(sb);
			String trimmed = origString.trim();
			if(!PARSERS.containsKey(trimmed)) {
				//TODO log unknown symbol
				return computationInfo;
			}
			sb.delete(0, origString.length());
			Object o = PARSERS.get(trimmed).parse(sb);
			computationInfo.properties.put(trimmed, o);			
		}
		
		return computationInfo;
	} 
	

	private Map<String, Object> properties =  new HashMap<String, Object>();
	
	
	
	public Integer getDepth() {
		return (Integer)properties.get(DEPTH);
	}

	public Integer getSeldepth() {
		return (Integer)properties.get(SEL_DEPTH);
	}

	public Integer getTime() {
		return (Integer)properties.get(TIME);
	}

	public Integer getNodes() {
		return (Integer)properties.get(NODES);
	}

	public List<LongAlgebraicMove> getPv() {
		return (List<LongAlgebraicMove>)properties.get(PV);
	}

	public ComputationInfoScore getScore() {
		return (ComputationInfoScore)properties.get(SCORE);
	}

	public LongAlgebraicMove getCurrMove() {
		return (LongAlgebraicMove)properties.get(CURRMOVE);
	}

	public Integer getCurrMoveNumber() {
		return (Integer)properties.get(CURRMOVE_NUMBER);
	}

	public Integer getHashfull() {
		return (Integer)properties.get(HASH_FULL);
	}

	public Long getNps() {
		return (Long)properties.get(NPS);
	}

	public Long getTbhits() {
		return (Long)properties.get(TBHITS);
	}

	public Integer getCpuload() {
		return (Integer)properties.get(CPU_LOAD);
	}

	public String getString() {
		return (String)properties.get(STRING);
	}

	public Map<LongAlgebraicMove, LongAlgebraicMove> getRefutations() {
		return (Map<LongAlgebraicMove, LongAlgebraicMove>)properties.get(REFUTATION);
	}

	public List<LongAlgebraicMove> getCurrline() {
		return (List<LongAlgebraicMove>)properties.get(CURRLINE);
	}
	
	@Override
	public String toString() {
		return "ComputationInfo: properties:" + properties.toString();
	}
}
