package org.virutor.chess.model;

import java.util.ArrayList;
import java.util.List;

import org.virutor.chess.model.generator.MoveGenerator;

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
		FORFEIT		
	}
		
	private Result result = Result.UNRESOLVED;
	private ResultExplanation resultExplanation = null;
	private GameNode headGameNode = new GameNode();
	private GameNode currentGameNode = headGameNode;

	public static Game newGameFromStartingPosition() {
		return new Game(Position.getStartPosition());
	}
	
	public Game(Position startingPosition) {
		headGameNode.position = startingPosition;
		headGameNode.generatedMoves = MoveGenerator.generateMoves(startingPosition);
		// really??
		headGameNode.ordinalNumber = 1;		
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

	
	public GameNode doMove(Move move, GameNode gameNode) {
		
		//TODO implement + delegate doMove(Move move) to here as well!!
		//TODO decide on "overriding strategy"

		int index = gameNode.generatedMoves.moves.indexOf(move);
		if(index == -1) {
			throw new IllegalArgumentException("Cannot do this move");
		}
		gameNode.nextMove = move;
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

	public GameNode getCurrentGameNode() {
		return currentGameNode;
	}

	public void setCurrentGameNode(GameNode currentGameNode) {
		if(currentGameNode == null) {
			throw new NullPointerException();
		}
		this.currentGameNode = currentGameNode;
	}
	
}
