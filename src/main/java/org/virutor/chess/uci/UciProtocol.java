package org.virutor.chess.uci;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.uci.commands.UciCommand;
import org.virutor.chess.ui.model.InvalidMoveException;

import java.io.*;

import static org.virutor.chess.uci.UciConstants.*;


/**
 * 
 * Thread safety: should be thread safe 
 * 
 * TODO: a) isolate the process logic (so that it could be used in android)
 * 
 * @author vaclav
 *
 */
public class UciProtocol {
	
	//FIXME: a) this is most likely broken b) it doesn't look good
	private static final Logger LOG = LogManager.getLogger(UciProtocol.class + ".second");
	private static final Logger UCI_LOG = LogManager.getLogger(UciProtocol.class);
	
	private String path;
	private final EngineInfo engineInfo;
	
	private UciEngineAgent uciEngineAgent;
	private InfoListener infoListener;
	
	private Process uciEngineProcess;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Thread workerThread;		
	
	//state 
	private volatile boolean quit;
	
	public UciProtocol(String path, UciEngineAgent uciEngineAgent, EngineInfo engineInfo) {
		this.path = path;
		this.uciEngineAgent = uciEngineAgent;
		this.engineInfo = engineInfo;
	}
	
	public UciProtocol(String path, UciEngineAgent uciEngineAgent) {
		this(path, uciEngineAgent, new EngineInfo());
	}
	
	public UciProtocol(String path) {
		this(path, null, new EngineInfo());
	}	
	
	public UciProtocol(String path, EngineInfo engineInfo) {
		this(path, null, engineInfo);
	}	
	
	public InfoListener getInfoListener() {
		return infoListener;
	}

	public void setInfoListener(InfoListener infoListener) {
		this.infoListener = infoListener;
	}

	public void setUiGame(UciEngineAgent uciEngineAgent) {
		this.uciEngineAgent = uciEngineAgent;
	}

	public EngineInfo getEngineInfo() {
		return engineInfo;
	}
	
	/**
	 * Starts the engine synchronously
	 * TODO think about asynchronous behavior
	 * @throws UciProtocolException
	 */
	public void start() throws UciProtocolException {
		
		//TODO better exception handling
		try {
			
			uciEngineProcess = Runtime.getRuntime().exec(path);
			LOG.info("Uci engine process started");
			
		} catch (IOException e) {
			throw new UciProtocolException(e);
		}

		quit = false;
		
		OutputStream clientInputStream = uciEngineProcess.getOutputStream();
		InputStream clientOutputStream = uciEngineProcess.getInputStream();
		
		bufferedReader = new BufferedReader(new InputStreamReader(clientOutputStream));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientInputStream));

		workerThread = new Thread(new ReadWorker());
		workerThread.start();		
		LOG.info("RaeadWorker thread started");
		
		writeCommand(ServerToEngineUciCommand.COMMAND_UCI);

	}

	public void stop() {
		writeCommand(ServerToEngineUciCommand.COMMAND_STOP);
	}

	public static enum QuitStatus {
		QUIT_ALREADY,
		OK,
		FORCED
	}
	
	public QuitStatus quit() {
		//TODO rethink states
		if(quit) {
			return QuitStatus.QUIT_ALREADY;
		}
		quit = true;
		writeCommand(ServerToEngineUciCommand.COMMAND_QUIT);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) { }
		try{ 
			int ret = uciEngineProcess.exitValue();
			LOG.trace("Uci engine process returned " + ret);
			return QuitStatus.OK;
		} catch (IllegalThreadStateException e) {
			LOG.trace("Uci engine process had to be destroyed");
			uciEngineProcess.destroy();			
			return QuitStatus.FORCED;
		}		 
	}
	
	private class ReadWorker implements Runnable { 
		
		public void run() {
		
			try {
				do {
					String line = readCommand();
					processCommand(line); 
				} while(!quit);
				
			} catch (Throwable e) {
				//TODO
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	
	protected void processCommand(String line) {		
		
		if(line == null) {
			return; //probably end of life but TODO make it better 
		}	
		
		String[] words = line.split(" ");
		
		//TODO is uci case-insensitive?
		if(!UciConstants.READ_COMMANDS.contains(words[0])) {
			//NOTE: don't throw exception, just ignore 
			LOG.trace("Cannot understand: " + line + ", ignoring");
			return;
		}	
		
		//TODO is uci case-insensitive?
		String firstWord = words[0].toLowerCase();
		if(ID.equals(firstWord)) {
			
			UciEngineInfoUtils.handleId(engineInfo, words);
			//TODO notify engine ??
			
		} else if(UCI_OK.equals(firstWord)) {
			
			writeCommand(ServerToEngineUciCommand.COMMAND_IS_READY);
			
		} else if(OPTION.equals(firstWord)) {
			
			UciEngineInfoUtils.handleOptions(engineInfo, words);
			//TODO notify engine ??
			
		} else if(READY_OK.equals(firstWord)) {
			
			uciEngineAgent.notifyReady();
			
		} else if(INFO.equals(firstWord)) {
			
			if(infoListener != null) {
				try {
					ComputationInfo computationInfo = ComputationInfo.parse(line);
					infoListener.onInfo(computationInfo);
				} catch (Exception e) {
					LOG.error("Cannot parse info line: " + line, e);
				}
				
			}
			
			
		} else if(BEST_MOVE.equals(firstWord)) {			
			
			try {
				uciEngineAgent.play(new LongAlgebraicMove(words[1]));
			} catch (InvalidMoveException e) {
				// TODO
				throw new IllegalArgumentException(e);
			}
		}
	}
	
	public void sendCommand(UciCommand uciCommand) {
		for (String str : uciCommand.getStringCommands()) {
			writeCommand(str);
		}
	}
	
	private void writeCommand(ServerToEngineUciCommand command) {	
		writeCommand(command.toString());
	}
	

	
	protected void writeCommand(String command) {	

		if(command == null) {
			throw new NullPointerException();
		}
		
		synchronized(bufferedWriter) {
			try {
				bufferedWriter.write(command + "\n");
				bufferedWriter.flush();
			} catch (IOException e) {
				//TODO
				throw new RuntimeException(e);
			}
		}
		UCI_LOG.info("------->" + command);
	}
	
	
	/**
	 * TODO try to use non busy waiting
	 * @return
	 * @throws IOException
	 */
	private String readCommand() throws IOException {
		while(!bufferedReader.ready()) {
			try {
				Thread.sleep(0, 10000);
			} catch (InterruptedException e) { /*ignore*/ }
			if(quit) {
				return null;
			}			
		}
		String read = bufferedReader.readLine();
		UCI_LOG.info("<-------" + read);
		return read;
	}

}
