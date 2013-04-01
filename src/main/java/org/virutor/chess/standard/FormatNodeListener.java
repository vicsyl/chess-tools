package org.virutor.chess.standard;

import org.virutor.chess.model.GameNode;

public interface FormatNodeListener {

	void beforeNode(CharSequence charSequence);
	void afterNode(CharSequence charSequence, GameNode gameNode);
	
}
