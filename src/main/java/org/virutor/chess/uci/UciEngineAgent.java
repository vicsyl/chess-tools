package org.virutor.chess.uci;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.virutor.chess.model.GameNode;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.Position;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.uci.commands.PlayCommand;
import org.virutor.chess.ui.model.InvalidMoveException;
import org.virutor.chess.ui.model.UiGame;
import org.virutor.chess.ui.model.UiGameListener;

/**
 * Introduce interface and factory (take android into account)
 * - can still leave the "desktop" implementation in the core module
 * @author vaclav
 *
 */
public class UciEngineAgent implements InfoListener, UiGameListener {

	private static final Logger LOG = LogManager.getLogger(UciEngineAgent.class);
	
	public static enum State {
		BEFORE_STARTING,
		STARTED,
		//THINKING,
		QUIT;
	}
	
	//TODO check states in some state dependent methods 
	private volatile State state = State.BEFORE_STARTING; 
	
	private byte color;
	
	private volatile boolean isThinking = false; 

	private UciProtocol uciProtocol;
	private CountDownLatch startCountDownLatch;
	private EngineInfo engineInfo = new EngineInfo();
	private String name;
	
	public UciEngineAgent(byte color, String path, String name) {
		this.name = name;
		this.color = color;
		//TODO in the long, this is a hack.... 
		uciProtocol = UciProtocolFactory.uciProtocolFactoryImpl.newUciProtocol(path, this, engineInfo);
		uciProtocol.setInfoListener(this);
	}
	
	public String getName() {
		return name;
	}

	public EngineInfo getEngineInfo() {
		return engineInfo;
	}
	
	public State getState() {
		return state;
	}

	boolean isThinking() {
		return isThinking;
	}

	public void setColor(byte color) {
		//TODO check state
		this.color = color;
	}
	
	public byte getColor() {
		return color;
	}

	/**
	 * UciProtocol (aka the engine) has played
	 */
	public void play(LongAlgebraicMove laMove) throws InvalidMoveException {
	
		isThinking = false;
		
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
		LOG.debug("onInfo: " + info.getOriginalString());
		for(InfoListener infoListener :	UiGame.instance.getInfoListeners()) {
			LOG.debug("onInfo sent to : " + infoListener.getClass());
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
		
		state = State.STARTED;
	}
	
	/**
	 * Constructs play command from the UiGame singleton 
	 * @return
	 */
	private PlayCommand getPlayCommand() {
		
		List<Move> moves = new ArrayList<Move>();
		GameNode gameNode = UiGame.instance.getGame().getHeadGameNode();
		while(gameNode.getNextMove() != null && gameNode != UiGame.instance.getGame().getCurrentGameNode()) {
			moves.add(gameNode.getNextMove());
			gameNode = gameNode.getNext();
		}
		Position position = UiGame.instance.getGame().getHeadGameNode().getPosition();
		
		PlayCommand playCommand = new PlayCommand(position, moves);
		playCommand.setTimeFromEnvironment();

		return playCommand;
		
	}
	
	
	/**
	 * quit the engine
	 * TODO add some method that will wait some tome for the process to quit!!
	 */
	public void quit() {
		isThinking = false;
		uciProtocol.quit();
		state = State.QUIT;
	}


	/**
	 * stop computing
	 */
	public void stop() {
		isThinking = false;
		uciProtocol.stop();
	}
	
	/**
	 * Generic change to game in UI
	 */
	@Override
	public void onGenericChange(UiGameListener.GameChangeType changeType) {
		if(UiGame.instance.getGame().getCurrentGameNode().getNext() == null && UiGame.instance.shouldAgentsPlayOnGenericChange) {
			onDoMove(null);
		}
	}

	/**
	 * Move done in UI
	 */
	@Override
	public void onDoMove(Move move) {
		
		if(UiGame.instance.shouldAgentPlay(this)) {  
			uciProtocol.sendCommand(getPlayCommand());
			isThinking = true;			
		}
	}

	/**
	 * Move undone in UI
	 */
	@Override
	public void onUndoMove(Move move) {
		//do nothing
	}
	
}
