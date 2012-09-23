package org.virutor.chess.uci.server;


@Deprecated
public class OldUciServer {

	/*
	public void startEngine(String path, int timeout) throws IOException {
		
		UciEngine connector = null; //new UciEngine(path, messageQueue);
		connector.runProcess();
		
		Thread connectorThread = new Thread(connector);
		connectorThread.start();
		
		Date startDate = new Date();
				
		try {
			
			connector.writeCommand("uci");
			
			while((new Date()).getTime() - startDate.getTime() < timeout) {
				
				while(messageQueue.isEmpty()) {
					if(new Date().getTime() - startDate.getTime() >= timeout) {
						break;
					}
				};
				
				if(new Date().getTime() - startDate.getTime() >= timeout) {
					break;
				}

				
				processMessage(messageQueue.poll());
				
				
			
			}
			connector.writeCommand("quit");

		} catch (Throwable e) {	
			e.printStackTrace();
		}
		
		connector.setQuit(true);
		
		try {
			connectorThread.join();
		} catch (InterruptedException e) {
			// TODO 
		}
		
	} 
*/
	/*
	private void handleState() throws IOException {
		
		switch(uciState) {
			case START:
				uciState = UciStates.AFTER_START;
				//writeCommand("xboard");
				writeCommand("uci");
				break;
			case AFTER_START:		
				break;
			case UCI_OK:
				break;
		}
	}
	*/
	
	/*
	private void processMessage(Message message) {

		String line = message.getCommand();
		//System.out.println("Received: " + line);				

		if(line == null) {
			System.out.println(" message null");
			return;
		}
		
	
		
	}
	 */
	
}
