package org.virutor.chess.config;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class MultiPropertiesTest {

	private static URL FILE_URL = MultiPropertiesTest.class.getResource("/config/EnginesPlayers.txt");
	
	
	@Test
	public void readSaveReadTest() throws Exception {
		
		
		//File currentDir = new File("").getAbsoluteFile();		
		//currentDir = new File("target\\classes\\VirutorChess_1.1\\bin\\VirutorChessUci_1.1.exe").getAbsoluteFile();		
		
		MultiProperties multiProperties = new MultiProperties(FILE_URL.getFile().toString());

		Map<String, List<Map<String, String>>> ret = multiProperties.load();
				

		String parent = new File(FILE_URL.getFile()).getParent();
		
		String out = new File(new File(parent), "out.txt").getPath();
		
		MultiProperties.save(out, ret);
		
		multiProperties = new MultiProperties(out);
		Map<String, List<Map<String, String>>> ret2 = multiProperties.load();
		
		Assert.assertEquals(ret, ret2);
		
		System.out.println(new File(FILE_URL.getFile()).getParent());
		
		
		
		System.out.println(ret);
		
	}
	
}
