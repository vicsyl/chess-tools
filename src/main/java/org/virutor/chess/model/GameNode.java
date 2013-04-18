package org.virutor.chess.model;

import java.util.ArrayList;
import java.util.List;

import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;

public class GameNode {

		String sanMove;
		Position position;
		GeneratedMoves generatedMoves; 
		Move nextMove;
		Position nextPosition;
		List<GameNode> variations = new ArrayList<GameNode>();
		GameNode next;
		GameNode previous;
		//TODO continuation - mate / 1-0 / * / +-
		int ordinalNumber;
		String comment;
		
		public GameNode getNewVariationNode() {
			GameNode ret = new GameNode();
			ret.ordinalNumber = ordinalNumber;
			ret.position = position;
			//ret.comment = comment; //TODO ???!!
			ret.generatedMoves = generatedMoves;
			ret.previous = previous; //TODO ???!!!
			//ret.variations = null; //TODO ???!!!			
			variations.add(ret);			
			return ret;
		}
		
		public GameNode getPreviousOrMainLine() {
			if(previous == null) {
				return null;				
			}
			if(previous.previous == null) {
				return previous;
			}
			return previous.previous.next;

		}
		
		public String getSanMove() {
			return sanMove;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}
		
		public Position getPosition() {
			return position;
		}
		void setPosition(Position position) {
			this.position = position;
		}
		
		public GeneratedMoves getGeneratedMoves() {
			return generatedMoves;
		}
		void setGeneratedMoves(GeneratedMoves generatedMoves) {
			this.generatedMoves = generatedMoves;
		}
		
		public Move getNextMove() {
			return nextMove;
		}
		void setNextMove(Move nextMove) {
			this.nextMove = nextMove;
		}
		
		public Position getNextPosition() {
			return nextPosition;
		}
		void setNextPosition(Position nextPosition) {
			this.nextPosition = nextPosition;
		}
		
		public List<GameNode> getVariations() {
			return variations;
		}
		void setVariations(List<GameNode> variations) {
			this.variations = variations;
		}
		
		public GameNode getNext() {
			return next;
		}
		void setNext(GameNode next) {
			this.next = next;
		}
		
		public GameNode getPrevious() {
			return previous;
		}
		void setPrevious(GameNode previous) {
			this.previous = previous;
		}
		
		public int getOrdinalNumber() {
			return ordinalNumber;
		}
		void setOrdinalNumber(int ordinalNumber) {
			this.ordinalNumber = ordinalNumber;
		}
		
}
