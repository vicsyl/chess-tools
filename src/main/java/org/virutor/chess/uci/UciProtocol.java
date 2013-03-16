package org.virutor.chess.uci;

import static org.virutor.chess.uci.UciConstants.ID;
import static org.virutor.chess.uci.UciConstants.OPTION;
import static org.virutor.chess.uci.UciConstants.READY_OK;
import static org.virutor.chess.uci.UciConstants.UCI_OK;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.uci.GameServerTemp.InvalidMoveException;
import org.virutor.chess.uci.commands.UciCommand;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;




/**
 * 
 * Thread safety: should be thread safe 
 * 
 * @author vaclav
 *
 */
public class UciProtocol {

	private static final Logger LOG = Logger.getLogger(UciProtocol.class); 
	
	private String path;
	private EngineInfo engineInfo = new EngineInfo();
	private GameServerTemp gameServer;	
	private Process uciEngineProcess;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Thread workerThread;		
	
	//state 
	private volatile boolean quit;
	
	public UciProtocol(String path, GameServerTemp gameServer) {
		this.path = path;
		this.gameServer = gameServer;
	}
	
	public UciProtocol(String path) {
		this(path, null);
	}	

	public void setGameServer(GameServerTemp gameServer) {
		this.gameServer = gameServer;
	}

	public EngineInfo getEngineInfo() {
		return engineInfo;
	}

	/*
	public void setEngineInfo(EngineInfo engineInfo) {
		this.engineInfo = engineInfo;
	}*/



	/**
	 * Starts the engine synchronously
	 * TODO think about asynchronous behavior
	 * @throws UciProtocolException
	 */
	public void start() throws UciProtocolException {
		
		
		try {
			
			uciEngineProcess = Runtime.getRuntime().exec(path);
			LOG.info("Uci engine process started");
			
		} catch (IOException e) {
			throw new UciProtocolException(e);
		}

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

	public void quit() {
		quit = true;
		writeCommand(ServerToEngineUciCommand.COMMAND_QUIT);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) { }
		try{ 
			int ret = uciEngineProcess.exitValue();
			LOG.trace("Uci engine process returned " + ret);
		} catch (IllegalThreadStateException e) {
			LOG.trace("Uci engine process had to be destroyed");
			uciEngineProcess.destroy();			
		}		 
	}
	
	public void play(LongAlgebraicMove move) {
		throw new NotImplementedException();
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
				LOG.info(e.getMessage());
			}
		}
	}
	
	
	private void processCommand(String line) {		
		
		if(line == null) {
			throw new NullPointerException();
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
			gameServer.notifyReady();	
		} else {
			try {
				gameServer.play(new LongAlgebraicMove(words[1]));
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
	

	
	private void writeCommand(String command) {	

		if(command == null) {
			throw new NullPointerException();
		}
		
		synchronized(bufferedWriter) {
			try {
				bufferedWriter.write(command + "\n");
				bufferedWriter.flush();
			} catch (IOException e) {
				//TODO
				throw new RuntimeException();
			}
		}
		LOG.info("------->" + command);
	}
	
	
	/**
	 * TODO try to use non busy waiting
	 * @return
	 * @throws IOException
	 */
	private String readCommand() throws IOException {
		while(!bufferedReader.ready()) {
			if(quit) {
				return null;
			}			
		}
		String read = bufferedReader.readLine();
		LOG.info("<-------" + read);
		return read;
	}

}
