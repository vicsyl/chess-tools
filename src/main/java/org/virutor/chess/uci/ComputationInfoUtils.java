package org.virutor.chess.uci;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.MoveGenerator.GeneratedMoves;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.standard.SanMove;

public class ComputationInfoUtils {

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss,SSS");

	public static String formatTime(ComputationInfo computationInfo) { 
		return TIME_FORMAT.format(new Date(computationInfo.getTime()));				
	}
	
	//TODO cetralize code common with formatCurrMove method 
	public static String formatVariation(ComputationInfo computationInfo, Position position) {

		List<LongAlgebraicMove> laMoves = computationInfo.getPv();
		if(CollectionUtils.isEmpty(laMoves)) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		GeneratedMoves generatedMoves = MoveGenerator.generateMoves(position);
		
		for(LongAlgebraicMove laMove : laMoves) {
			
			Move move = LongAlgebraicMove.findMove(laMove, generatedMoves);
			if(move == null) {
				sb.append(" ?? ");
				break;
			}
			SanMove san = new SanMove(move, position, generatedMoves);
			sb.append(san.toString() + " ");
			
			position = MoveGenerator.doMove(position, move);
			generatedMoves = MoveGenerator.generateMoves(position);
		}
		return sb.toString();
	}
	
	public static String formatCurrMove(ComputationInfo computationInfo, Position position) {	
	
		LongAlgebraicMove currMove = computationInfo.getCurrMove();
		StringBuilder sb = new StringBuilder("  ");
		
		GeneratedMoves generatedMoves = MoveGenerator.generateMoves(position);
		
		Move move = LongAlgebraicMove.findMove(currMove, generatedMoves);
		if(move == null) {
			return "";
		}
		SanMove san = new SanMove(move, position, generatedMoves);
		sb.append(san.toString());

		if(computationInfo.getCurrMoveNumber() != null) {
			sb.append("(" + computationInfo.getCurrMoveNumber() + "/" + generatedMoves.moves.size() + ")");
		}
		
		return sb.toString();
	}
	
	
	
	public static void unknown(ComputationInfo columnIndex) {
		/*
		ComputationInfo computationInfo = computationInfos.get(rowIndex - 1);  
		switch(columnIndex) {
			case 0:
				return computationInfo.getDepth() == null ? "" : computationInfo.getDepth();
			case 1:
				if(computationInfo.getTime() == null) {
					return "";
				}
				return formatTime(computationInfo.getTime());
			case 2:
				if(computationInfo.getNodes() == null) {
					return "";
				}
				return NODE_FORMAT.format(computationInfo.getNodes().longValue()); 
			case 3:
				if(computationInfo.getNps() == null) {
					return "";
				}
				return NODE_FORMAT.format(computationInfo.getNps().longValue()); 
			case 4:
				return computationInfo.getScore() == null ? "" : computationInfo.getScore(); 
			case 5:
				return "  " + formatVariation(computationInfo.getPv());
			case 6:
				if(rowIndex > 1 || computationInfo.getCurrMove() == null) {
					return "";
				}
				return formatCurrMove(computationInfo);
			default:
				return "";
		}
		*/
		
	}
	
}
