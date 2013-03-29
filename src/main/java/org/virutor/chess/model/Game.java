package org.virutor.chess.model;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.virutor.chess.model.Position.Continuation;
import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;

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
		
	private List<Position> positions = new Vector<Position>();
	private List<Move> moves = new Vector<Move>();
	private List<GeneratedMoves> generatedMovesList = new Vector<GeneratedMoves>();
	
	
	public Game(Position startingPosition) {
		positions.add(startingPosition);
		generateMoves();
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

	public void doMove(Move move) {
		
		//TODO makes sense, but causes trouble!!
		/*
		if(result != Result.UNRESOLVED) {
			throw new IllegalStateException("Game already resolved");
		}*/

		int index = generatedMovesList.get(generatedMovesList.size()-1).moves.indexOf(move);
		if(index == -1) {
			throw new IllegalArgumentException("Cannot do this move");
		}		 
		moves.add(move);
		positions.add(generatedMovesList.get(generatedMovesList.size()-1).position.get(index));
		generateMoves();
		
	}	
	
	private void generateMoves() {
		GeneratedMoves generatedMoves = MoveGenerator.generateMoves(positions.get(positions.size()-1));
		generatedMovesList.add(generatedMoves);
		
		//TODO other resolved result types
		if(generatedMoves.continuation == Position.Continuation.CHECK_MATE) {
			result = positions.get(positions.size()-1).colorToMove == Position.COLOR_WHITE ? Result.BLACK_WINS : Result.WHITE_WINS;
		} else if(generatedMoves.continuation == Position.Continuation.STALEMATE) {
			result = Result.DRAW;
		} 
	}

	public List<Position> getPositions() {
		return Collections.unmodifiableList(positions);
	}

	public List<Move> getMoves() {
		return Collections.unmodifiableList(moves);
	}

	public List<GeneratedMoves> getGeneratedMovesList() {
		return generatedMovesList;
	}

	private static <T> T getLastItem(List<? extends T> list) {
		return list.get(list.size() - 1);
	}  
	public GeneratedMoves getLastGeneratedMoves() {
		return getLastItem(generatedMovesList);
	}
	public Position getLastPosition() {
		return getLastItem(positions);
	}
	
	
}
