package org.virutor.chess.model;

import java.util.ArrayList;
import java.util.List;

import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;

/**
 * Add result!
 */
public class GameNode {

		public int getVariationDepth() {
			
			int ret = 0;
			GameNode gn = this;
			
			while(gn.previous != null) {
				if(gn.previous.next != gn) {
					ret++;
				}
				gn = gn.previous;
			}
			return ret;
		}
	
		String sanMove;
		Position position;
		GeneratedMoves generatedMoves; 
		Move nextMove;
		Position nextPosition;
		List<GameNode> variations = new ArrayList<GameNode>();
		GameNode next;
		GameNode previous;
		//TODO remove it and use position.fullMoveClock  
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
		
		@Override
		public String toString() {
			if(nextMove == null) {
				return "move is null";
			}
			return nextMove.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((generatedMoves == null) ? 0 : generatedMoves.hashCode());
			result = prime * result + ((next == null) ? 0 : next.hashCode());
			result = prime * result + ((nextMove == null) ? 0 : nextMove.hashCode());
			result = prime * result + ordinalNumber;
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			result = prime * result + ((sanMove == null) ? 0 : sanMove.hashCode());
			result = prime * result + ((variations == null) ? 0 : variations.hashCode());
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
			GameNode other = (GameNode) obj;
			if (generatedMoves == null) {
				if (other.generatedMoves != null)
					return false;
			} else if (!generatedMoves.equals(other.generatedMoves))
				return false;
			if (next == null) {
				if (other.next != null)
					return false;
			} else if (!next.equals(other.next))
				return false;
			if (nextMove == null) {
				if (other.nextMove != null)
					return false;
			} else if (!nextMove.equals(other.nextMove))
				return false;
			if (ordinalNumber != other.ordinalNumber)
				return false;
			if (position == null) {
				if (other.position != null)
					return false;
			} else if (!position.equals(other.position))
				return false;
			if (sanMove == null) {
				if (other.sanMove != null)
					return false;
			} else if (!sanMove.equals(other.sanMove))
				return false;
			if (variations == null) {
				if (other.variations != null)
					return false;
			} else if (!variations.equals(other.variations))
				return false;
			return true;
		}		
		
}
