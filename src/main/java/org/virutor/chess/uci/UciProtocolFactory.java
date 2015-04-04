package org.virutor.chess.uci;

import org.virutor.chess.ui.model.UiGame;

public class UciProtocolFactory {

	public static interface UciProtocolFactoryInterface {
		UciProtocol newUciProtocol(String path, UciEngineAgent uciEngineAgent, EngineInfo engineInfo);
	} 
	
	private static final UciProtocolFactoryInterface DEFAULT_IMPL = new UciProtocolFactoryInterface() {
		
		@Override
		public UciProtocol newUciProtocol(String path, UciEngineAgent uciEngineAgent, EngineInfo engineInfo) {
			return new UciProtocol(path, uciEngineAgent, engineInfo);
		}
	};
	
	public static UciProtocolFactoryInterface uciProtocolFactoryImpl = DEFAULT_IMPL;	 
	
}
