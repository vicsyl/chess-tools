package org.virutor.chess.uci;

public enum ServerToEngineUciCommand {
	
	COMMAND_QUIT(UciConstants.QUIT),
	COMMAND_IS_READY(UciConstants.IS_READY),
	COMMAND_POSITION(UciConstants.POSITION),
	COMMAND_START_POSITION(UciConstants.STARTPOS),
	COMMAND_MOVES(UciConstants.MOVES),
	COMMAND_GO(UciConstants.GO),
	COMMAND_STOP(UciConstants.STOP),
	COMMAND_UCI(UciConstants.UCI);
	
	private final String command;

	private ServerToEngineUciCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}	
	
	public String toString() {
		return command;
	}
	
}
