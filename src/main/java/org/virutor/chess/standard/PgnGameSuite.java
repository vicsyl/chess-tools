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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pgnGames == null) ? 0 : pgnGames.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PgnGameSuite other = (PgnGameSuite) obj;
		if (pgnGames == null) {
			if (other.pgnGames != null)
				return false;
		} else if (!pgnGames.equals(other.pgnGames))
			return false;
		return true;
	} 
	
}
