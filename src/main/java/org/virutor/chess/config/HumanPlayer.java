package org.virutor.chess.config;

public class HumanPlayer extends Player {

	private String name;
	
	public HumanPlayer() {}
	
	public HumanPlayer(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
}
