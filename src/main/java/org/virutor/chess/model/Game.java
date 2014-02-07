package org.virutor.chess.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;
import org.virutor.chess.model.generator.RepetitionGameContext;
import org.virutor.chess.standard.SanMove;

public class Game {

	//join these two enums??
	public static enum Result {
		UNRESOLVED,
		DRAW,
		WHITE_WINS,
		BLACK_WINS
	}
	
	public static enum ResultExplanation {
		
		MATE,
		STALE_MATE,
		_50_MOVES_RULE,
		INSUFFICIENT_MATERIAL,
		AGREED_DRAW,
		SURRENDER,
		TIME_UP,
		FORFEIT,
		THREE_FOLD_REPETITION
	}
		
	private Result result = Result.UNRESOLVED;
	private ResultExplanation resultExplanation = null;
	
	private GameNode headGameNode;
	private GameNode currentGameNode;
	private RepetitionGameContext repetitionGameContext = new RepetitionGameContext();
	
	public static Game newGameFromStartingPosition() {
		return new Game(Position.getStartPosition());
	}
	
	public Game(Position startingPosition) {
		setUpStartingPosition(startingPosition);
	}	
	
	public void setUpStartingPosition(Position position) {
		
		headGameNode = new GameNode();
		currentGameNode = headGameNode;
		
		headGameNode.position = position;
		headGameNode.generatedMoves = MoveGenerator.generateMoves(position);
		// really??
		headGameNode.ordinalNumber = position.fullMoveClock; 
		repetitionGameContext.clear();
		
	}
	
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public ResultExplanation getResultExplanation() {
		return resultExplanation;
	}

	public void setResultExplanation(ResultExplanation resultExplanation) {
		this.resultExplanation = resultExplanation;
	}

	/**
	 * When called from outside of this class (see MoveTextParser) the caller must ensure that
	 * repetition context is recomputed once the game node is set...
	 * Better yet, make this method private/remove it and always keep the repetition context up-to-date 
	 * 
	 * @param move
	 * @param gameNode
	 * @return
	 */
	public static GameNode doMove(Move move, GameNode gameNode) {
		
		if(gameNode.generatedMoves.continuation != Position.Continuation.POSSIBLE_MOVES) {
			throw new RuntimeException("This line has already a result :" + gameNode.generatedMoves.continuation);
		}
		
		//TODO implement + delegate doMove(Move move) to here as well!!
		//TODO decide on "overriding strategy"

		int index = gameNode.generatedMoves.moves.indexOf(move);
		if(index == -1) {
			throw new IllegalArgumentException("Cannot do this move");
		}
		gameNode.nextMove = move;
		
		SanMove san = new SanMove(move, gameNode.position, gameNode.generatedMoves);
		gameNode.sanMove = san.toString();
		
		gameNode.nextPosition = MoveGenerator.doMove(gameNode.position, gameNode.nextMove, gameNode.generatedMoves);
				
		GameNode newGameNode = new GameNode();
		gameNode.next = newGameNode;
		
		newGameNode.position = gameNode.nextPosition;
		boolean whiteToMove = newGameNode.position.colorToMove == Position.COLOR_WHITE;
		newGameNode.ordinalNumber = whiteToMove ? gameNode.ordinalNumber + 1 : gameNode.ordinalNumber;
		newGameNode.generatedMoves = MoveGenerator.generateMoves(newGameNode.position);
		
		newGameNode.previous = gameNode;
				
		return newGameNode;		
		
	}
	
	public void doMove(Move move) {				
		currentGameNode = doMove(move, currentGameNode);		
		//TODO this could be done only in  doMove(Move move, GameNode gameNode) if only it's made private - see the comment there
		int rep = repetitionGameContext.addAndGetRepetitions(getCurrentPosition());
		if(rep >= 3) {
			currentGameNode.generatedMoves = GeneratedMoves._3_FOLD_REPETION_GENERATED_MOVES; 
		}
	}	
	
	@Deprecated
	public List<Move> getMoves() {

		List<Move> moves = new ArrayList<Move>();
		GameNode node = headGameNode;
		while(node.nextMove != null) {
			moves.add(node.nextMove);
			node = node.next;
		}
		return moves;
		
	}
	
	public GameNode getHeadGameNode() {
		return headGameNode;
	}
	
	//TODO check for similar idioms throughout code
	public GameNode getTailGameNode() {
		GameNode node = headGameNode;
		while(node.next != null) {
			node = node.next;
		}
		return node;
	}

	public Position getCurrentPosition() {
		return currentGameNode.getPosition();
	}
	
	public GameNode getCurrentGameNode() {
		return currentGameNode;
	}

	public void setCurrentGameNode(GameNode currentGameNode) {
		if(currentGameNode == null) {
			throw new NullPointerException();
		}
		this.currentGameNode = currentGameNode;
		recalculateRepetitionContext();
		
	}
	
	private void recalculateRepetitionContext() {
		// recalculate the repetition context
		repetitionGameContext.clear();
		Queue<GameNode> nodes = new LinkedList<GameNode>();
		GameNode node = currentGameNode;
		do {
			nodes.add(node);
			if (node.position.halfMoveClock == 0) {
				break;
			}
			node = node.previous;
		} while (node != null);

		while (!nodes.isEmpty()) {
			node = nodes.poll();
			int rep = repetitionGameContext.addAndGetRepetitions(node.position);
			if (rep >= 3 && node.generatedMoves.continuation != Position.Continuation._3_FOLD_REPETITION) {
				node.generatedMoves = GeneratedMoves._3_FOLD_REPETION_GENERATED_MOVES;
			}
		}

	}
	
	public void setCurrentGameNodeToTail() {
		setCurrentGameNode(getTailGameNode());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headGameNode == null) ? 0 : headGameNode.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((resultExplanation == null) ? 0 : resultExplanation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (headGameNode == null) {
			if (other.headGameNode != null)
				return false;
		} else if (!headGameNode.equals(other.headGameNode))
			return false;
		if (result != other.result)
			return false;
		if (resultExplanation != other.resultExplanation)
			return false;
		return true;
	}		
	
}
