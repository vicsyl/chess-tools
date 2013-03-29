package org.virutor.chess.standard;

import org.virutor.chess.standard.PgnGame.PropertyHandler;

public class DateHandler implements PropertyHandler {

	@Override
	public void parse(String key, String value, PgnGame pgnGame) {				
		pgnGame.setDate(new PgnDate(value));		
	}

	@Override
	public String format(String key, PgnGame pgnGame) {
		//TODO assert key
		return pgnGame.pgnDate.toString();
	}

}
