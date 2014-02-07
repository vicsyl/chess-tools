package org.virutor.chess.standard;

import java.util.Date;

import junit.framework.Assert;

import org.virutor.chess.model.Game.Result;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.io.LongAlgebraicMove;

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

	public static void assertEqualsMove(String expectedMove, Move realMove) {
		LongAlgebraicMove laMoveR = new LongAlgebraicMove(realMove);
		LongAlgebraicMove laMoveE = new LongAlgebraicMove(expectedMove);
		Assert.assertEquals(laMoveE, laMoveR);
		
	}
	
}
