package org.virutor.chess.uci;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.virutor.chess.model.GameNode;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.uci.commands.PlayCommand;
import org.virutor.chess.ui.model.UiGame;
import org.virutor.chess.ui.model.UiGameListener;

/**
 * Introduce interface and factory (take android into account)
 * - can still leave the "desktop" implementation in the core module
 * @author vaclav
 *
 */
public class UciEngineAgent implements GameServerTemp, InfoListener, UiGameListener {

	private byte color;

	private UciProtocol uciProtocol;
	private CountDownLatch startCountDownLatch;
	private EngineInfo engineInfo = new EngineInfo();
	
	
	public UciEngineAgent(byte color, String path) {
		this.color = color;
		uciProtocol = new UciProtocol(path, this, engineInfo);
		uciProtocol.setInfoListener(this);
	}
	
	
	public EngineInfo getEngineInfo() {
		return engineInfo;
	}

	void setColor(byte color) {
		//TODO check state
		this.color = color;
	}

	/**
	 * UciProtocol (aka the engine) has played
	 */
	public void play(LongAlgebraicMove laMove) throws InvalidMoveException {
	
		if(UiGame.instance.getGame().getCurrentPosition().colorToMove != color) {
			//TODO send something via Uci
			throw new InvalidMoveException("Wrong player : " + color);
		}

		Move move = LongAlgebraicMove.findMove(laMove, UiGame.instance.getGame().getCurrentGameNode().getGeneratedMoves());
		if(move == null) {
			throw new InvalidMoveException("Invalid move: " + laMove);
		}
		UiGame.instance.doMove(move);			
		
	}
	
	/**
	 * Uci engine is ready to play
	 */
	public void notifyReady() {
		startCountDownLatch.countDown();
	}
	
	/**
	 * Info sent from uci engine. Delegate to other (possibly UI listener)
	 */
	public void onInfo(ComputationInfo info) {
		//TODO move to UiGame
		for(InfoListener infoListener :	UiGame.instance.getInfoListeners()) {
			infoListener.onInfo(info);
		}
	}
	
	/**
	 * Blocking method for starting the engine -> wait for notify ready
	 * @throws UciProtocolException
	 */
	public void start() throws UciProtocolException {
		
		startCountDownLatch = new CountDownLatch(1);
		
		uciProtocol.start();

		try {
			startCountDownLatch.await();
		} catch (InterruptedException e) {
			// TODO what to do ???
		}
	}
	
	/**
	 * Constructs play command from the UiGame singleton 
	 * @return
	 */
	private PlayCommand getPlayCommand() {
		
		List<Move> moves = new ArrayList<Move>();
		GameNode gameNode = UiGame.instance.getGame().getHeadGameNode();
		while(gameNode.getNextMove() != null) {
			moves.add(gameNode.getNextMove());
			gameNode = gameNode.getNext();
		}
		
		PlayCommand playCommand = new PlayCommand(Position.getStartPosition(), moves);
		playCommand.setTimeFromEnvironment();

		return playCommand;
		
	}
	
	
	/**
	 * quit the engine
	 */
	public void quit() {
		uciProtocol.quit();
	}

	/**
	 * Generic change to game in UI
	 */
	@Override
	public void onGenericChange(UiGameListener.GameChangeType changeType) {
		//TODO how to implement this?
	}

	/**
	 * Move done in UI
	 */
	@Override
	public void onDoMove(Move move) {
		if(UiGame.instance.getGame().getCurrentPosition().colorToMove == color) {
			uciProtocol.sendCommand(getPlayCommand());	
		}
	}

	/**
	 * Move undone in UI
	 */
	@Override
	public void onUndoMove(Move move) {
		//TODO how to implement this?
	}
	
}
