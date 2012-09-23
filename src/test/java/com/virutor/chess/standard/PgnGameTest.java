package com.virutor.chess.standard;

import java.io.InputStream;

import org.junit.Test;
import org.virutor.chess.standard.PgnGame;

public class PgnGameTest {

	
	@Test
	public void simpleParseTest() throws Exception {
		
		InputStream istream = getClass().getResourceAsStream("/sample_pgn.pgn");
		PgnGame pgnGame = PgnGame.parse(istream);
		
		System.out.println(pgnGame.format());
		
		
		
	}
}
