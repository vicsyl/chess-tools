package org.virutor.chess.server;

import org.virutor.chess.model.Move;

public abstract class HumanPlayer implements Player {

	@Override
	public void moveDone(Move move) {}

	@Override
	public String getName() { return null; }

	@Override
	public void play() { }

	@Override
	public void start() { }

	@Override
	public boolean isStarted() { return true; }

	@Override
	public void quit() { }

	@Override
	public void stop() { }

}
