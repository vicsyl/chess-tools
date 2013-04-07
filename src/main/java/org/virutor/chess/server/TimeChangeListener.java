package org.virutor.chess.server;

import org.virutor.chess.standard.time.TimeControl;

public interface TimeChangeListener {

	void timeChanged(long time, TimeControl timeControl);
	void onTimeForfeit(); 
	
}
