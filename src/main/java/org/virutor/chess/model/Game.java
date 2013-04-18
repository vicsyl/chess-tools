package org.virutor.chess.model;

import java.util.ArrayList;
import java.util.List;

import org.virutor.chess.model.generator.MoveGenerator;
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
		FORFEIT		
	}
		
	private Result result = Result.UNRESOLVED;
	private ResultExplanation resultExplanation = null;
	private GameNode headGameNode;
	private GameNode currentGameNode;

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
	}
	
}
