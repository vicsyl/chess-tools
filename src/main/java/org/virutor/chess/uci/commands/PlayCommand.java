package org.virutor.chess.uci.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.standard.FenUtils;
import org.virutor.chess.uci.UciConstants;

public class PlayCommand implements UciCommand {

	public Position initialPosition;
	public List<Move> moves;
	public Integer wtime;
	public Integer btime;
	public Integer winc;
	public Integer binc;
	public Integer movesToGo;
	
	public PlayCommand(Position initialPosition, List<Move> moves) {
		this.initialPosition = initialPosition;
		this.moves = moves;
	}

	@Override
	public List<String> getStringCommands() {

		List<String> ret = new ArrayList<String>();
		
		StringBuilder sb = new StringBuilder();
		sb.append(UciConstants.POSITION_);
		
		
		String fen = FenUtils.positionToFen(initialPosition);
		if(FenUtils.INITIAL_POSITION_FEN.equals(fen)) {
			sb.append(UciConstants.STARTPOS);
		} else {
			sb.append(fen);
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
		
		appendIntegerProperty(UciConstants.WTIME, wtime, sb);
		appendIntegerProperty(UciConstants.BTIME, btime, sb);
		appendIntegerProperty(UciConstants.WINC, winc, sb);
		appendIntegerProperty(UciConstants.BINC, binc, sb);
		appendIntegerProperty(UciConstants.MOVESTOGO, movesToGo, sb);

		ret.add(sb.toString());
		return ret;

	}	
	
	private void appendIntegerProperty(String key, Integer integer, StringBuilder sb) {
		if(integer != null) {
			sb.append(" " + key + " " + integer);
		}
	}
	
}
