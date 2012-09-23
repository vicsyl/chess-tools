package org.virutor.chess.uci;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import org.virutor.chess.log.LoggerServiceFactory;
import org.virutor.chess.model.Move;
import org.virutor.chess.model.io.LongAlgebraicMove;
import org.virutor.chess.server.GameStateChangeListener;
import org.virutor.chess.server.GameStateServer;
import org.virutor.chess.server.Player;
import org.virutor.chess.uci.UciOption.UciOptionType;
import org.virutor.chess.uci.UciOption.UciOptionValueType;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class UciEngine implements Runnable, Player, GameStateChangeListener {

	
	//Engine to GUI:
	private static final String ID = "id";
	private static final String UCI_OK = "uciok";
	private static final String READY_OK = "readyok";
	private static final String BEST_MOVE = "bestmove";
	private static final String COPY_PROTECTION = "copyprotection";
	private static final String REGISTRATION = "registration";
	private static final String INFO = "info";
	private static final String OPTION = "option";
	
	//GUI to engine
	private static final String QUIT = "quit";
	private static final String IS_READY = "isready";
	private static final String POSITION_ = "position ";
	private static final String STARTPOS_ = "startpos ";
	private static final String MOVES_ = "moves ";
	private static final String GO = "go";
	private static final String STOP = "stop";
	
	
	private static final List<String> IMPLEMENTED_READ_COMMANDS = Arrays.asList(new String[] {
		ID, UCI_OK, READY_OK, OPTION, BEST_MOVE	
	});
	
	private boolean canUnderstand(String firstWord) {
		String[] words = firstWord.split(" ");
		if(words == null || words.length == 0) {
			return false;
		}
		//TODO discuss UCI
		return IMPLEMENTED_READ_COMMANDS.contains(words[0].toLowerCase());
	}
	
	
	//private Queue<Message> messageQueue;
	private GameStateServer gameStateServer;
	private String name;
	private String path;
	private Thread readThread;
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	private boolean started = false;
	
	private synchronized void setStarted() {
		 started = true;
	}
	public synchronized boolean isStarted() {
		return started;
	}
	
	public UciStates uciState = UciStates.START;

	public EngineInfo engineInfo = new EngineInfo();
	
	public UciEngine(String path, GameStateServer gameStateServer) {
	
		this.name = path;
		this.path = path;
		this.gameStateServer = gameStateServer;		
		
	}	
	
	public void writeCommand(String command) {		
		//System.out.println("------->" + command);
		try {
			bufferedWriter.write(command + "\n");
			bufferedWriter.flush();
		} catch (IOException e) {
			//TODO
		}
	}
	
	private String readCommand() throws IOException {
		while(!bufferedReader.ready()) {
			if(isQuit()) {
				return null;
			}			
		}
		String read = bufferedReader.readLine();
		//System.out.println("<-------" + read);
		return read;
	}

private UciOption handleOption(String[] words) {
		
		UciOption uciOption = new UciOption();
		
		final String NAME = "name";
		final String TYPE = "type";
		final String MIN = "min";
		final String MAX = "max";
		final String DEFAULT = "default";
		final String VAR = "var";		
		
		final List<String> keywords = Arrays.asList(new String[] {NAME, TYPE, MIN, MAX, DEFAULT, VAR});
		
		for(int i = 1; i < words.length; i++) {
			if(!keywords.contains(words[i])) {
				System.out.println("cannot understand option keyword:" + words[i]);
				return uciOption;
			}
			if(i == words.length - 1) {
				System.out.println("no value after keyword:" + words[i]);
				return uciOption;					
			}
			if(NAME.equals(words[i])) {
				uciOption.name = words[i+1];
				i++;
			} else if(TYPE.equals(words[i])) {
				boolean success = false;
				for(UciOptionType uciOptionType : UciOptionType.values()) {
					if(uciOptionType.name().equalsIgnoreCase(words[i+1])) {
						uciOption.type = uciOptionType;
						i++;
						success = true;
						break;
					}
				}
				if(!success) {
					//TODO cannot understabda type
				}
			} else {
				for(UciOptionValueType uciOptionValueType : UciOptionValueType.values()) {
					if(uciOptionValueType.name().equalsIgnoreCase(words[i])) {
						uciOption.values.put(uciOptionValueType, words[i+1]);
						i++;
						break;
					}
				}
			} 
		}
		
		return uciOption;
	}
	
	private void handleId(String[] words) {
		if(words.length < 3) {
			return;
		}
		if(words[1].equals("name")) {
			engineInfo.name = join(words, 2);
		} else if (words[2].equals("author")) {
			engineInfo.author = join(words, 2);
		}
	}
	
	private String join(String[] array, int from) {
		StringBuffer sb = new StringBuffer();
		for(int i = from; i < array.length; i++) {
			if(i != from) {
				sb.append(" ");
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}
	
	/*
	public static void main(String[] args) {
		
		try {
			
			Connector connector = new Connector("C:\\devel\\C++\\EclipseCppWorkspace\\VirutorChessMake\\bin\\Debug\\VirutorChessUci.exe");
			
			Thread connectorThread = new Thread(connector);
			//connectorThread.setDaemon(false);
			connectorThread.start();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Type 'quit' to exit");
			String line = null;
			
			while((line = bufferedReader.readLine()) != null) {
				if(line.equals("quit")) {
					connector.setQuit(true);
					break;
				} else {
					System.out.println("It's not quit it is '" + line + "'");
				}				
			}
			
			//connectorThread.stop();
			Thread.sleep(1000);
			System.out.println("Quitting main thread");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	*/
	private boolean quit = false;
		
	synchronized public boolean isQuit() {
		return quit;
	}

	synchronized public void setQuit(boolean quit) {
		this.quit = quit;
	}
	
	
	@Override
	public void run() {
	
		writeCommand("uci");
		
		try {
			while(!isQuit()) {
				String line = readCommand();
				processCommand(line); 
			}
			
		} catch (Throwable e) {
			//TODO
		}
		
	}

	private void processCommand(String line) {		
			
		if(line == null) {
			return;
		}	
		
		String[] words = line.split(" ");
		if(words == null || words.length == 0) {
			return;
		}		
		
		if(!canUnderstand(words[0])) {
			System.out.println(" Cannot understand: " + line);
			return;
		}	
		
		System.out.println(Thread.currentThread().getId() + ": Understands: " + line);
		
		String firstWord = words[0].toLowerCase();
		if(ID.equals(firstWord)) {
			handleId(words);
		} else if(UCI_OK.equals(firstWord)) {					
			writeCommand(IS_READY);
		} else if(OPTION.equals(firstWord)) {
			UciOption option = handleOption(words);
			engineInfo.options.put(option.name, option);
			System.out.println("Read option: " + option.toString());
		} else if(READY_OK.equals(firstWord)) {
			setStarted();
		} else {
			boolean handled = handleBestMove(words);
		}
			
		
	}
	
	private boolean handleBestMove(String[] words) {
		if(!BEST_MOVE.equals(words[0].toLowerCase())) {			
			return false;
		}
		
		String bestMoveStr = words[1].toLowerCase();
		Move bestMove = null;
		for(Move move : gameStateServer.getGeneratedMoves()) {
			if(new LongAlgebraicMove(move).toString().toLowerCase().equals(bestMoveStr)) {
				bestMove = move;
				break;
			}			
		}
		if(bestMove == null) {
			//TODO 
			System.out.println("invalid move " + words[1]);
			return true;
		}
		
		if(!gameStateServer.doMove(bestMove, this)) {
			//TODO 
			System.out.println("cannot do move " + words[1]);
			return true;
		} else {
			System.out.println("move done");
		}
		
		return true;
	}
	
	@Override
	public String getName() {		
		return name;
	}

	@Override
	public void moveDone(Move move) {
		play();
	}

	@Override
	public void start() {
		
		Process p = null;
		try {
			
			p = Runtime.getRuntime().exec(path);
			
		} catch (IOException e) {

			//TODO
			e.printStackTrace();
		}

		OutputStream clientInputStream = p.getOutputStream();
		InputStream clientOutputStream = p.getInputStream();
		
		bufferedReader = new BufferedReader(new InputStreamReader(clientOutputStream));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientInputStream));

		LoggerServiceFactory.getLogger().info(Thread.currentThread().getId() + "Starting thread");
		readThread = new Thread(this);
		readThread.start();
		LoggerServiceFactory.getLogger().info("Thread started");

	}

	@Override
	public void quit() {
		writeCommand(QUIT);
		readThread.interrupt();
		/*
		try {
			//readThread.join();
		} catch(InterruptedException e) {
			//TODO
		};*/ 
	}

	@Override
	public void play() {

		StringBuilder sb = new StringBuilder();
		sb.append(POSITION_);
		boolean isStart = gameStateServer.isFirstStartPosition();
		if(isStart) {
			sb.append(STARTPOS_);			
		} else {
			//TODO
			throw new NotImplementedException();
		}
		List<Move> moves = gameStateServer.getMoves();
		if(!moves.isEmpty()) {
			sb.append(MOVES_);
			boolean firstFlag = true;
			for(Move move : moves) {
				if(firstFlag) {
					firstFlag = false;
				} else {
					sb.append(" ");
				}
				sb.append(new LongAlgebraicMove(move).toString());				
			}
		}
		
		writeCommand(sb.toString());
		
		//TODO
		writeCommand(GO);
	}
	@Override
	public void stop() {
		writeCommand(STOP);
	}
	@Override
	public void notifyChange() { /*DO NOTHING */ }

	
}
