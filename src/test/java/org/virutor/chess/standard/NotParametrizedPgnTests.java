package org.virutor.chess.standard;

import java.io.ByteArrayInputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Position;

public class NotParametrizedPgnTests {

	@Test
	public void FenSetUpTagTest() throws Exception {
		
		Position position = Position.getStartPosition();
		position.fullMoveClock = 10;
		String positionFen = FenUtils.positionToFen(position); 
		
		PgnGame pgnGame = new PgnGame(new Game(position));
		PgnTestUtils.fillDefaultSevenTagRooster(pgnGame);
		
		String formatter = pgnGame.format();
		//NOTE after formatting, you'd just have the setup and fen tags... #implementation
		
		Assert.assertEquals(new Integer(1), pgnGame.getProperties().get(SetUpHandler.SET_UP));
		Assert.assertEquals(positionFen, pgnGame.getProperties().get(FenPropertyHandler.FEN));
		
		PgnGameSuite pgnGameSuit = PgnGame.parse(new ByteArrayInputStream(formatter.getBytes()));
		PgnGame pgnGameCopy = pgnGameSuit.pgnGames.get(0);		
		
		Assert.assertEquals(1, pgnGameSuit.pgnGames.size());		
		Assert.assertEquals(new Integer(1), pgnGameCopy.getProperties().get(SetUpHandler.SET_UP));
		Assert.assertEquals(positionFen, pgnGameCopy.getProperties().get(FenPropertyHandler.FEN));
		 
		
	}
	
}
