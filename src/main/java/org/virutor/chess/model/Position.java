package org.virutor.chess.model;

import static org.virutor.chess.model.Piece.PIECE_BISHOP;
import static org.virutor.chess.model.Piece.PIECE_KING;
import static org.virutor.chess.model.Piece.PIECE_KNIGHT;
import static org.virutor.chess.model.Piece.PIECE_PAWN;
import static org.virutor.chess.model.Piece.PIECE_QUEEN;
import static org.virutor.chess.model.Piece.PIECE_ROOK;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Position implements Cloneable {

	
	public static final int A1 = 21;
	public static final int B1 = 22;
	public static final int C1 = 23;
	public static final int D1 = 24;
	public static final int E1 = 25;
	public static final int F1 = 26;
	public static final int G1 = 27;
	public static final int H1 = 28;

	public static final int A2 = 31;
	public static final int B2 = 32;
	public static final int C2 = 33;
	public static final int D2 = 34;
	public static final int E2 = 35;
	public static final int F2 = 36;
	public static final int G2 = 37;
	public static final int H2 = 38;

	public static final int A3 = 41;
	public static final int B3 = 42;
	public static final int C3 = 43;
	public static final int D3 = 44;
	public static final int E3 = 45;
	public static final int F3 = 46;
	public static final int G3 = 47;
	public static final int H3 = 48;

	public static final int A4 = 51;
	public static final int B4 = 52;
	public static final int C4 = 53;
	public static final int D4 = 54;
	public static final int E4 = 55;
	public static final int F4 = 56;
	public static final int G4 = 57;
	public static final int H4 = 58;

	public static final int A5 = 61;
	public static final int B5 = 62;
	public static final int C5 = 63;
	public static final int D5 = 64;
	public static final int E5 = 65;
	public static final int F5 = 66;
	public static final int G5 = 67;
	public static final int H5 = 68;

	public static final int A6 = 71;
	public static final int B6 = 72;
	public static final int C6 = 73;
	public static final int D6 = 74;
	public static final int E6 = 75;
	public static final int F6 = 76;
	public static final int G6 = 77;
	public static final int H6 = 78;

	public static final int A7 = 81;
	public static final int B7 = 82;
	public static final int C7 = 83;
	public static final int D7 = 84;
	public static final int E7 = 85;
	public static final int F7 = 86;
	public static final int G7 = 87;
	public static final int H7 = 88;

	public static final int A8 = 91;
	public static final int B8 = 92;
	public static final int C8 = 93;
	public static final int D8 = 94;
	public static final int E8 = 95;
	public static final int F8 = 96;
	public static final int G8 = 97;
	public static final int H8 = 98;
	public static final int OFF_BOARD = -1;

	public static final String[] FIELD_STRINGS = new String[] {
		null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, 
		null, "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", null, 
		null, "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", null, 
		null, "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", null, 
		null, "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", null, 
		null, "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", null, 
		null, "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", null, 
		null, "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", null, 
		null, "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", null, 
		null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, 
	};

	public static int getRow(int index) {
		return (index / 10) - 1; 
	}
	
	public static char getFile(int index) {
		return (char)('a' + ((index % 10) - 1));
	}

	public static void assertPieceColor(byte color) {
		if(color != COLOR_BLACK && color != COLOR_WHITE) {
			throw new IllegalArgumentException("Expected COLOR_WHITE or COLOR_BLACK");
		}
	}
	public static void assertColor(byte color) {
		if(color < COLOR_BLACK && color > COLOR_OFF_BOARD) {
			throw new IllegalArgumentException("Invalid color");
		}	
	}
	
	public static final byte COLOR_WHITE = 0; 
	public static final byte COLOR_BLACK = 1; 
	public static final byte COLOR_FREE = 2; 
	public static final byte COLOR_OFF_BOARD = 3; 

	public static final int CASTLE_INDEX_WHITE_0_0 = 0;
	public static final int CASTLE_INDEX_WHITE_0_0_0 = 1;
	public static final int CASTLE_INDEX_BLACK_0_0 = 2;
	public static final int CASTLE_INDEX_BLACK_0_0_0 = 3;


	
	//public Piece[] pieces = new Piece[32];
	public Field[] board = new Field[120];
	
	public int[] kingIndices = new int[]{OFF_BOARD, OFF_BOARD}; 
	
	public byte colorToMove = COLOR_WHITE;
	public boolean[] castles = new boolean[4];
	public int possibleEpIndex = OFF_BOARD;

	//??
	public int halfMoveClock = 0;
	public int fullMoveClock = 0;
	
	public long hash1;
	public long hash2;
	
	public Position() {		
		emptyBoard();	
	}
	
	private void emptyBoard() {
		for(int i = 0; i < 120; i++) {
			board[i] = new Field();
			if(i%10 == 0 || i%10 == 9 || i < 20 || i > 99) {
				board[i].color = COLOR_OFF_BOARD;
			} else {
				board[i].color = COLOR_FREE;
			}
		}
	}
		
	public Position effectiveClone() {
		
		Position ret = new Position();
		
		for(int i = 0; i < 120; i++) {
			ret.board[i] = board[i].effectiveClone();
			if(i < 4) {				
				ret.castles[i] = castles[i];
			}
			if(i < 2) {
				ret.kingIndices[i] = kingIndices[i];
			}
			
			
		}
		
		ret.colorToMove = colorToMove;
		ret.fullMoveClock = fullMoveClock;
		ret.halfMoveClock = halfMoveClock;
		ret.hash1 = hash1;
		ret.hash2 = hash2;
		ret.possibleEpIndex = possibleEpIndex;

		return ret;
		
	}
	
	public Position setStartPosition() {
	
		kingIndices[COLOR_WHITE] = E1;
		kingIndices[COLOR_BLACK] = E8;
		
		for(int i = 0; i < 8; i++) {
			board[21 + i].color = COLOR_WHITE;
			board[31 + i].color = COLOR_WHITE;
			board[31 + i].pieceType = PIECE_PAWN;
			board[81 + i].color = COLOR_BLACK;
			board[81 + i].pieceType = PIECE_PAWN;
			board[91 + i].color = COLOR_BLACK;
			if(i < 4) {
				castles[i] = true;
			}
		}
		
		board[A1].pieceType = PIECE_ROOK;
		board[H1].pieceType = PIECE_ROOK;
		board[A8].pieceType = PIECE_ROOK;
		board[H8].pieceType = PIECE_ROOK;

		board[B1].pieceType = PIECE_KNIGHT;
		board[G1].pieceType = PIECE_KNIGHT;
		board[B8].pieceType = PIECE_KNIGHT;
		board[G8].pieceType = PIECE_KNIGHT;

		board[C1].pieceType = PIECE_BISHOP;
		board[F1].pieceType = PIECE_BISHOP;
		board[C8].pieceType = PIECE_BISHOP;
		board[F8].pieceType = PIECE_BISHOP;

		board[D1].pieceType = PIECE_QUEEN;
		board[D8].pieceType = PIECE_QUEEN;

		board[E1].pieceType = PIECE_KING;
		board[E8].pieceType = PIECE_KING;

		colorToMove = COLOR_WHITE;
		
		return this;
	}
	
	String patternString = 	"([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/" +
							"([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)/([rnbqkpRNBKQP1-8]+)" +
							" (w|b) ([KQkq]+|\\-) ([a-h][1-8]|\\-)([ \\d]?)([ \\d]?)";
	Pattern pattern = Pattern.compile(patternString);

	
	public void setFen(String fenString) {
	
		Matcher matcher = pattern.matcher(fenString);

		
		if(!matcher.find()) {
			throw new RuntimeException("not found");			
		}
		
		
		if(matcher.groupCount() < 11) {
			throw new RuntimeException();
		}

		
		emptyBoard();
		
		for(int i = 1; i < 9; i++) {
			setFen1_8Row(9-i, matcher.group(i));
		}
		
		setKingIndices();
		
		colorToMove = "w".equalsIgnoreCase(matcher.group(9)) ? COLOR_WHITE : COLOR_BLACK;

		setFenCastles(matcher.group(10));
		setPossibleEp(matcher.group(11));
		
		fullMoveClock = 0;
		halfMoveClock = 0;
		
		if(matcher.groupCount() > 11 && matcher.group(12).length() > 1) {
			fullMoveClock = Integer.parseInt(matcher.group(12).substring(1));	
		} 
				
		if(matcher.groupCount() > 12 && matcher.group(13).length() > 1) {
			halfMoveClock = Integer.parseInt(matcher.group(13).substring(1));
		}
		
	}

	private void setKingIndices() {
		kingIndices[0] = OFF_BOARD;		
		kingIndices[1] = OFF_BOARD;		
		for(int i = A1; i <= H8; i++) {
			if((board[i].color == COLOR_BLACK || board[i].color == COLOR_WHITE) && (board[i].pieceType == PIECE_KING)) {
				kingIndices[board[i].color] = i;
			}
		}
		if(kingIndices[0] == -1 || kingIndices[1] == -1) {
			throw new RuntimeException("At least one of the kings not found on the board when setting king indices");
		}
	}
	
	private void setPossibleEp(String epFen) {
		
		possibleEpIndex = OFF_BOARD;
		if(epFen.equals("-")) {
			return;
		}
		possibleEpIndex = parseField(epFen);
		
	}

	public static int parseRow(char c) {
		
		if(c < '1' || c > '8') {
			return Position.OFF_BOARD;
		}
		return c - '1' + 1;		
	}

	
	public static int parseColumn(char c) {

		if(c >= 'A' && c <= 'H') {
			return c - 'A' + 1;
		} else if(c >= 'a' && c <= 'h') {
			return c - 'a' + 1;			
		} else {
			return Position.OFF_BOARD;
		}
		
	}
	
	public static int parseField(String field) {
		
		int column1_8 = parseColumn(field.charAt(0));		
		int row1_8 = parseRow(field.charAt(1));
		if(row1_8 == Position.OFF_BOARD || column1_8 == Position.OFF_BOARD) {
			return Position.OFF_BOARD;
		}
		
		return row1_8 * 10 + 10 + column1_8;
	}
	
	private void setFenCastles(String fenCastles) {
		
		for(int i = 0; i < 4; i++) {
			castles[i] = false;
		}
		if(fenCastles.equals("-")) {
			return;
		}
		for(int i = 0; i < fenCastles.length(); i++) {
			switch(fenCastles.charAt(i)) {
				case 'K':
					castles[CASTLE_INDEX_WHITE_0_0] = true;
					break;
				case 'Q':
					castles[CASTLE_INDEX_WHITE_0_0_0] = true;
					break;
				case 'k':
					castles[CASTLE_INDEX_BLACK_0_0] = true;
					break;
				case 'q':
					castles[CASTLE_INDEX_BLACK_0_0_0] = true;
					break;				
			}
		}
	}
	
	private void setFen1_8Row(int row, String fenRow) {
		
		int column = 1;
		for(int i = 0; i < fenRow.length(); i++) {
			int boardIndex = row*10 + 10 + column;
			switch(fenRow.charAt(i)) {
				case 'r':
					board[boardIndex].pieceType = PIECE_ROOK;
					board[boardIndex].color = COLOR_BLACK;
					break;
				case 'n':
					board[boardIndex].pieceType = PIECE_KNIGHT;
					board[boardIndex].color = COLOR_BLACK;
					break;
				case 'b':
					board[boardIndex].pieceType = PIECE_BISHOP;
					board[boardIndex].color = COLOR_BLACK;
					break;
				case 'q':
					board[boardIndex].pieceType = PIECE_QUEEN;
					board[boardIndex].color = COLOR_BLACK;
					break;
				case 'k':
					board[boardIndex].pieceType = PIECE_KING;
					board[boardIndex].color = COLOR_BLACK;
					break;
				case 'p':
					board[boardIndex].pieceType = PIECE_PAWN;
					board[boardIndex].color = COLOR_BLACK;
					break;
	
				case 'R':
					board[boardIndex].pieceType = PIECE_ROOK;
					board[boardIndex].color = COLOR_WHITE;
					break;
				case 'N':
					board[boardIndex].pieceType = PIECE_KNIGHT;
					board[boardIndex].color = COLOR_WHITE;
					break;
				case 'B':
					board[boardIndex].pieceType = PIECE_BISHOP;
					board[boardIndex].color = COLOR_WHITE;
					break;
				case 'Q':
					board[boardIndex].pieceType = PIECE_QUEEN;
					board[boardIndex].color = COLOR_WHITE;
					break;
				case 'K':
					board[boardIndex].pieceType = PIECE_KING;
					board[boardIndex].color = COLOR_WHITE;
					break;
				case 'P':
					board[boardIndex].pieceType = PIECE_PAWN;
					board[boardIndex].color = COLOR_WHITE;
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
	
}
