package org.virutor.chess.generator;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;
import org.virutor.chess.model.generator.ZobristHashing;
import org.virutor.chess.standard.FenUtils;


@RunWith(Parameterized.class)
public class ZobristHashTest {
	
	private static final int MAX_DEPTH = 4;

	private final String fen;
	private final Long expectedHash;	
	
	public ZobristHashTest(String fen, Long expectedHash) {
		this.fen = fen;
		this.expectedHash = expectedHash;
	}
	
	@Parameters
	public static Collection<Object[]> getParameters() {
		
		Object[][] objectArrays = {
				
				{
				//starting position
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
				0x463b96181691fc9cL				
				},

				{
				//position after e2e4
				"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
				0x823c9b50fd114196L
				},

				{
				//position after e2e4 d75
				"rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2",
				0x0756b94461c50fb0L
				},

				{
				//position after e2e4 d7d5 e4e5
				"rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2",
				0x662fafb965db29d4L
				},

				{
				//position after e2e4 d7d5 e4e5 f7f5
				"rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3",
				0x22a48b5a8e47ff78L
				},

				{
				//position after e2e4 d7d5 e4e5 f7f5 e1e2
				"rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR b kq - 0 3",
				0x652a607ca3f242c1L
				},

				{
				//position after e2e4 d7d5 e4e5 f7f5 e1e2 e8f7
				"rnbq1bnr/ppp1pkpp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR w - - 0 4",
				0x00fdd303c946bdd9L
				},
				
				{
				//position after a2a4 b7b5 h2h4 b5b4 c2c4
				"rnbqkbnr/p1pppppp/8/8/PpP4P/8/1P1PPPP1/RNBQKBNR b KQkq c3 0 3",
				0x3c8123ea7b067637L
				},

				{
				//position after a2a4 b7b5 h2h4 b5b4 c2c4 b4c3 a1a3
				"rnbqkbnr/p1pppppp/8/8/P6P/R1p5/1P1PPPP1/1NBQKBNR b Kkq - 0 4",
				0x5c3f9b829b279560L
				}
				
		};
		
		return Arrays.<Object[]>asList(objectArrays);
	
		
	}
	
	@Test
	public void setHashTest() {
		Position position = FenUtils.getPositionFromFen(fen);
		Assert.assertEquals(expectedHash.longValue(), position.hash);		
	}
	
	@Test
	public void updateHashTest() {
		
		Position position = FenUtils.getPositionFromFen(fen);
		checkUpdateHash(position, 0);
	}
	
	private void checkUpdateHash(Position position, int depth) {
		
		if(depth == MAX_DEPTH) {
			return;
		}
		GeneratedMoves generatedMoves = MoveGenerator.generateMoves(position);
		
		for(int i = 0; i < generatedMoves.moves.size(); i++) {
		
			Position newPosition = generatedMoves.position.get(i);
			long hash = newPosition.hash;
			ZobristHashing.setPositionHash(newPosition);
			
			if(newPosition.hash != hash) {
				int j = 0;
			}
			Assert.assertEquals(newPosition.hash, hash);
			
			checkUpdateHash(newPosition, depth + 1);
						
		}
		
	}	
	
}
