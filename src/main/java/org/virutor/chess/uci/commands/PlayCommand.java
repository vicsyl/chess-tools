package org.virutor.chess.uci.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.standard.FenUtils;
import org.virutor.chess.standard.time.IncrementalTimeControl;
import org.virutor.chess.standard.time.MovesInSecondsTimeControl;
import org.virutor.chess.standard.time.TimeControl;
import org.virutor.chess.uci.UciConstants;
import org.virutor.chess.ui.model.UiGame;

/**
 * TODO rename to Position/GoCommand or something like this...
 * @author vaclav
 *
 */
public class PlayCommand implements UciCommand {

	public Position initialPosition;
	public List<Move> moves;
	public Integer wtime;
	public Integer btime;
	public Integer winc;
	public Integer binc;
	public Integer movesToGo;
	public boolean infinite;
	
	public PlayCommand(Position initialPosition, List<Move> moves) {		
		this.initialPosition = initialPosition;
		this.moves = moves;
	}

	public void setTimeFromEnvironment() {

		//TODO sand clock time and unknown clock time are just not implemented		

		List<TimeControl> timeControls = UiGame.instance.getGameData().getTimeControls();
		if(CollectionUtils.isEmpty(timeControls)) {
			return;
		}
		
		TimeControl timeControl = timeControls.get(0);		
		if(timeControl == TimeControl.NO_CONTROL) {
			infinite = true;
		} 

		//TODO workaround
		//byte colorToMove = UiGame.instance.getGame().getCurrentPosition().colorToMove;
		
		wtime = Long.valueOf(UiGame.instance.TIMER_CONTROL_FOR_WHITE.getRemainingTime()).intValue();  		
		btime = Long.valueOf(UiGame.instance.TIMER_CONTROL_FOR_BLACK.getRemainingTime()).intValue();  		

		if(timeControl instanceof IncrementalTimeControl) {
			winc = ((IncrementalTimeControl) timeControl).getIncrement();
			binc = winc;
		} else if(timeControl instanceof MovesInSecondsTimeControl) {			
			//TODO test + REDO it's not perfect !!!
			movesToGo = ((MovesInSecondsTimeControl) timeControl).getMoves() - UiGame.instance.getGame().getCurrentPosition().fullMoveClock; 
		}		
		
	}
	
	@Override
	public List<String> getStringCommands() {

		List<String> ret = new ArrayList<String>();
		
		StringBuilder sb = new StringBuilder();
		sb.append(UciConstants.POSITION_);
		
		
		String fen = FenUtils.positionToFen(initialPosition);
		if(FenUtils.INITIAL_POSITION_FEN.equals(fen)) {
			sb.append(UciConstants.STARTPOS + " ");
		} else {
			sb.append(fen + " ");
		}
		
		if(!CollectionUtils.isEmpty(moves)) {
			sb.append(UciConstants.MOVES);
			for(Move move : moves) {
				sb.append(" " + new LongAlgebraicMove(move).toString());
			}
		}
		ret.add(sb.toString());

		sb = new StringBuilder();
		sb.append(UciConstants.GO);
		
		if(infinite) {
			sb.append(" " + UciConstants.INFINITE);
		} else {		
			appendIntegerProperty(UciConstants.WTIME, wtime, sb);
			appendIntegerProperty(UciConstants.BTIME, btime, sb);
			appendIntegerProperty(UciConstants.WINC, winc, sb);
			appendIntegerProperty(UciConstants.BINC, binc, sb);
			appendIntegerProperty(UciConstants.MOVESTOGO, movesToGo, sb);
		}
		
		ret.add(sb.toString());
		return ret;

	}	
	
	private void appendIntegerProperty(String key, Integer integer, StringBuilder sb) {
		if(integer != null) {
			sb.append(" " + key + " " + integer);
		}
	}
	
}
