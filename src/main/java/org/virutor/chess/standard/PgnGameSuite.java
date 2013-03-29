package org.virutor.chess.standard;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PgnGameSuite {

	public List<PgnGame> games = new ArrayList<PgnGame>();
	
	public String toString() {		
		return games.toString();		
	}	
	
	public void write(Writer writer) throws IOException {
		for(int i = 0; i < games.size(); i++) {			
			writer.append(games.get(i).format());
			if(i < games.size() - 1) {
				writer.append("\n\n");
			}
		}
	} 
	
}
