package org.virutor.chess.application;

import com.virutor.chess.ui.config.ConfigStorage;
import com.virutor.chess.ui.config.DefaultConfigStorageImpl;

public class Services {

	//TODO !!!! REMOVE
	public static interface StatusBarHook {
		void log(String text);
	}
	
	public static ConfigStorage configStorage = new DefaultConfigStorageImpl();
	public static StatusBarHook statusBarHook;
	
}
