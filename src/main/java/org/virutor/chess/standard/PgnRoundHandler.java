package org.virutor.chess.standard;


public class PgnRoundHandler implements PropertyHandler {

	@Override
	public void parse(String key, String value, PgnGame pgnGame) {
		pgnGame.setPgnRound(PgnRound.getInstance(value));
	}

	@Override
	public void format(String key, PgnGame pgnGame, StringBuilder sb) {
		pgnGame.appendProperty(key,pgnGame.getPgnRound().toString(), sb);
	}

	@Override
	public void chechBeforeParse(PgnGame pgnGame) {	} 

}
