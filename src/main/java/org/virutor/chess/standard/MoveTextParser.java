package org.virutor.chess.standard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.virutor.chess.model.Game;
import org.virutor.chess.model.Game.ResultExplanation;
import org.virutor.chess.model.GameNode;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position.Continuation;

public class MoveTextParser {

	private static final Logger LOG = Logger.getLogger(MoveTextParser.class); 
	
	private static final Pattern PATTERN_WORD = Pattern.compile("^\\S+");
	private static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\s+");	

	private StringBuilder stringBuilder;
	private Game game;

	public MoveTextParser(StringBuilder stringBuilder, Game game) {
		this.stringBuilder = stringBuilder;
		this.game = game;
	};
	
	public void parse() {
		
		parse(game.getHeadGameNode());
		
		
		
	}
	
	private void parse(GameNode gameNode) {

	
		while(stringBuilder.length() > 0) {
			
			Matcher whiteSpaceMacther = PATTERN_WHITESPACE.matcher(stringBuilder);
			if(whiteSpaceMacther.find()) {
				stringBuilder.delete(0,whiteSpaceMacther.group().length());
			}		
			
			Matcher matcher = PATTERN_WORD.matcher(stringBuilder);	
			if(!matcher.find()) {
				break;
			}
			
			String word = matcher.group();
	
			StringBuilder comment = getComment(stringBuilder);		
			if(comment != null) {
				LOG.debug("Comment : " + comment);
				gameNode.setComment(comment.toString());
				continue;
			}
			
			StringBuilder variationStringBuilder = getVariation(stringBuilder);
			if(variationStringBuilder != null) {

				GameNode previous = gameNode.getPrevious();
				
				GameNode variationNode = previous.getNewVariationNode();
				
				MoveTextParser variationMoveTestParser = new MoveTextParser(variationStringBuilder, game);
				variationMoveTestParser.parse(variationNode);				
				//TODO more variation for one position!!! 
				
				//TODO handle comment
				LOG.debug("Variation : " + variationStringBuilder);
				continue;
			}
			
			
	
			//interesting chars not at the beginning
			word = biteEnd(word, '(');
			word = biteEnd(word, '{');
			word = biteEnd(word, '.');
			word = biteEnd(word, ';');
					
			if(biteStart(word, '.', stringBuilder))  {
				continue;
			}

			//bite results must be befor bute digits!!!
			if(biteResults(word, stringBuilder)) {
				continue;
			}
			if(biteDigit(word, stringBuilder)) {
				continue;
			}			
		
			//it *must* be a SanMove 
			//TODO check for exception
			Move move = SanMove.parse(word, gameNode.getPosition(), gameNode.getGeneratedMoves());
			gameNode = game.doMove(move, gameNode);
			stringBuilder.delete(0, word.length());			
						
			Continuation continuation = gameNode.getPosition().getContinuation();
			
			//TODO not, this might apply only to variation (not the whole game)			
			if(continuation == Continuation.CHECK_MATE) {
				game.setResultExplanation(ResultExplanation.MATE);
			} else if(continuation == Continuation.STALEMATE) {
				game.setResultExplanation(ResultExplanation.STALE_MATE);
			}			
		}
		
	}
	
	private static boolean biteResults(String str, StringBuilder stringBuilder) {
		
		for(String strResult : PgnGame.STRING_RESULTS.keySet()) {
			if(str.startsWith(strResult)) {
				stringBuilder.delete(0, strResult.length());
				return true;
			}			
		}
		return false;		
	}
	

	private static StringBuilder getVariation(StringBuilder stringBuilder) {
		
		if(stringBuilder.length() == 0 || stringBuilder.charAt(0) != '(') {
			return null;
		}
		
		int left = 1;
		int fromIndex = 1;
		
		while(left != 0) {
			int indexLeft = stringBuilder.indexOf("(", fromIndex);
			int indexRight = stringBuilder.indexOf(")", fromIndex);
			if(indexRight == -1) {
				//TODO variation not ended
				//TODO HANDLE !!!
			}
			if(indexLeft < indexRight && indexLeft != -1) {
				fromIndex = indexLeft + 1;
				left++;
			} else {
				fromIndex = indexRight + 1;
				left--;
			}
		}
		StringBuilder newStringBuilder = new StringBuilder(stringBuilder.subSequence(1, fromIndex-1));
		stringBuilder.delete(0, fromIndex);
		
		return newStringBuilder;
		
	}
	
	private static StringBuilder getComment(StringBuilder stringBuilder) {

		StringBuilder newStringBuilder = null;
		
		switch(stringBuilder.charAt(0)) {

			case ';':				
				
				stringBuilder.delete(0, 1);
				
				int indexN = stringBuilder.indexOf("\n");
					
				// int indexR = stringBuilder.indexOf("\r");
				// TODO fix Unix like this if(indexR > 0 && indexR < indexN - 1)  {}
				
				if(indexN == -1) {
					newStringBuilder = new StringBuilder(stringBuilder);
					stringBuilder.delete(0, stringBuilder.length());
					return newStringBuilder;
				}
				
				newStringBuilder = new StringBuilder(stringBuilder.subSequence(0, indexN));
				stringBuilder.delete(0, indexN);
				return newStringBuilder;

			case '{':
				
				int indexRight = stringBuilder.indexOf("}");
				if(indexRight == -1) {
					//TODO comment not ended
					//TODO HANDLE !!!
				}
				
				newStringBuilder = new StringBuilder(stringBuilder.subSequence(1, indexRight));
				stringBuilder.delete(0, indexRight + 1);
				
				return newStringBuilder;
				
			default:
				return null;
		}
		
	}
	
	private static String biteEnd(String str, char end) {
		String oneCharString = String.valueOf(end);
		if(str.contains(oneCharString) && !str.startsWith(oneCharString)) {
			str = str.substring(0, str.indexOf(oneCharString));
		}
		return str;
	}
	
	private static boolean biteStart(String str, char start, StringBuilder stringBuilder) {
		if(str.length() == 0 || str.charAt(0) != start) {
			return false;
		}
		while(str.length() > 0 && str.charAt(0) == start) {
			str = str.substring(1);
			stringBuilder.delete(0, 1);
		}
		return true;
	}	
	
	private static boolean biteDigit(String str, StringBuilder stringBuilder) {

		if(str.length() == 0 || !Character.isDigit(str.charAt(0))) {
			return false;
		}
		
		while(str.length() > 0 && Character.isDigit(str.charAt(0))) {
			str = str.substring(1);
			stringBuilder.delete(0, 1);
		}
		return true;
	
	}
}
