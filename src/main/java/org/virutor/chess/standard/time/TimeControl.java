package org.virutor.chess.standard.time;

public interface TimeControl {

	public static final TimeControl UNKNOWN = new TimeControl(){
		public long getTotalFirstTime() {
			return -1;
		} 
	};    
	public static final TimeControl NO_CONTROL = new TimeControl(){
		public long getTotalFirstTime() {
			return -1;
		} 
	};
	
	long getTotalFirstTime();
	
}
