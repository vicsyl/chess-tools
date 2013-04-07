package org.virutor.chess.standard;


public class DateHandler implements PropertyHandler {

	@Override
	public void parse(String key, String value, PgnGame pgnGame) {				
		pgnGame.setDate(new PgnDate(value));		
	}

	@Override
	public void format(String key, PgnGame pgnGame, StringBuilder sb) {
		pgnGame.appendProperty(key, pgnGame.getDate().toString(), sb);
	}

	@Override
	public void chechBeforeParse(PgnGame pgnGame) {	
		
		//TODO!!!
	} 

	
}
