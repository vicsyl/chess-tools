package org.virutor.chess.ui;

import java.util.List;
import java.util.Stack;

import org.virutor.chess.model.GameNode;
import org.virutor.chess.standard.FormatNodeListener;

public class GameTextHighlighter implements FormatNodeListener {

	private Stack<Highlight> nodeHighlightList = new Stack<Highlight>();
	
	public List<Highlight> getNodeHighlightList() {
		return nodeHighlightList;
	}


	public static class Highlight {
		
		public GameNode gameNode;
		public int from;
		public int to;
	}
	
	
	
	@Override
	public void beforeNode(CharSequence charSequence) {
		Highlight highlight = new Highlight();
		highlight.from = charSequence.length();
		nodeHighlightList.add(highlight);
	}

	@Override
	public void afterNode(CharSequence charSequence, GameNode gameNode) {
		Highlight highlight = nodeHighlightList.peek();
		highlight.gameNode = gameNode;
		highlight.to = charSequence.length();
		
	}
	
	public Highlight getHighlightForNode(GameNode gameNode) {
		if(gameNode == null) {
			return null;
		}
		for (Highlight highlight : nodeHighlightList) {
			if(highlight.gameNode == gameNode) {
				return highlight;
			}
		}
		return null;
	}

	public void clear() {
		nodeHighlightList.clear();
	}
	
	
	public Highlight getHighlightForPosition(int position) {	
	
		//TODO binary search might be introduced
		for(Highlight highlightIter : nodeHighlightList) {
			if(highlightIter.from > position) {
				break;
			}
			if(highlightIter.to >= position) {
				return highlightIter;
			}
		}
		return null;
		
	}
}
