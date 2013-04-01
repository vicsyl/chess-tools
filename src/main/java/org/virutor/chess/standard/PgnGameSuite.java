package org.virutor.chess.standard;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PgnGameSuite {

	public List<PgnGame> pgnGames = new ArrayList<PgnGame>();
	
	public String toString() {		
		return pgnGames.toString();		
	}	
	
	public void write(Writer writer) throws IOException {
		for(int i = 0; i < pgnGames.size(); i++) {			
			writer.append(pgnGames.get(i).format());
			if(i < pgnGames.size() - 1) {
				writer.append("\n\n");
			}
		}
	} 
	
}
