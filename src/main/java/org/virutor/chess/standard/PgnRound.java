package org.virutor.chess.standard;

import java.util.regex.Pattern;

public class PgnRound {
	
	private static final String PATTERN_STRING = "\\?|-|\\d+(\\.\\d+)*";
	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

	public static PgnRound UNKNOWN = new PgnRound("?");
	public static PgnRound NOT_APPLICABLE = new PgnRound("-");
	
	public static PgnRound getInstance(String string) {
		if(UNKNOWN.string.equals(string)) {
			return UNKNOWN;
		}
		if(NOT_APPLICABLE.string.equals(string)) {
			return NOT_APPLICABLE;
		}
		if(!PATTERN.matcher(string).matches()) { 
			return new PgnRound("?", string);
		}
		
		return new PgnRound(string);		
	} 
	
	private final String string;
	private final String originalString;

	private PgnRound(String string) {
		this(string, string);
	}
	
	private PgnRound(String string, String originalString) {
		this.string = string;		
		this.originalString = originalString;
	}
	
	public String toString() {
		return string;
	}

	public String getOriginalString() {
		return originalString;
	}	
	
}
