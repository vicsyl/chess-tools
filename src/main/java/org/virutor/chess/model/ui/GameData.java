package org.virutor.chess.model.ui;

import java.util.List;

import org.virutor.chess.standard.time.TimeControl;


//TODO solve the ambiguity between this and pgn properties...
@Deprecated
public class GameData {
	
	private String white;
	private String black;	
	private List<TimeControl> timeControls;
	
	public String getWhite() {
		return white;
	}
	public void setWhite(String white) {
		this.white = white;
	}
	public String getBlack() {
		return black;
	}
	public void setBlack(String black) {
		this.black = black;
	}
	public List<TimeControl> getTimeControls() {
		return timeControls;
	}
	public void setTimeControls(List<TimeControl> timeControls) {
		this.timeControls = timeControls;
	}
	
}
