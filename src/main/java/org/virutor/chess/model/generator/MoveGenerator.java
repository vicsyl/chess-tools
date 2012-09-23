package org.virutor.chess.model.generator;

import static org.virutor.chess.model.Move.CASTLE_EP_FLAG_BLACK_0_0;
import static org.virutor.chess.model.Move.CASTLE_EP_FLAG_BLACK_0_0_0;
import static org.virutor.chess.model.Move.CASTLE_EP_FLAG_EP;
import static org.virutor.chess.model.Move.CASTLE_EP_FLAG_WHITE_0_0;
import static org.virutor.chess.model.Move.CASTLE_EP_FLAG_WHITE_0_0_0;
import static org.virutor.chess.model.Piece.PIECE_BISHOP;
import static org.virutor.chess.model.Piece.PIECE_KING;
import static org.virutor.chess.model.Piece.PIECE_KNIGHT;
import static org.virutor.chess.model.Piece.PIECE_PAWN;
import static org.virutor.chess.model.Piece.PIECE_QUEEN;
import static org.virutor.chess.model.Piece.PIECE_ROOK;
import static org.virutor.chess.model.Position.A1;
import static org.virutor.chess.model.Position.A8;
import static org.virutor.chess.model.Position.B1;
import static org.virutor.chess.model.Position.B8;
import static org.virutor.chess.model.Position.C1;
import static org.virutor.chess.model.Position.C8;
import static org.virutor.chess.model.Position.CASTLE_INDEX_BLACK_0_0;
import static org.virutor.chess.model.Position.CASTLE_INDEX_BLACK_0_0_0;
import static org.virutor.chess.model.Position.CASTLE_INDEX_WHITE_0_0;
import static org.virutor.chess.model.Position.CASTLE_INDEX_WHITE_0_0_0;
import static org.virutor.chess.model.Position.COLOR_FREE;
import static org.virutor.chess.model.Position.COLOR_OFF_BOARD;
import static org.virutor.chess.model.Position.COLOR_WHITE;
import static org.virutor.chess.model.Position.D1;
import static org.virutor.chess.model.Position.D8;
import static org.virutor.chess.model.Position.E1;
import static org.virutor.chess.model.Position.E8;
import static org.virutor.chess.model.Position.F1;
import static org.virutor.chess.model.Position.F8;
import static org.virutor.chess.model.Position.G1;
import static org.virutor.chess.model.Position.G8;
import static org.virutor.chess.model.Position.H1;
import static org.virutor.chess.model.Position.H8;
import static org.virutor.chess.model.Position.OFF_BOARD;

import java.util.ArrayList;
import java.util.List;

import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;

public class MoveGenerator {

	private static final int[] BISHOP_OFFSETS = new int[] {-11,11,-9,9};
	private static final int[] ROOK_OFFSETS = new int[] {10,-10,-1,1};
	private static final int[] QUEEN_OFFSETS = new int[] {-11,-10,-9,-1,9,10,11,1};
	private static final int[] KNIGHT_OFFSETS = new int[] {-21,-12,8,19,21,12,-8,-19};

	private static final int[] PAWN_PUSH_OFFSETS = new int[] {10,-10};
	private static final int[][] PAWN_CAPTURE_OFFSETS = new int[][] {{11,9},{-11,-9}};
	private static final int[][] PAWN_CAPTURE_EP = new int[][] {{1,-1},{-1,1}};
	private static final int[] PAWN_BASIC_ROW_10x12 = new int[] {3,8};
	private static final int[] PAWN_PROMOTE_ROW_10x12 = new int[] {9,2};

	private static final byte[] PROMOTED_PIECES = new byte[]{PIECE_QUEEN, PIECE_KNIGHT, PIECE_ROOK, PIECE_BISHOP};

	
	private static class CastleData {
		byte castleMoveFlag;
		boolean[] castlesAndFilter;
		int kingFrom;
		int kingTo;
		int rookFrom;
		int rookTo;
		private CastleData(byte castleMoveFlag, boolean[] castlesAndFilter,
				int kingFrom, int kingTo, int rookFrom, int rookTo) {
			this.castleMoveFlag = castleMoveFlag;
			this.castlesAndFilter = castlesAndFilter;
			this.kingFrom = kingFrom;
			this.kingTo = kingTo;
			this.rookFrom = rookFrom;
			this.rookTo = rookTo;
		}
		
	}
	
	private static final CastleData CASTLE_DATA[] = new CastleData[4];
	
	static {
		
		boolean[] castlesWhiteAnd = new boolean[4];
		castlesWhiteAnd[CASTLE_INDEX_WHITE_0_0] = false;
		castlesWhiteAnd[CASTLE_INDEX_WHITE_0_0_0] = false;
		castlesWhiteAnd[CASTLE_INDEX_BLACK_0_0] = true;
		castlesWhiteAnd[CASTLE_INDEX_BLACK_0_0_0] = true;
		boolean[] castlesBlackAnd = new boolean[4];
		castlesBlackAnd[CASTLE_INDEX_WHITE_0_0] = true;
		castlesBlackAnd[CASTLE_INDEX_WHITE_0_0_0] = true;
		castlesBlackAnd[CASTLE_INDEX_BLACK_0_0] = false;
		castlesBlackAnd[CASTLE_INDEX_BLACK_0_0_0] = false;
		
		CASTLE_DATA[CASTLE_INDEX_WHITE_0_0] = new CastleData(CASTLE_EP_FLAG_WHITE_0_0, castlesWhiteAnd, E1, G1, H1, F1);
		CASTLE_DATA[CASTLE_INDEX_WHITE_0_0_0] = new CastleData(CASTLE_EP_FLAG_WHITE_0_0_0, castlesWhiteAnd, E1, C1, A1, D1);
		CASTLE_DATA[CASTLE_INDEX_BLACK_0_0] = new CastleData(CASTLE_EP_FLAG_BLACK_0_0, castlesBlackAnd, E8, G8, H8, F8);
		CASTLE_DATA[CASTLE_INDEX_BLACK_0_0_0] = new CastleData(CASTLE_EP_FLAG_BLACK_0_0_0, castlesBlackAnd, E8, C8, A8, D8);
		
	}
	
	
	
	public static enum Continuation {
		POSSIBLE_MOVES,
		CHECK_MATE,
		STALEMATE;
	}
	
	public static class GeneratedMoves {

		public List<Move> moves = new ArrayList<Move>();
		public List<Position> position = new ArrayList<Position>();
		public Continuation continuation = Continuation.POSSIBLE_MOVES;
		
	}
	
	public static Position doMove(Position position, Move move) {		
		return doMove(position, move, generateMoves(position));		
	}

	public static Position doMove(Position position, Move move, GeneratedMoves generatedMoves) {
		if(generatedMoves.continuation != Continuation.POSSIBLE_MOVES) {
			throw new RuntimeException("No possible moves");
		}
		for(int i = 0 ; i < generatedMoves.moves.size(); i++) {
			if(generatedMoves.moves.get(i).equals(move)) {
				return generatedMoves.position.get(i);
			}
		}
		throw new RuntimeException("Cannot move");	
		
	}

	public static GeneratedMoves generateMoves(Position position) {
		
		GeneratedMoves ret = new GeneratedMoves();
		
		for(int i = 21; i < 99; i++) {
			if(i % 10 == 0 || i % 10 == 9) {
				continue;
			}
			if(position.board[i].color != position.colorToMove) {
				continue;				
			}
			switch(position.board[i].pieceType) {
				case PIECE_BISHOP: 
					generateLongDistanceMoves(position, i, BISHOP_OFFSETS, ret);
					break;
				case PIECE_ROOK: 
					generateLongDistanceMoves(position, i, ROOK_OFFSETS, ret);
					break;
				case PIECE_QUEEN: 
					generateLongDistanceMoves(position, i, QUEEN_OFFSETS, ret);
					break;
				case PIECE_KNIGHT: 
					for(int offset : KNIGHT_OFFSETS) {
						byte color = position.board[i + offset].color; 
						if(color == COLOR_FREE || color == 1-position.colorToMove) {
							generateMove(position, i, i+offset, ret);
						}
					}
					break;
				case PIECE_KING:
					for(int offset : QUEEN_OFFSETS) {
						byte color = position.board[i + offset].color; 
						if(color == COLOR_FREE || color == 1-position.colorToMove) {
							generateMove(position, i, i+offset, ret);
						}
					}
					if(!isInCheck(position)) {
						if(position.colorToMove == COLOR_WHITE) {
							if(position.castles[CASTLE_INDEX_WHITE_0_0]
									   && !isThreatened(position, F1, (byte)(1-position.colorToMove))
									   && !isThreatened(position, G1, (byte)(1-position.colorToMove))
									   && position.board[F1].color == COLOR_FREE
									   && position.board[G1].color == COLOR_FREE) {
								
										generateCastle(position, ret, CASTLE_INDEX_WHITE_0_0);
								
							}
							if(position.castles[CASTLE_INDEX_WHITE_0_0_0]
							           && !isThreatened(position, D1, (byte)(1-position.colorToMove))
									   && !isThreatened(position, C1, (byte)(1-position.colorToMove))
									   && position.board[D1].color == COLOR_FREE
									   && position.board[C1].color == COLOR_FREE 
									   && position.board[B1].color == COLOR_FREE) {
											
										generateCastle(position, ret, CASTLE_INDEX_WHITE_0_0_0);
										
							}
						} else {
							if(position.castles[CASTLE_INDEX_BLACK_0_0]
									   && !isThreatened(position, F8, (byte)(1-position.colorToMove))
									   && !isThreatened(position, G8, (byte)(1-position.colorToMove))
									   && position.board[F8].color == COLOR_FREE
									   && position.board[G8].color == COLOR_FREE) {
								
										generateCastle(position, ret, CASTLE_INDEX_BLACK_0_0);
								
							}
							if(position.castles[CASTLE_INDEX_BLACK_0_0_0]
							           && !isThreatened(position, D8, (byte)(1-position.colorToMove))
									   && !isThreatened(position, C8, (byte)(1-position.colorToMove))
									   && position.board[D8].color == COLOR_FREE
									   && position.board[C8].color == COLOR_FREE 
									   && position.board[B8].color == COLOR_FREE) {
											
										generateCastle(position, ret, CASTLE_INDEX_BLACK_0_0_0);
										
							}
						}
					}	
					
					break;
				case PIECE_PAWN:

					if(position.board[i + PAWN_PUSH_OFFSETS[position.colorToMove]].color == COLOR_FREE) {

						if(((i + PAWN_PUSH_OFFSETS[position.colorToMove]) / 10) == PAWN_PROMOTE_ROW_10x12[position.colorToMove]) {
							for(byte promotedPiece : PROMOTED_PIECES) {
								if(generateMove(position, i, i + PAWN_PUSH_OFFSETS[position.colorToMove], ret)) {							
									Position lastGeneratedPosition = ret.position.get(ret.position.size() - 1);
									lastGeneratedPosition.board[i + PAWN_PUSH_OFFSETS[position.colorToMove]].pieceType = promotedPiece;
									Move lastGeneratedMove = ret.moves.get(ret.moves.size() - 1);
									lastGeneratedMove.piece_promoted = promotedPiece;
								}
							}
						} else {
							generateMove(position, i, i + PAWN_PUSH_OFFSETS[position.colorToMove], ret);
						}
						
						if(i / 10 == PAWN_BASIC_ROW_10x12[position.colorToMove] && position.board[i + PAWN_PUSH_OFFSETS[position.colorToMove] * 2].color == COLOR_FREE) {
							if(generateMove(position, i, i + PAWN_PUSH_OFFSETS[position.colorToMove] * 2, ret)) {
								ret.position.get(ret.position.size()-1).possibleEpIndex = i + PAWN_PUSH_OFFSETS[position.colorToMove];
							}
						}
					}
					
					for(int captupureIndex = 0; captupureIndex < 2; captupureIndex++) {
						
						int to = i + PAWN_CAPTURE_OFFSETS[position.colorToMove][captupureIndex];
						if(position.board[to].color == 1-position.colorToMove) {
							if(to / 10 == PAWN_PROMOTE_ROW_10x12[position.colorToMove]) {
								for(byte promotedPiece : PROMOTED_PIECES) {		
									if(generateMove(position, i, to, ret)) {
										Position lastGeneratedPosition = ret.position.get(ret.position.size() - 1);
										lastGeneratedPosition.board[to].pieceType = promotedPiece;
										Move lastGeneratedMove = ret.moves.get(ret.moves.size() - 1);
										lastGeneratedMove.piece_promoted = promotedPiece;
									}
								}
							} else {
								generateMove(position, i, to, ret);
							}
						} else if(position.possibleEpIndex == to)  {							
							GeneratedMoveAndPosition moveAndPosition = generateMoveAndPositionWithoutCheck(position, i, to);
							moveAndPosition.position.board[i + PAWN_CAPTURE_EP[position.colorToMove][captupureIndex]].color = COLOR_FREE;
							moveAndPosition.move.piece_captured = PIECE_PAWN;
							moveAndPosition.move.castle_ep_flag = CASTLE_EP_FLAG_EP;
							if(!isInOppositeCheck(moveAndPosition.position)) {
								ret.moves.add(moveAndPosition.move);
								ret.position.add(moveAndPosition.position);
							}
						}
					}

					break;
			}
						
		} 
		
		if(ret.moves.size() == 0) {
			ret.continuation = isInCheck(position) ? Continuation.CHECK_MATE : Continuation.STALEMATE; 
		} else {
			ret.continuation = Continuation.POSSIBLE_MOVES;
		}
		
		return ret;
		
	}

	private static void generateCastle(Position position, GeneratedMoves ret, int castleIndex) {

		Position newPosition = position.effectiveClone();
		newPosition.possibleEpIndex = OFF_BOARD;
		
		Move newMove = new Move();
				
		newPosition.board[CASTLE_DATA[castleIndex].kingFrom].color = COLOR_FREE;
		newPosition.board[CASTLE_DATA[castleIndex].rookFrom].color = COLOR_FREE;
		
		newPosition.board[CASTLE_DATA[castleIndex].kingTo].color = newPosition.colorToMove;
		newPosition.board[CASTLE_DATA[castleIndex].kingTo].pieceType = PIECE_KING;
		newPosition.board[CASTLE_DATA[castleIndex].rookTo].color = newPosition.colorToMove;
		newPosition.board[CASTLE_DATA[castleIndex].rookTo].pieceType = PIECE_ROOK;
		
		newPosition.kingIndices[newPosition.colorToMove] = CASTLE_DATA[castleIndex].kingTo;
		
		for(int i = 0; i < 4; i++) {
			newPosition.castles[i] &= CASTLE_DATA[castleIndex].castlesAndFilter[i];			
		}
		
		newMove.castle_ep_flag = CASTLE_DATA[castleIndex].castleMoveFlag;

		//newPosition.fullMoveClock++; //??
		newPosition.halfMoveClock++;
		newPosition.colorToMove = (byte)(1 - newPosition.colorToMove);
		
		ret.moves.add(newMove);
		ret.position.add(newPosition);
		
	}

	private static void generateLongDistanceMoves(Position position, int index, int[] offsets, GeneratedMoves ret) {
		
		for(int offset : offsets) {
			for(int i = 1; i < 8; i++) {
				byte color = position.board[index + offset*i].color; 
				if(color == COLOR_OFF_BOARD || color == position.colorToMove) {
					break;
				}
				generateMove(position, index, index + offset*i, ret);
				if(color != COLOR_FREE) {
					break;
				}
			}
		}
		
	}
	
	private static class GeneratedMoveAndPosition {
		public Move move;
		public Position position;
	}

	private static GeneratedMoveAndPosition generateMoveAndPositionWithoutCheck(Position position, int from, int to) {

		GeneratedMoveAndPosition generatedMoveAndPosition = new GeneratedMoveAndPosition();
		generatedMoveAndPosition.position = position.effectiveClone();
		generatedMoveAndPosition.move = new Move();
		
		Position newPosition = generatedMoveAndPosition.position;
		newPosition.possibleEpIndex = OFF_BOARD;
		Move newMove = generatedMoveAndPosition.move;
		
		newMove.from = from;
		newMove.to = to;
		newMove.piece_moved = position.board[from].pieceType;
		
		if(position.board[to].color != COLOR_FREE) {
			newMove.piece_captured = position.board[to].pieceType;
		}
		
		newPosition.board[to].color = newPosition.board[from].color;
		newPosition.board[to].pieceType = newPosition.board[from].pieceType;
		newPosition.board[from].color = COLOR_FREE;
		
		newPosition.halfMoveClock++;
		//newPosition.fullMoveClock++; //??
		//newPosition.hash1 = ??

		if(newMove.piece_moved == PIECE_KING) {
			newPosition.kingIndices[newPosition.colorToMove] = to;
			if(newPosition.colorToMove == COLOR_WHITE) {
				newPosition.castles[CASTLE_INDEX_WHITE_0_0] = false;
				newPosition.castles[CASTLE_INDEX_WHITE_0_0_0] = false;
			} else {
				newPosition.castles[CASTLE_INDEX_BLACK_0_0] = false;
				newPosition.castles[CASTLE_INDEX_BLACK_0_0_0] = false;				
			}
		} else if(newMove.piece_moved == PIECE_ROOK) {
			switch(from) {
				case A1:
					newPosition.castles[CASTLE_INDEX_WHITE_0_0_0] = false;
					break;
				case H1:
					newPosition.castles[CASTLE_INDEX_WHITE_0_0] = false;
					break;
				case A8:
					newPosition.castles[CASTLE_INDEX_BLACK_0_0_0] = false;
					break;
				case H8:
					newPosition.castles[CASTLE_INDEX_BLACK_0_0] = false;
					break;
			}
		}

		switch(to) {
			case A1:
				newPosition.castles[CASTLE_INDEX_WHITE_0_0_0] = false;
				break;
			case H1:
				newPosition.castles[CASTLE_INDEX_WHITE_0_0] = false;
				break;
			case A8:
				newPosition.castles[CASTLE_INDEX_BLACK_0_0_0] = false;
				break;
			case H8:
				newPosition.castles[CASTLE_INDEX_BLACK_0_0] = false;
				break;
		}

		
		newPosition.possibleEpIndex = OFF_BOARD;
		newPosition.colorToMove = (byte)(1 - newPosition.colorToMove);

		return generatedMoveAndPosition;

	}
	
	private static boolean generateMove(Position position, int from, int to, GeneratedMoves ret) {

		GeneratedMoveAndPosition moveAndPosition = generateMoveAndPositionWithoutCheck(position, from, to);
		
		if(isInOppositeCheck(moveAndPosition.position)) {
			return false;
		} else {
			ret.moves.add(moveAndPosition.move);
			ret.position.add(moveAndPosition.position);
			return true;
		}
		
	}

	
	public static boolean isThreatened(Position position, int index, byte fromColor) {
		for(int offset : ROOK_OFFSETS) {
			for(int i = 1; i < 8; i++) {
				byte color = position.board[index + i*offset].color;  
				if(color == 1-fromColor || color == COLOR_OFF_BOARD) {
					break;
				}
				if(color == fromColor) {
					byte pieceType = position.board[index+ i*offset].pieceType; 
					if(pieceType == PIECE_ROOK || pieceType == PIECE_QUEEN) {
						return true;
					} else {
						break;
					}
				}
			}
		}
		for(int offset : BISHOP_OFFSETS) {
			for(int i = 1; i < 8; i++) {
				byte color = position.board[index + i*offset].color;  
				if(color == 1-fromColor || color == COLOR_OFF_BOARD) {
					break;
				}
				if(color == fromColor) {
					byte pieceType = position.board[index+ i*offset].pieceType; 
					if(pieceType == PIECE_BISHOP || pieceType == PIECE_QUEEN) {
						return true;
					} else {
						break;
					}
				}
			}
		}
		for(int offset : KNIGHT_OFFSETS) {
			byte color = position.board[index + offset].color;  
			if(color == fromColor && position.board[index + offset].pieceType == PIECE_KNIGHT) {
				return true;
			}
		}
		
		for(int offset : PAWN_CAPTURE_OFFSETS[1-fromColor]) {
			byte color = position.board[index + offset].color;
			if(color == fromColor && position.board[index + offset].pieceType == PIECE_PAWN) {
				return true;
			}
		}
		
		return false;

	}
	
	public static boolean isInCheck(Position position) {
		
		int index = position.kingIndices[position.colorToMove];
		return isThreatened(position, index, (byte)(1-position.colorToMove));
	}
	
	private static boolean isInOppositeCheck(Position position) {
		
		int index = position.kingIndices[1-position.colorToMove];
		return isThreatened(position, index, position.colorToMove);
	}
	
}
