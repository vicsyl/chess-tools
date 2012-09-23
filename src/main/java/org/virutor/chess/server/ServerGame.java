package org.virutor.chess.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.virutor.chess.model.Position;

public class ServerGame {

	public static long getTime(long minutes, long seconds) {
		return 1000l*seconds + 1000l*60l*minutes; 
	}
	
	private long[] totalTimeLimit = new long[2];	
	
	private long remainingTime[] = new long[2];	
	private Date lastTimeStarted[] = new Date[2];
	
	private long[] fisherTime = new long[2];
	private List<TimeChangeListener> timeChangeListeners = new ArrayList<TimeChangeListener>();
	
	
	private class ListenerTimerTask extends TimerTask {

		private byte color;		
		public ListenerTimerTask(byte color) {
			this.color = color;
		}

		@Override
		public void run() {
			long time = lastTimeStarted[color].getTime() - new Date().getTime() + remainingTime[color];
			notifyListeners(color, time);
		}
		
	} 	
	
	private Timer[] timers = new Timer[2];

	private void notifyListeners(byte color, long time) {
		for(TimeChangeListener listener : timeChangeListeners) {
			listener.timeChanged(time, color);
		}
	}
	
	private void notifyListeners(byte color) { 
		notifyListeners(color, remainingTime[color]);
	}
	
	private void notifyListeners() {
		notifyListeners(Position.COLOR_WHITE);
		notifyListeners(Position.COLOR_BLACK);		
	}
	
	public void startTimeForColor(byte color) {
		Position.assertPieceColor(color);
		
		if(lastTimeStarted[1-color] != null) {
			remainingTime[1-color] -= (getPlusTime(lastTimeStarted[1-color]) - fisherTime[1-color]) ;
			notifyListeners((byte)(1-color));
			if(timers[1-color]!=null) {
				timers[1-color].cancel();
			}			
			//notify listener and switch of the timer listener(?)
		}
		
		Date now = new Date();
		lastTimeStarted[color] = now;
		Date firstTime = new Date((remainingTime[color] % 1000) + now.getTime());
		timers[color] = new Timer();
		timers[color].scheduleAtFixedRate(new ListenerTimerTask(color), firstTime, 1000);
		
		
		
	}
	
	private long getPlusTime(Date lastTimeStarted) {
		return new Date().getTime() - lastTimeStarted.getTime();
	}

	
	public void restart() {
		//TODO let's set some reasonable defaults	
		setTotalTime(getTime(5,0), Position.COLOR_WHITE);
		setTotalTime(getTime(5,0), Position.COLOR_BLACK);
		setFisherTime(0, Position.COLOR_BLACK);
		setFisherTime(0, Position.COLOR_WHITE);
		remainingTime[Position.COLOR_WHITE] = totalTimeLimit[Position.COLOR_WHITE]; 
		remainingTime[Position.COLOR_BLACK] = totalTimeLimit[Position.COLOR_BLACK]; 
		lastTimeStarted[Position.COLOR_WHITE] = null;
		lastTimeStarted[Position.COLOR_WHITE] = null;
		notifyListeners();
		for(int i = 0; i < 2; i++) {
			if(timers[i] != null) {
				timers[i].cancel();
			}
			//timers[i] = new Timer();
			
		}
	}
	
	
	private void setTotalTime(long time, byte color) {
		Position.assertPieceColor(color);
		totalTimeLimit[color] = time;
	}	

	private void setFisherTime(long time, byte color) {
		Position.assertPieceColor(color);
		fisherTime[color] = time;
	}	
	
	public void addTimeChangeListener(TimeChangeListener listener) {
		timeChangeListeners.add(listener);
	}
	public void removeTimeChangeListener(TimeChangeListener listener) {
		timeChangeListeners.remove(listener);
	}
	
	
}
