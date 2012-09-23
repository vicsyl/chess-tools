package org.virutor.chess.uci;

import java.util.HashMap;
import java.util.Map;

public class EngineInfo {

	public String author;
	public String name;
	public Map<String, UciOption> options = new HashMap<String, UciOption>();
}
