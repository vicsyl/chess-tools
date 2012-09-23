package com.virutor.chess.model;

import org.junit.Test;
import org.virutor.chess.model.Position;

public class FenTest {

	/**
	 * TODO add asserts
	 */
	@Test
	public void testFen() {
		
		Position position = new Position();

		position.setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0");		
		System.out.println(UIUtils.prettyPositionString(position));

		position.setFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		System.out.println(UIUtils.prettyPositionString(position));

		position.setFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
		System.out.println(UIUtils.prettyPositionString(position));

		position.setFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
		System.out.println(UIUtils.prettyPositionString(position));
		
	}
	
}
