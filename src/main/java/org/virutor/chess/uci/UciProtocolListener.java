package org.virutor.chess.uci;

public interface UciProtocolListener {

	void onReadCommand(String command);
	void onWriteCommand(String command);
}
