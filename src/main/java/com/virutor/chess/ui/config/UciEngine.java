package com.virutor.chess.ui.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UciEngine extends Player {
	
	// centralize? (i.e. get rod of this one?)
	final String ENGINE_PROPERTY = "engine";
	
	final String PATH_PROPERTY = "path";
	
	Map<String, String> properties = new HashMap<String, String>();
	
	@Override
	public String getName() {
		return properties.get(ENGINE_PROPERTY);		
	}
	
	public String getPath() {
		return properties.get(PATH_PROPERTY);		
	}
	
	@Override 
	public void setName(String name) {
		properties.put(ENGINE_PROPERTY, name);		
	}
	
	public void setPath(String path) {
		properties.put(PATH_PROPERTY, path);		
	}
	
	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}	
	
}
