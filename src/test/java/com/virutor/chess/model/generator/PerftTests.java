package com.virutor.chess.model.generator;

import static org.virutor.chess.model.Move.CASTLE_EP_FLAG_EP;
import static org.virutor.chess.model.Move.CASTLE_EP_FLAG_NO_CASTLE;
import static org.virutor.chess.model.Piece.NO_PIECE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.Position.Continuation;
import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;
import org.virutor.chess.standard.FenUtils;

import com.virutor.chess.model.UIUtils;

@RunWith(Parameterized.class)
public class PerftTests {

	private static int counter = 0;
	
	private PerftTestData data;

	public PerftTests(PerftTestData data) {
		this.data = data;
	}

	public static class PerftTestData {
		
		String fenString;
		long[] nodes;
		long[] captures;
		long[] ep;
		long[] castles;
		long[] promotions;
		long[] checks;
		long[] checkMates;
		
		public void prettyPrint() {
			System.out.println("Fen: " + fenString);
			prettyPrintLongs("Nodes", nodes);
			prettyPrintLongs("Captures", captures);
			prettyPrintLongs("Ep", ep);
			prettyPrintLongs("Castles", castles);
			prettyPrintLongs("Promotions", promotions);
			prettyPrintLongs("Checks", checks);
			prettyPrintLongs("CheckMatse", checkMates);
		}
		
		private void prettyPrintLongs(String metric, long[] longs){
			System.out.println("Metric: " + metric);
			for(long l : longs) {
				System.out.println(l);
			}
		}
	}
	
	@Parameters
	public static Collection getData(){
		
		List<Object[]> ret = new ArrayList<Object[]>();
		
		PerftTestData data = new PerftTestData();		
		data.fenString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		data.nodes = new long[] {20, 400, 8902, 197281, 4865609/*, 119060324*/};
		data.captures = new long[] {0, 0, 34, 1576, 82719, 82719};
		data.ep = new long[] {0, 0, 0, 0, 258, 5248};
		data.castles = new long[] {0, 0, 0, 0, 0, 0};
		data.promotions = new long[] {0, 0, 0, 0, 0, 0};
		data.checks = new long[] {0, 0, 12, 469, 27351, 809099};
		data.checkMates = new long[] {0, 0, 0, 8, 347, 10828};
		ret.add(new Object[]{data});
		
		//the FEN string was changed to contain '0 1' at the end 
		// (these values were missing and the FEN considered invalid) 
		data = new PerftTestData();
		data.fenString = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
		data.nodes = new long[] {48, 2039, 97862, 4085603/*, 193690690*/};
		data.captures = new long[] {8, 351, 17102, 757163, 35043416};
		data.ep = new long[] {0, 1, 45, 1929, 73365};
		data.castles = new long[] {2, 91, 3162, 128013, 4993637};
		data.promotions = new long[] {0, 0, 0, 15172, 8392};
		data.checks = new long[] {0, 3, 993, 25523, 3309887};
		data.checkMates = new long[] {0, 0, 1, 43, 30171};
		ret.add(new Object[]{data});
				
		//the FEN string was changed to contain '0 1' at the end 
		// (these values were missing and the FEN considered invalid) 
		data = new PerftTestData();
		data.fenString = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1";
		data.nodes = new long[] {14, 191, 2812, 43238, 674624, 11030083/*, 178633661*/};
		data.captures = new long[] {1, 14, 209, 3348, 52051, 940350, 14519036};
		data.ep = new long[] {0, 0, 2, 123, 1165, 33325, 294874};
		data.castles = new long[] {0, 0, 0, 0, 0, 0, 0};
		data.promotions = new long[] {0, 0, 0, 0, 0, 7552, 140024};
		data.checks = new long[] {2, 10, 267, 1680, 52950, 452473, 12797406};
		data.checkMates = new long[] {0, 0, 0, 17, 0, 2733, 87};
		ret.add(new Object[]{data});

		data = new PerftTestData();
		data.fenString = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
		data.nodes = new long[] {6, 264, 9467, 422333, 15833292/*, 706045033*/};
		data.captures = new long[] {0, 87, 1021, 131393, 2046173, 210369132};
		data.ep = new long[] {0, 0, 4, 0, 6512, 212};
		data.castles = new long[] {0, 6, 0, 7795, 0, 10882006};
		data.promotions = new long[] {0, 48, 120, 60032, 329464, 81102984};
		data.checks = new long[] {0, 10, 38, 15492, 200568, 26973664};
		data.checkMates = null; //not available
		ret.add(new Object[]{data});
		
		return ret;
	}
	
	
	@Test
	public void basicPerftTest() throws Exception {
		
		Position position = new Position();
		FenUtils.setFen(data.fenString, position);
		
		System.out.println("Position: \n" + UIUtils.prettyPositionString(position));
		
		PerftTestData runtimeData = new PerftTestData();
		runtimeData.captures = new long[data.captures.length];
		runtimeData.castles = new long[data.castles.length];
		runtimeData.checkMates = new long[data.nodes.length];
		runtimeData.checks = new long[data.checks.length];
		runtimeData.ep = new long[data.ep.length];
		runtimeData.nodes = new long[data.nodes.length];
		runtimeData.promotions = new long[data.promotions.length];
		
		recursiveSearch(position, runtimeData, 0, data.nodes.length, new ArrayList<Move>());	
		
		assertLongs("Nodes", data.nodes, runtimeData.nodes, data.nodes.length);
		assertLongs("Captures", data.captures, runtimeData.captures, data.nodes.length);
		assertLongs("Castles", data.castles, runtimeData.castles, data.nodes.length);
		assertLongs("Ep", data.ep, runtimeData.ep, data.nodes.length);
		assertLongs("Promotions", data.promotions, runtimeData.promotions, data.nodes.length);		
		if(data.checkMates != null) {
			assertLongs("Check mates", data.checkMates, runtimeData.checkMates, data.nodes.length - 1);
		}
		//TODO assertLongs("Checks", data.checks, runtimeData.checks);
				
	}
	
	private static void assertLongs(String metric, long[] expected, long[] actual, int length) {
		
		for(int i = 0; i < length; i++) {
			Assert.assertEquals(metric + " not equal in depth " + i, expected[i], actual[i]);
		}
		
	}
	
	private void recursiveSearch(Position position, PerftTestData runtimeData, int depth, int maxDepth, List<Move> moves) {
		
		if(depth >= maxDepth) {
			return;
		}
		
		GeneratedMoves generatedMoves = MoveGenerator.generateMoves(position);		
		
		if(generatedMoves.continuation == Position.Continuation.CHECK_MATE) {
			runtimeData.checkMates[depth - 1]++; //depth - 1 ??
			return;
		}
		
		runtimeData.nodes[depth] += generatedMoves.moves.size();
		for(Move move : generatedMoves.moves) {
			if(move.piece_captured != NO_PIECE) {
				runtimeData.captures[depth]++;
			}
			if(move.castle_ep_flag != CASTLE_EP_FLAG_NO_CASTLE) {
				if(move.castle_ep_flag == CASTLE_EP_FLAG_EP) {
					runtimeData.ep[depth]++;
				} else {
					runtimeData.castles[depth]++;					
				}
			} else if(move.piece_promoted != NO_PIECE) {
				runtimeData.promotions[depth]++;			
			}
		}
		
		for(Position newPosition : generatedMoves.position) {
			if(depth == 0) {
				System.out.println("" + (generatedMoves.position.indexOf(newPosition) + 1) + "/" + generatedMoves.position.size());
			} 
			
			ArrayList<Move> newLocalList = new ArrayList<Move>(moves);
			newLocalList.add(generatedMoves.moves.get(generatedMoves.position.indexOf(newPosition)));
			
			recursiveSearch(newPosition, runtimeData, depth+1, maxDepth, newLocalList);
		}
	}
	
}
