package com.virutor.chess.standard;

import java.util.Date;

import org.virutor.chess.model.Game.Result;
import org.virutor.chess.standard.PgnDate;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.standard.PgnRound;

public class PgnTestUtils {

	public static void fillDefaultSevenTagRooster(PgnGame pgnGame) {
		
		pgnGame.setStringProperty(PgnGame.PROPERTY_WHITE, "White");
		pgnGame.setStringProperty(PgnGame.PROPERTY_BLACK, "Black");
		pgnGame.setDate(new PgnDate(new Date()));
		pgnGame.setStringProperty(PgnGame.PROPERTY_EVENT, "Event");
		pgnGame.setResult(Result.UNRESOLVED);
		pgnGame.setPgnRound(PgnRound.UNKNOWN);
		pgnGame.setStringProperty(PgnGame.PROPERTY_SITE, "Prague");

	}
	
}
