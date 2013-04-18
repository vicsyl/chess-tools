package com.virutor.chess.ui.config;

public class HumanPlayer extends Player {

	private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
}
