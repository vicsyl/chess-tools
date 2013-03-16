package com.virutor.chess.model;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.model.Position;
import org.virutor.chess.standard.FenUtils;

@RunWith(Parameterized.class)
public class FenTest {

	private String fenString;
	
	public FenTest(String fenString) {
		this.fenString = fenString;
	}

	@Parameters
	public static Collection<String[]> getData() {

		return Arrays.asList(new String[][] {
			new String[] {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"},
			new String[] {"r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1"},
			new String[] {"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1"},
			new String[] {"r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"}
		});
	}
	
	/**
	 * TODO add asserts
	 */
	@Test
	public void fenToPosition() {
		
		Position position = new Position();
		FenUtils.setFen(fenString, position);		
		System.out.println(UIUtils.prettyPositionString(position));
	}
	
	@Test
	public void positionToFen() {
		
		Position position = new Position();
		FenUtils.setFen(fenString, position);		
		System.out.println(UIUtils.prettyPositionString(position));
		String fenBack = FenUtils.positionToFen(position);
		Assert.assertEquals(fenString, fenBack);
		
	}
	
}
