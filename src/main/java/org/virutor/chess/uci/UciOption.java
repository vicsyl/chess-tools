package org.virutor.chess.uci;

import java.util.ArrayList;
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
	private Map<UciOptionValueType, Object> values = new HashMap<UciOptionValueType, Object>();
	
	public UciOption setName(String name) {
		this.name = name;
		return this;		
	}
	
	public UciOption setType(UciOptionType type) {
		this.type = type;
		return this;		
	}
	
	public UciOption addValue(UciOptionValueType uciOptionValueType, Object value) {
		if(uciOptionValueType == UciOptionValueType.VAR) {
			if(!values.containsKey(UciOptionValueType.VAR)) {
				values.put(UciOptionValueType.VAR, new ArrayList<Object>());
			}
			((List<Object>)values.get(UciOptionValueType.VAR)).add(value);
		} else {
			values.put(uciOptionValueType, value);
		}

		
		return this;
	}


	public String toString() {
		String ret =  "Name: " + name + "; type: " + type + "; ";
	
		for(Map.Entry<UciOptionValueType, Object> entry : values.entrySet()) {
			ret += entry.getKey() + ":" + entry.getValue() + ";";
		}
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		UciOption other = (UciOption) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
	
	
}
