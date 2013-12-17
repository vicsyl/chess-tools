package org.virutor.chess.model.generator;

import java.util.HashMap;
import java.util.Map;

import org.virutor.chess.model.Position;

public class RepetitionGameContext {

	private final Map<Long, Integer> repetitionMap = new HashMap<Long, Integer>();
	
	
	public void clear() {
		repetitionMap.clear();
	}
	

	public int addAndGetRepetitions(Position position) {
		
		Integer entry = repetitionMap.get(position.hash);
		if(entry == null) {
			entry = Integer.valueOf(0);
		}
		entry++;
		repetitionMap.put(position.hash, entry);
		return entry;
	} 
	
}
