package org.virutor.chess.model;

import static org.virutor.chess.model.Piece.PIECE_BISHOP;
import static org.virutor.chess.model.Piece.PIECE_KING;
import static org.virutor.chess.model.Piece.PIECE_KNIGHT;
import static org.virutor.chess.model.Piece.PIECE_PAWN;
import static org.virutor.chess.model.Piece.PIECE_QUEEN;
import static org.virutor.chess.model.Piece.PIECE_ROOK;

import org.virutor.chess.model.generator.MoveGenerator;
import org.virutor.chess.model.generator.ZobristHashing;
import org.virutor.chess.standard.FenUtils;


/**
 * Uses classical 10x12 board representation,
 * see https://chessprogramming.wikispaces.com/10x12+Board
 */
public class Position {
	
	public static enum Continuation {
		POSSIBLE_MOVES,
		CHECK_MATE,
		STALEMATE,
		_50_MOVES_DRAW,
		_3_FOLD_REPETITION;

		public static Game.Result getForcedResult(byte colorToMove, Continuation continuation) {
			switch (continuation) {
				case POSSIBLE_MOVES:
					return Game.Result.UNRESOLVED;
				case _3_FOLD_REPETITION:
				case _50_MOVES_DRAW:
				case STALEMATE:
					return Game.Result.DRAW;
				case CHECK_MATE:
					return colorToMove == COLOR_WHITE ? Game.Result.BLACK_WINS : Game.Result.WHITE_WINS;
				default:
					throw new RuntimeException("Unknown continuation:" + continuation);
			}

		}
	}

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

	public Field getField(int row, int file) {
		if(row < 1 || row > 8) {
			throw new IllegalArgumentException("row out of range (" + row + ")");
		}
		if(file < 1 || file > 8) {
			throw new IllegalArgumentException("file out of range (" + row + ")");
		}
		return board[row*10 + file + 10];
		
	}
	
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
	
	public static byte oppositeColor(byte color) {
		return (byte)(1 - color);
	}
	
	public static final byte COLOR_WHITE = 0; 
	public static final byte COLOR_BLACK = 1; 
	public static final byte COLOR_FREE = 2; 
	public static final byte COLOR_OFF_BOARD = 3; 

	public static final int CASTLE_INDEX_WHITE_0_0 = 0;
	public static final int CASTLE_INDEX_WHITE_0_0_0 = 1;
	public static final int CASTLE_INDEX_BLACK_0_0 = 2;
	public static final int CASTLE_INDEX_BLACK_0_0_0 = 3;


	//TODO what about generated moves, too!!!!	
	private Continuation continuationCache;

	@Deprecated //flawed: this won't capture 3 fold generation moves, use Node.gerenerated moves!!!
	public Continuation getContinuation() {
		if(continuationCache == null) {
			continuationCache = MoveGenerator.generateMoves(this).continuation;
		}
		return continuationCache;			
		
	}
	
	public Field[] board = new Field[120];
	
	public int[] kingIndices = new int[]{OFF_BOARD, OFF_BOARD}; 
	
	public byte colorToMove = COLOR_WHITE;
	public boolean[] castles = new boolean[4];
	public int possibleEpIndex = OFF_BOARD;

	public int halfMoveClock = 0;
	public int fullMoveClock = 1;
	
	public long hash;	
	
	public Position() {		
		emptyBoard();	
	}
	
	public static Position getStartPosition() {
		Position position = new Position();
		position.setStartPosition();
		return position;
	} 
	
	
	/**
	 * only accessed from FenUtils; why cannot I make it protected??!!
	 */
	public void emptyBoard() {
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
		ret.hash = hash;
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
		
		fullMoveClock = 1;
		
		ZobristHashing.setPositionHash(this);
				
		return this;
	}
	
	@Override
	public String toString() {
		return FenUtils.positionToFen(this);		
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
	
	//TODO check arguments
	public static int parseField(String field) {
		
		int column1_8 = parseColumn(field.charAt(0));		
		int row1_8 = parseRow(field.charAt(1));
		if(row1_8 == Position.OFF_BOARD || column1_8 == Position.OFF_BOARD) {
			return Position.OFF_BOARD;
		}
		
		return row1_8 * 10 + 10 + column1_8;
	}

	@Override
	public int hashCode() {
		if(hash == 0) {
			ZobristHashing.setPositionHash(this);
		}
		return (int) (hash ^ (hash >>> 32)); 
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;

		if(hash == 0) {
			ZobristHashing.setPositionHash(this);
		}
		if(other.hash == 0) {
			ZobristHashing.setPositionHash(other);
		}
		return hash == other.hash;
	}
	
}
