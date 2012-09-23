package org.virutor.chess.uci;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UciOption {
	
	public static final List<String> UciOptionsStrings = 
		Arrays.asList(new String[] {"CHECK", "SPIN", "COMBO", "BUTTON", "STRING"});
	
	public enum UciOptionType {
		CHECK,
		SPIN, 
		COMBO, 
		BUTTON, 
		STRING;
	}
	
	public enum UciOptionValueType {
		MIN, MAX, VALUE, VAR, DEFAULT
	}
	
	public String name;
	public UciOptionType type;
	public Map<UciOptionValueType, Object> values = new HashMap<UciOptionValueType, Object>();
	
	public String toString() {
		String ret =  "Name: " + name + "; type: " + type + "; ";
	
		for(Map.Entry<UciOptionValueType, Object> entry : values.entrySet()) {
			ret += entry.getKey() + ":" + entry.getValue() + ";";
		}
		return ret;
	}
	
}
