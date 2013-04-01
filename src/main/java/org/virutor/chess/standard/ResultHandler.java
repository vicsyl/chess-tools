package org.virutor.chess.standard;

import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.standard.PgnGame.PropertyHandler;

public class ResultHandler implements PropertyHandler {

	private static final Logger LOG = Logger.getLogger(ResultHandler.class); 

	public static final ResultHandler INSTANCE = new ResultHandler();		
	
	private ResultHandler() {}
	
	public static String format(Result result) {
		return PgnGame.RESULT_STRING.get(result);
	}
	
	@Override
	public void parse(String key, String value, PgnGame pgnGame) {

		//TODO really???
		
		//TODO assert key == result
		if(!PgnGame.STRING_RESULTS.containsKey(value)) {
			LOG.warn("Cannot recognize result='" + value + "', skipping");
			return;
		}
		Game.Result result = PgnGame.STRING_RESULTS.get(value);
		pgnGame.getGame().setResult(result);
		pgnGame.getProperties().put(key, result);
		
		
	}

	@Override
	public String format(String key, PgnGame pgnGame) {
		return format(pgnGame.getGame().getResult());
	}
		
}
