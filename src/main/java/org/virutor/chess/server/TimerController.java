package org.virutor.chess.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.collections.CollectionUtils;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.ui.GameData;
import org.virutor.chess.standard.time.IncrementalTimeControl;
import org.virutor.chess.standard.time.MovesInSecondsTimeControl;
import org.virutor.chess.standard.time.SandClockTimeControl;
import org.virutor.chess.standard.time.TimeControl;
import org.virutor.chess.ui.model.UiGame;
import org.virutor.chess.ui.model.UiGameListener;

//TODO account for UKNOWN / NO TIME CONTROL

public class TimerController implements UiGameListener {

	private byte color;
	
	private TimeControl currentTimeControl;
	private Iterator<TimeControl> iterator;
	private List<TimeChangeListener> timeChangeListeners = new ArrayList<TimeChangeListener>();


	private Timer timer;

	private long remainingTime = -1;
	private long lastTimeStarted;

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
		}
	}

	private void setRemainingTime() {
		if (currentTimeControl == null) {
			return;
		}
		remainingTime = currentTimeControl.getTotalFirstTime();		
		notifyListeners(remainingTime);

	}

	public TimerController(byte color) {
		this.color = color;
	}

	@Override
	public void onGenericChange(UiGameChange uiGameChange) {

		cancelTimer();

		GameData gameData = UiGame.instance.getGameData();
		if(gameData == null || CollectionUtils.isEmpty(gameData.getTimeControls())) {
			iterator = null;
			currentTimeControl = TimeControl.NO_CONTROL;
		} else {
			iterator = UiGame.instance.getGameData().getTimeControls().iterator();
			currentTimeControl = iterator.next();
		}
		lastTimeStarted = 0;
		setRemainingTime();

	}

	@Override
	public void onDoMove(Move move) {

		if (UiGame.instance.getGame().getCurrentPosition().colorToMove == color) {

			lastTimeStarted = System.currentTimeMillis();

			if (timer != null) {
				timer.cancel();
			}
			// REALLY ?
			if (remainingTime == -1) {
				return;
			}
			timer = new Timer();
			timer.scheduleAtFixedRate(new ListenerTimerTask(), (remainingTime % 1000) + 1, 1000);

		} else {

			if (timer != null) {
				cancelTimer();
				setRemainingTimeAfterStop();
			}

		}

	}

	@Override
	public void onUndoMove(Move move) {
		// TODO let's just pretend that the time just moves on when this
		// happens... for now....
	}

	private class ListenerTimerTask extends TimerTask {

		@Override
		public void run() {
			long time = lastTimeStarted - System.currentTimeMillis() + remainingTime;
			System.out.println("Time: " + time);
			notifyListeners(time);
			// TODO not accounted for more TimeControls
			if (time < 0) {
				notifyListenersForfeit();
			}
		}

	}

	
	private void notifyListeners(long time) {
		for (TimeChangeListener listener : timeChangeListeners) {
			listener.timeChanged(time , currentTimeControl);
		}
	}

	private void notifyListenersForfeit() {

		// TODO notified every time
		for (TimeChangeListener listener : timeChangeListeners) {
			listener.onTimeForfeit();
		}
	}

	private void setRemainingTimeAfterStop() {
		
		//the clock wasn't started yet
		if(lastTimeStarted == 0) {
			return;
		}
		remainingTime = lastTimeStarted - System.currentTimeMillis();

		if (currentTimeControl instanceof IncrementalTimeControl) {
			IncrementalTimeControl incrementalTimeControl = (IncrementalTimeControl) currentTimeControl;
			remainingTime += incrementalTimeControl.getIncrement() * 1000;
			notifyListeners(remainingTime);
		}

		if (currentTimeControl instanceof MovesInSecondsTimeControl) {
			MovesInSecondsTimeControl movesInSecondsTimeControl = (MovesInSecondsTimeControl) currentTimeControl;
			int checkAgainst = UiGame.instance.getGame().getCurrentPosition().fullMoveClock;
			if (color == Position.COLOR_BLACK) {
				checkAgainst++;
			}

			
			if (movesInSecondsTimeControl.getMoves() == checkAgainst) {
				
				if(iterator != null && iterator.hasNext()) {
					currentTimeControl = iterator.next();					
				} else {
					iterator = null;
					currentTimeControl = TimeControl.NO_CONTROL;					
				}
				
				remainingTime += currentTimeControl.getTotalFirstTime();		
				notifyListeners(remainingTime);

			}
		}
	}

	public void addTimeChangeListener(TimeChangeListener listener) {
		timeChangeListeners.add(listener);
	}

}
