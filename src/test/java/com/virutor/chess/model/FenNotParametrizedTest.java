package com.virutor.chess.model;

import junit.framework.Assert;

import org.junit.Test;
import org.virutor.chess.model.Position;
import org.virutor.chess.standard.FenUtils;

public class FenNotParametrizedTest {

	/**
	 * this fixed the bug when full move clock was set incorrectly to 0 
	 * for a starting position (should be 1)
	 */
	@Test
	public void startingPositionTest() {
		final String expected = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		final String actual = FenUtils.positionToFen(new Position().setStartPosition());
		Assert.assertEquals(expected, actual);
	}
	
}
