package org.virutor.chess.standard;

import static org.virutor.chess.model.Piece.PIECE_KING;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.virutor.chess.model.Field;
import org.virutor.chess.model.Piece;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.generator.ZobristHashing;

public class FenUtils {
	
	public static final String INITIAL_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"; 

	private static final Logger logger = LogManager.getLogger(FenUtils.class);

	//TODO why doesn't it work with optional groups like this?
	//PATTERN_STRING = " ....  (w|b) ([KQkq]+|\\-) ([a-h][1-8]|\\-) ([\\d]+)? ([\\d]+)?"	
	private static final String PATTERN_STRING = "([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/" +
												"([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)" +
												" (w|b) ([KQkq]+|\\-) ([a-h][1-8]|\\-)\\s*([\\d]*)\\s*([\\d]*)";

	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

	public static Position getPositionFromFen(String fenString) { 
		Position position = new Position();
		setFen(fenString, position);
		return position;
	}

	/**
	 * Parses FEN String (see http://www.thechessdrum.net/PGN_Reference.txt or http://kirill-kryukov.com/chess/doc/fen.html)
	 * and sets the passed position accordingly. Accepts even non-compliant strings that
	 * are missing the fullmove number or halfmove clock, which is sometimes the case
	 *
	 * @param fenString FEN String
	 * @param position the position to be updated
	 */
	public static void setFen(String fenString, Position position) {
		
		Matcher matcher = PATTERN.matcher(fenString);
		
		if(!matcher.find() || matcher.groupCount() < 11) {
			throw new IllegalArgumentException("Wrong fen format: " + fenString);
		}
		
		position.emptyBoard();
		
		for(int i = 1; i < 9; i++) {
			setFen1_8Row(9-i, matcher.group(i), position);
		}
		
		setKingIndices(position);
		
		position.colorToMove = "w".equalsIgnoreCase(matcher.group(9)) ? Position.COLOR_WHITE : Position.COLOR_BLACK;

		setFenCastles(matcher.group(10), position);
		setPossibleEp(matcher.group(11), position);
		
		position.fullMoveClock = 1;
		position.halfMoveClock = 0;
		
		if(matcher.groupCount() > 11 && !StringUtils.isBlank(matcher.group(12))) {
			logger.warn("The fen string ({}) is missing the halfmove clock information, will use the default value of 0", fenString);
			position.halfMoveClock = Integer.parseInt(matcher.group(12));
		} 
			
		if(matcher.groupCount() > 12 && !StringUtils.isBlank(matcher.group(13))) {
			logger.warn("The fen string ({}) is missing the fullmove counter information, will use the default value of 1", fenString);
			position.fullMoveClock = Integer.parseInt(matcher.group(13));
		}
		
		ZobristHashing.setPositionHash(position);		
	}

	private static void setKingIndices(Position position) {
		position.kingIndices[0] = Position.OFF_BOARD;		
		position.kingIndices[1] = Position.OFF_BOARD;		
		for(int i = Position.A1; i <= Position.H8; i++) {
			if((position.board[i].color == Position.COLOR_BLACK || position.board[i].color == Position.COLOR_WHITE) && (position.board[i].pieceType == PIECE_KING)) {
				position.kingIndices[position.board[i].color] = i;
			}
		}
		if(position.kingIndices[0] == -1 || position.kingIndices[1] == -1) {
			throw new RuntimeException("At least one of the kings not found on the board when setting king indices");
		}
	}
	
	private static void setPossibleEp(String epFen, Position position) {
		
		position.possibleEpIndex = Position.OFF_BOARD;
		if(epFen.equals("-")) {
			return;
		}
		position.possibleEpIndex = Position.parseField(epFen);
		
	}
	
	
	private static void setFenCastles(String fenCastles, Position position) {
		
		for(int i = 0; i < 4; i++) {
			position.castles[i] = false;
		}
		if(fenCastles.equals("-")) {
			return;
		}
		for(int i = 0; i < fenCastles.length(); i++) {
			switch(fenCastles.charAt(i)) {
				case 'K':
					position.castles[Position.CASTLE_INDEX_WHITE_0_0] = true;
					break;
				case 'Q':
					position.castles[Position.CASTLE_INDEX_WHITE_0_0_0] = true;
					break;
				case 'k':
					position.castles[Position.CASTLE_INDEX_BLACK_0_0] = true;
					break;
				case 'q':
					position.castles[Position.CASTLE_INDEX_BLACK_0_0_0] = true;
					break;				
			}
		}
	}
	
	private static void setFen1_8Row(int row, String fenRow, Position position) {
		
		int column = 1;
		for(int i = 0; i < fenRow.length(); i++) {
			int boardIndex = row*10 + 10 + column;
			switch(fenRow.charAt(i)) {
				case 'r':
					position.board[boardIndex].pieceType =  Piece.PIECE_ROOK;
					position.board[boardIndex].color = Position.COLOR_BLACK;
					break;
				case 'n':
					position.board[boardIndex].pieceType = Piece.PIECE_KNIGHT;
					position.board[boardIndex].color = Position.COLOR_BLACK;
					break;
				case 'b':
					position.board[boardIndex].pieceType = Piece.PIECE_BISHOP;
					position.board[boardIndex].color = Position.COLOR_BLACK;
					break;
				case 'q':
					position.board[boardIndex].pieceType = Piece.PIECE_QUEEN;
					position.board[boardIndex].color = Position.COLOR_BLACK;
					break;
				case 'k':
					position.board[boardIndex].pieceType = Piece.PIECE_KING;
					position.board[boardIndex].color = Position.COLOR_BLACK;
					break;
				case 'p':
					position.board[boardIndex].pieceType = Piece.PIECE_PAWN;
					position.board[boardIndex].color = Position.COLOR_BLACK;
					break;	
				case 'R':
					position.board[boardIndex].pieceType = Piece.PIECE_ROOK;
					position.board[boardIndex].color = Position.COLOR_WHITE;
					break;
				case 'N':
					position.board[boardIndex].pieceType = Piece.PIECE_KNIGHT;
					position.board[boardIndex].color = Position.COLOR_WHITE;
					break;
				case 'B':
					position.board[boardIndex].pieceType = Piece.PIECE_BISHOP;
					position.board[boardIndex].color = Position.COLOR_WHITE;
					break;
				case 'Q':
					position.board[boardIndex].pieceType = Piece.PIECE_QUEEN;
					position.board[boardIndex].color = Position.COLOR_WHITE;
					break;
				case 'K':
					position.board[boardIndex].pieceType = Piece.PIECE_KING;
					position.board[boardIndex].color = Position.COLOR_WHITE;
					break;
				case 'P':
					position.board[boardIndex].pieceType = Piece.PIECE_PAWN;
					position.board[boardIndex].color = Position.COLOR_WHITE;
					break;
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
					column+=Integer.parseInt(String.valueOf(fenRow.charAt(i))) - 1;
					break;
				case '8':
					return;
			}
			column++;
			
		}
		
	}

	
	
	public static String positionToFen(Position position) {
		
		StringBuilder sb = new StringBuilder();
		
		for(int row = 8; row > 0; row--) {			
			int lastFile = 0;
			for(int file = 1; file < 9; file++) {
				Field f = position.getField(row, file);
				if(f.color != Position.COLOR_FREE) {
					if(file - lastFile > 1) {
						sb.append(file - lastFile - 1);
					}
					sb.append(getPieceChar(f.pieceType, f.color));
					lastFile = file;
				}				
			}
			if(lastFile != 8) {
				sb.append(8-lastFile);
			}
			if(row > 1) {
				sb.append("/");
			}
		}

		sb.append(position.colorToMove == Position.COLOR_WHITE ? " w" : " b");
		appendCastling(position, sb);
		appendEp(position, sb);
		sb.append(" " + position.halfMoveClock);
		sb.append(" " + position.fullMoveClock);

		return sb.toString();

	} 
	
	private static char[] pieceChars = new char[] {
		' ', 'P', 'R', 'N', 'B', 'Q', 'K'
	};

	private static char[] castlesChars = new char[] {
		'K', 'Q', 'k', 'q'
	};
	
	private static void appendEp(Position position, StringBuilder sb) {
		if(position.possibleEpIndex == Position.OFF_BOARD) {
			sb.append(" -");
		} else {
			sb.append(" " + Position.FIELD_STRINGS[position.possibleEpIndex]);			
		}
	}
	
	private static void appendCastling(Position position, StringBuilder sb) {
		sb.append(" ");
		boolean flag = false;
		for(int i = 0; i < 4; i++) {
			if(position.castles[i]) {
				sb.append(castlesChars[i]);
				flag = true;
			}
		}
		if(!flag) {
			sb.append("-");
		}
	}
	
	private static char getPieceChar(byte pieceType, byte color) {
		
		char c = pieceChars[pieceType];
		if(color == Position.COLOR_BLACK) {
			c = Character.toLowerCase(c);
		}
		return c;
	}
	
}
