package org.virutor.chess.uci;

import java.util.Arrays;
import java.util.List;

public class UciConstants {

	// Engine to GUI:
	public static final String ID = "id";
	public static final String UCI_OK = "uciok";
	public static final String READY_OK = "readyok";
	public static final String BEST_MOVE = "bestmove";
	public static final String COPY_PROTECTION = "copyprotection";
	public static final String REGISTRATION = "registration";
	public static final String INFO = "info";
	public static final String OPTION = "option";

	// GUI to engine
	public static final String UCI = "uci";
	public static final String QUIT = "quit";
	public static final String IS_READY = "isready";
	public static final String POSITION = "position";
	public static final String STARTPOS = "startpos";
	public static final String POSITION_ = "position ";
	public static final String STARTPOS_ = "startpos ";
	public static final String MOVES = "moves";
	public static final String MOVES_ = "moves ";
	public static final String GO = "go";
	public static final String STOP = "stop";
	public static final String WTIME = "wtime";
	public static final String BTIME = "btime";
	public static final String WINC = "winc";
	public static final String BINC = "binc";
	public static final String MOVESTOGO = "movestogo";
	public static final String INFINITE = "infinite";

	// read commands == implemented read commands
	// TO BE REMOVED
	public static final List<String> READ_COMMANDS = 
			Arrays.asList(new String[] {ID, UCI_OK, READY_OK, OPTION, BEST_MOVE, INFO });

}
