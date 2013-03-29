package org.virutor.chess.standard;

import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.standard.PgnGame.PropertyHandler;

public class ResultHandler implements PropertyHandler {

	private static final Logger LOG = Logger.getLogger(ResultHandler.class); 
	
	@Override
	public void parse(String key, String value, PgnGame pgnGame) {

		//TODO really???
		
		//TODO assert key == result
		if(!PgnGame.STRING_RESULTS.containsKey(value)) {
			LOG.warn("Cannot recognize result='" + value + "', skipping");
			return;
		}
		Game.Result result = PgnGame.STRING_RESULTS.get(value);
		pgnGame.game.setResult(result);
		pgnGame.properties.put(key, result);
		
		
	}

	@Override
	public String format(String key, PgnGame pgnGame) {
		
		Result result = pgnGame.game.getResult();
		return PgnGame.RESULT_STRING.get(result);
	}
		
}
