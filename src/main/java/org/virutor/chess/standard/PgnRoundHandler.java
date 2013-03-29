package org.virutor.chess.standard;

import org.virutor.chess.standard.PgnGame.PropertyHandler;

public class PgnRoundHandler implements PropertyHandler {

	@Override
	public void parse(String key, String value, PgnGame pgnGame) {
		pgnGame.setPgnRound(PgnRound.getInstance(value));
	}

	@Override
	public String format(String key, PgnGame pgnGame) {
		return pgnGame.getPgnRound().toString();
	}

}
