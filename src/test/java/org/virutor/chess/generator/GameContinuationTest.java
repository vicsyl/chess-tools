package org.virutor.chess.generator;

import org.junit.Assert;
import org.junit.Test;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position.Continuation;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.standard.FenUtils;

/**
 * TODO Test insufficient mating material. 
 * This is not implemented yet and actually raises a lot of questions...
 * @author vaclav
 *
 */
public class GameContinuationTest {

	private static final String MATE_POSITION = "8/8/8/8/8/2k5/4p3/2K5 b - - 0 1";
	
	private static final String[] MATE_MOVES = new String[] { 
		"e2e1q" 
	};
	
	private static final String[] MATE_CONTINUATION_MOVES = new String[] { 
		"e2e1n" 
	};
	
	private static final String STALE_MATE_POSITION = "8/8/8/8/8/3k4/4p3/4K3 b - - 0 1";
	
	private static final String[] STALE_MATE_MOVES = new String[] { 
		"d3e3" 
	};
	
	private static final String[] STALE_MATE_CONTINUATION_MOVES = new String[] { 
		"d3e4" 
	};
	
	private static final String THREEFOLD_REP_POSITION = "k7/1p6/1R6/1R5P/4q3/8/6P1/6KQ b - - 0 1";
	
	private static final String[] THREEFOLD_REP_MOVES = new String[] { 
		"e4e1", "g1h2", "e1h4", "h2g1",
		"h4e1", "g1h2", "e1h4", "h2g1", 
		"h4e1" 
	};
	
	private static final String[] THREEFOLD_REP_BUT_ONE_MOVES = new String[] { 
				"g1h2", "e1h4", "h2g1",
		"h4e1", "g1h2", "e1h4", "h2g1", 
		"h4e1" 
	};

	private static final String FIFTY_MOVES_DRAW_POSITION = "k7/1p6/1R6/1R5P/4q3/8/6P1/6KQ b - - 91 1";

	private static final String[] FIFTY_MOVES_DRAR_MOVES = new String[] { 
		"e4e1", "g1h2", "e1h4", "h2g1",
		"h4e1", "g1h2", "e1h4", "h2g1", 
		"h4f2" 
	};
	
	private static final String[] CONTINUATION_MOVES = new String[] { 
		"e4e1", "g1h2", "e1h4", "h2g1",
		"h4e1", "g1h2", "e1h4", "h2g1", 
		"h4h1" 
	};
	
	@Test
	public void mateTest() {

		check3FoldRepetition(MATE_POSITION, MATE_MOVES, Continuation.CHECK_MATE);
		check3FoldRepetition(MATE_POSITION, MATE_CONTINUATION_MOVES, Continuation.POSSIBLE_MOVES);

	}
	
	@Test
	public void staleMateTest() {

		check3FoldRepetition(STALE_MATE_POSITION, STALE_MATE_MOVES, Continuation.STALEMATE);
		check3FoldRepetition(STALE_MATE_POSITION, STALE_MATE_CONTINUATION_MOVES, Continuation.POSSIBLE_MOVES);

	}
		
	@Test
	public void threeFoldRepetitionTest() {

		check3FoldRepetition(THREEFOLD_REP_POSITION, THREEFOLD_REP_MOVES, Continuation._3_FOLD_REPETITION);
		check3FoldRepetition(THREEFOLD_REP_POSITION, CONTINUATION_MOVES, Continuation.POSSIBLE_MOVES);

	}

	@Test
	public void threeFoldRepetitionSetCurrentNodeTest() {

		Game game = check3FoldRepetition(THREEFOLD_REP_POSITION, THREEFOLD_REP_MOVES, Continuation._3_FOLD_REPETITION);
		game.setCurrentGameNode(game.getHeadGameNode().getNext());
		
		check3FoldRepetition(game, THREEFOLD_REP_BUT_ONE_MOVES, Continuation._3_FOLD_REPETITION);

	}
	

	@Test
	public void fiftyMovesRuleTest() {

		check3FoldRepetition(FIFTY_MOVES_DRAW_POSITION, FIFTY_MOVES_DRAR_MOVES, Continuation._50_MOVES_DRAW);
		check3FoldRepetition(FIFTY_MOVES_DRAW_POSITION, CONTINUATION_MOVES, Continuation.POSSIBLE_MOVES);

	}

	private Game check3FoldRepetition(String fen, String[] moves, Continuation checkContinuationAtTheEnd) {
		Game game = new Game(FenUtils.getPositionFromFen(fen));
		return check3FoldRepetition(game, moves, checkContinuationAtTheEnd);
	}
	
	private Game check3FoldRepetition(Game game, String[] moves, Continuation checkContinuationAtTheEnd) {		

		for (String moveStr : moves) {

			Assert.assertEquals(Continuation.POSSIBLE_MOVES, game.getCurrentGameNode().getGeneratedMoves().continuation);

			LongAlgebraicMove laMove = new LongAlgebraicMove(moveStr);
			Move move = LongAlgebraicMove.findMove(laMove, game.getCurrentGameNode().getGeneratedMoves());
			game.doMove(move);
		}
		
		Assert.assertEquals(checkContinuationAtTheEnd, game.getCurrentGameNode().getGeneratedMoves().continuation);
		
		return game;

	}

}
