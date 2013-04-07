package org.virutor.chess.standard;

public interface PropertyHandler {
	void parse(String key, String value, PgnGame pgnGame);
	void format(String key, PgnGame pgnGame, StringBuilder sb);	
	void chechBeforeParse(PgnGame pgnGame);
}