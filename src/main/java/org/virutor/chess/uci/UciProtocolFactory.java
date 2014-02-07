package org.virutor.chess.uci;

public class UciProtocolFactory {

	public static interface UciProtocolFactoryInterface {
		UciProtocol newUciProtocol(String path, GameServerTemp gameServer, EngineInfo engineInfo);	
	} 
	
	private static final UciProtocolFactoryInterface DEFAULT_IMPL = new UciProtocolFactoryInterface() {
		
		@Override
		public UciProtocol newUciProtocol(String path, GameServerTemp gameServer, EngineInfo engineInfo) {			
			return new UciProtocol(path, gameServer, engineInfo);
		}
	};
	
	public static UciProtocolFactoryInterface uciProtocolFactoryImpl = DEFAULT_IMPL;	 
	
}
