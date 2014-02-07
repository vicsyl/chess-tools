package org.virutor.chess.standard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.Test;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.GameNode;

public class SpecificPgnTests {

	//move text: 1.d4 d5 2.c4 c6 ( 2...e6 3.Nf3 ) ( 2...e5 3.dxe5 d4 ) 3.Nc3 g6 ( 3...Nf6 4.Nf3 (4.g3 g6) ) 4.e3 *
	private static final String path = "/multiple_variations.pgn";

	@Test 
	public void multipleVariationsParseFormatSemicolonBugTest() throws Exception {

		//read the pgn game
		PgnGameSuite suite = PgnGame.parse(getClass().getResourceAsStream(path));		
		Assert.assertEquals(1, suite.pgnGames.size());
		
		StringWriter stringWriter = new StringWriter();
		suite.write(stringWriter);				
		Assert.assertFalse(stringWriter.toString().contains(";"));
		
	}
	
	@Test 
	public void multipleVariationsParseFormatTest() throws Exception {

		//read the pgn game
		PgnGameSuite suite = PgnGame.parse(getClass().getResourceAsStream(path));		
		Assert.assertEquals(1, suite.pgnGames.size());
		
		StringWriter stringWriter = new StringWriter();
		suite.write(stringWriter);				
		InputStream istream = new ByteArrayInputStream(stringWriter.toString().getBytes());		
		PgnGameSuite suiteCopy = PgnGame.parse(istream);
		Assert.assertEquals(1, suiteCopy.pgnGames.size());
		
		Assert.assertEquals(suite, suiteCopy);
		
	}
	
	@Test
	public void multipleVariationsReadTest() throws Exception {
	
		//read the pgn game
		PgnGameSuite suite = PgnGame.parse(getClass().getResourceAsStream(path));		
		Assert.assertEquals(1, suite.pgnGames.size());
		
		//assert the moves are there in right order until the part with multiple variations 
		String[] expectedMoves = new String[] {
			"d2d4", 
			"d7d5",
			"c2c4",
			"c7c6"
		};
		
		Game game = suite.pgnGames.get(0).getGame();
		GameNode currentNode = game.getHeadGameNode();
		
		for(String expectedMove : expectedMoves) {
			PgnTestUtils.assertEqualsMove(expectedMove, currentNode.getNextMove());
			currentNode = currentNode.getNext();
		}
				
		//now check if the two variant are there
		currentNode = currentNode.getPrevious();
		Assert.assertEquals(2, currentNode.getVariations().size());
		
		String[][] expectedVariantMoves = new String[][] {
				{"e7e6", "g1f3"},
				{"e7e5", "d4e5", "d5d4"}};
		
		GameNode variantNode = currentNode;		
		for(int i = 0; i < 2; i++) {
			String[] variant = expectedVariantMoves[i];
			currentNode = variantNode.getVariations().get(i);			
			for(String expectedMove : variant) {
				PgnTestUtils.assertEqualsMove(expectedMove, currentNode.getNextMove());
				currentNode = currentNode.getNext();
			}
		}		
	}
	
}