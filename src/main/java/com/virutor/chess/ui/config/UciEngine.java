package com.virutor.chess.ui.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UciEngine extends Player {
	
	// centralize? (i.e. get rod of this one?)
	final String ENGINE_PROPERTY = "engine";
	
	final String PATH_PROPERTY = "path";
	final String IS_UNINSTALLABLE = "isUninstallable";
	
	Map<String, String> properties = new HashMap<String, String>();
	
	@Override
	public String getName() {
		return properties.get(ENGINE_PROPERTY);		
	}
	
	public String getPath() {
		return properties.get(PATH_PROPERTY);		
	}

	/**
	 * TODO let it set explicitly? Isn't it better to call it by the filename first and then what it get via UCI?
	 *
	 * @param name
	 */
	@Override 
	public void setName(String name) {
		properties.put(ENGINE_PROPERTY, name);		
	}
	
	public void setPath(String path) {
		properties.put(PATH_PROPERTY, path);		
	}

	public boolean isUninstallable() {		
		String unistallableString = properties.get(IS_UNINSTALLABLE);
		if(unistallableString == null) {
			return true;
		}
		return Boolean.parseBoolean(unistallableString);
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}	
	
}
