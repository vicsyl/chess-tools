package org.virutor.chess.uci;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.virutor.chess.uci.UciOption.UciOptionType;
import org.virutor.chess.uci.UciOption.UciOptionValueType;

public class UciEngineInfoUtils {
	
	// id
	private static final String NAME = "name";
	private static final String AUTHOR = "author";
	
	//options
	private static final String TYPE = "type";
	private static final String MIN = "min";
	private static final String MAX = "max";
	private static final String DEFAULT = "default";
	private static final String VAR = "var";		
	
	private static final List<String> OPTIONS_KEYWORDS = Arrays.asList(new String[] {NAME, TYPE, MIN, MAX, DEFAULT, VAR});
	

	//TODO reimplement!!! - what doeas the standard actually say?
	public static void handleOptions(EngineInfo engineInfo, String[] words) { 
		
		UciOption newUciOption = new UciOption();
		
		for(int i = 1; i < words.length; i++) {
			if(!OPTIONS_KEYWORDS.contains(words[i])) {
				System.out.println("cannot understand option keyword:" + words[i]);
				engineInfo.options.put(newUciOption.name, newUciOption);
				return;
			}
			if(i == words.length - 1) {
				throw new IllegalArgumentException("no value after keyword:" + words[i]);
			}
			if(NAME.equals(words[i])) {
				int start = i + 1; 
				i++;
				while(!OPTIONS_KEYWORDS.contains(words[i+1]) && i < words.length - 1) {
					i++;
				}
				newUciOption.name = StringUtils.join(words, " ", start, i + 1);
			} else if(TYPE.equals(words[i])) {
				
				//TODO better !!!!
				boolean success = false;
				for(UciOptionType uciOptionType : UciOptionType.values()) {
					if(uciOptionType.name().equalsIgnoreCase(words[i+1])) {
						newUciOption.type = uciOptionType;
						i++;
						success = true;
						break;
					}
				}
				if(!success) {
					throw new IllegalArgumentException("cannot understand type:" + words[i+1]);
				}
			} else {
				for(UciOptionValueType uciOptionValueType : UciOptionValueType.values()) {
					if(uciOptionValueType.name().equalsIgnoreCase(words[i])) {
						newUciOption.addValue(uciOptionValueType, parse(words[i+1]));
						i++;
						break;
					}
				}
			} 
		}
		
		engineInfo.options.put(newUciOption.name, newUciOption);

		
	}
	
	private static Object parse(String s) {
		
		try {
			Integer i = Integer.parseInt(s);
			return i;
		} catch (NumberFormatException e) {}
		//TODO context -> check 
		if("true".equalsIgnoreCase(s)) {
			return true;
		} 
		if("false".equalsIgnoreCase(s)) {
			return false;
		} 
		return s;
		
	} 
	
	public static void handleId(EngineInfo engineInfo, String[] words) {
		
		if(words.length < 3) {
			throw new IllegalArgumentException();
		}
		
				
		if(words[1].equals(NAME)) {
			//engineInfo.name = join(words, 2);
			engineInfo.name = StringUtils.join(words, " ", 2, words.length);
		} else if (words[1].equals(AUTHOR)) {
			//engineInfo.author = join(words, 2);
			engineInfo.author = StringUtils.join(words, " ", 2, words.length);
		}
	}
	
	//TODO better implementation?
	/*
	private static String join(String[] array, int from) {
		StringBuffer sb = new StringBuffer();
		for(int i = from; i < array.length; i++) {
			if(i != from) {
				sb.append(" ");
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}*/
	
	
}
