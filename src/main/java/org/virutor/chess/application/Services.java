package org.virutor.chess.application;

import org.virutor.chess.config.ConfigStorage;
import org.virutor.chess.config.DefaultConfigStorageImpl;

public class Services {

	//TODO !!!! REMOVE
	public static interface StatusBarHook {
		void log(String text);
	}
	
	public static ConfigStorage configStorage = new DefaultConfigStorageImpl();
	public static StatusBarHook statusBarHook;
	
}
