package com.virutor.chess.standard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.standard.PgnDate;

@RunWith(Parameterized.class)
public class PgnDateTests {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	private static class ExpectedData {
		public Date exactDate;
		public boolean shouldFail;

		public ExpectedData(Date exactDate) {
			this.exactDate = exactDate;
		}

		public ExpectedData(Date exactDate, boolean shouldFail) {
			this.exactDate = exactDate;
			this.shouldFail = shouldFail;
		}
		public ExpectedData() {}
		
	}
	
	private String inputString;
	private ExpectedData expectedData;

	
	public PgnDateTests(String inputString, ExpectedData expectedData) {
		this.inputString = inputString;
		this.expectedData = expectedData;
	}


	//TODO add	
	@Parameters
	public static Collection<Object[]> getData() throws ParseException {

		List<Object[]> ret = new ArrayList<Object[]>();
		
		ret.add(new Object[]{"????.??.??", new ExpectedData()});
		ret.add(new Object[]{"2023.??.??", new ExpectedData()});
		ret.add(new Object[]{"????.10.10", new ExpectedData()});
		ret.add(new Object[]{"10??.??.??", new ExpectedData()});

		ret.add(new Object[]{"xx??.??.??", new ExpectedData(null, true)});
		ret.add(new Object[]{"?.??.??.??", new ExpectedData(null, true)});
		
		
		String dateString = "2011.08.31"; 
		Date date = DATE_FORMAT.parse(dateString);
		ret.add(new Object[]{dateString,new ExpectedData(date)});
		
		return ret;
		
	}
	
	@Test
	public void creationTests() {
		
		if(expectedData.shouldFail) {
			//skipping
			return;
		}
		
		PgnDate pgnDate = new PgnDate(inputString);  
		if(expectedData.exactDate != null) {
			Assert.assertEquals(expectedData.exactDate, pgnDate.getExactDate());
		}
		
	}
	
	@Test(expected = IllegalArgumentException.class)	
	public void negativeCreationTest() {

		if(!expectedData.shouldFail) {
			//skipping
			throw new IllegalArgumentException();
		}
		
		new PgnDate(inputString);  
		
	}
	
	
}
