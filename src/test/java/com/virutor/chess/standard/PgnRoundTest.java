package com.virutor.chess.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.standard.PgnRound;

@RunWith(Parameterized.class)
public class PgnRoundTest {

	private static class ExpectedData {
		
		boolean shouldFail;
		PgnRound expectedInstance;
		
		ExpectedData(boolean shouldFail) {
			this.shouldFail = shouldFail;
		}

		ExpectedData(PgnRound expectedInstance) {
			this.expectedInstance = expectedInstance;
		}		
		
	}
	
	private String inputString;
	private ExpectedData expectedData;
	
	public PgnRoundTest(String inputString, ExpectedData expectedData) {
		this.inputString = inputString;
		this.expectedData = expectedData;
	}

	@Parameters
	public static Collection<Object[]> getData() {
		
		List<Object[]> ret = new ArrayList<Object[]>();
		
		ret.add(new Object[]{"??", new ExpectedData(true)});
		ret.add(new Object[]{"a", new ExpectedData(true)});
		ret.add(new Object[]{"1.", new ExpectedData(true)});
		ret.add(new Object[]{"-", new ExpectedData(PgnRound.NOT_APPLICABLE)});
		ret.add(new Object[]{"?", new ExpectedData(PgnRound.UNKNOWN)});
		ret.add(new Object[]{"33", new ExpectedData(false)});
		ret.add(new Object[]{"445.1.5", new ExpectedData(false)});
	
		
		return ret;
	}
	
	@Test
	public void creationTests() {
		
		if(expectedData.shouldFail) {
			//skipping
			return;
		}
		
		PgnRound pgnRound = PgnRound.getInstance(inputString);  
		if(expectedData.expectedInstance != null) {
			Assert.assertTrue(expectedData.expectedInstance == pgnRound);
		}
		Assert.assertEquals(inputString, pgnRound.toString());
		
	}
	
	@Test(expected = IllegalArgumentException.class)	
	public void negativeCreationTest() {

		if(!expectedData.shouldFail) {
			//skipping
			throw new IllegalArgumentException();
		}
		
		PgnRound.getInstance(inputString);  
		
	}
	
	
}
