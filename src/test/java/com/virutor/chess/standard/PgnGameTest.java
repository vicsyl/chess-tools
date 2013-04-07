package com.virutor.chess.standard;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.model.Game.ResultExplanation;
import org.virutor.chess.standard.PgnDate;
import org.virutor.chess.standard.PgnGame;
import org.virutor.chess.standard.PgnGameSuite;
import org.virutor.chess.standard.PgnRound;

@RunWith(Parameterized.class)
public class PgnGameTest {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static class ExpectedValuesForGame {
		Result result;
		ResultExplanation resultExplanation;
		PgnRound pgnRound;
		PgnDate pgnDate;
		Map<String, Object> otherProperties = new HashMap<String, Object>(); 
	}
	

	private static class ExpectedValues {
		
	
		Integer numberOfGamesInsuite;
		Map<Integer, ExpectedValuesForGame> expectedValuesForGames = new HashMap<Integer, ExpectedValuesForGame>();
		
		//TODO rename		
		ExpectedValuesForGame getExpectedValuesForGame(Integer game) {
			if(!expectedValuesForGames.containsKey(game)) {
				expectedValuesForGames.put(game, new ExpectedValuesForGame());
			}
			return expectedValuesForGames.get(game);
		}
	
	}
	
	private final String path;
	private final ExpectedValues expectedValues;
	
	public PgnGameTest(String path, ExpectedValues expectedValues) {
		this.path = path;
		this.expectedValues = expectedValues;
	}

	
	@Parameters
	public static Collection<Object[]> getParameters() {

		List<Object[]> ret = new ArrayList<Object[]>();
		
		ret.add(new Object[] {"/adam_virutor_1.1_1_game.pgn", new ExpectedValues()});		
		
		ExpectedValues exp = new ExpectedValues();
		exp.numberOfGamesInsuite = 9;		
		
		ExpectedValuesForGame expectedValuesForGame = exp.getExpectedValuesForGame(0);

		//TODO redo with exact game
		PgnDate pgnDate = new PgnDate("2012.12.30");
		Assert.assertTrue(pgnDate.getExactDate() != null);
		expectedValuesForGame.pgnDate = pgnDate;
		expectedValuesForGame.pgnRound = PgnRound.UNKNOWN;
		expectedValuesForGame.resultExplanation = ResultExplanation.MATE;
		expectedValuesForGame.result = Result.WHITE_WINS;
		
		
		ret.add(new Object[] {"/adam_virutor_1.1_9_games.pgn", exp});

		
		ret.add(new Object[] {"/sample_pgn.pgn", new ExpectedValues()});
		ret.add(new Object[] {"/fritz_11_variation.pgn", new ExpectedValues()});
		ret.add(new Object[] {"/arena_not_starting_position.pgn", new ExpectedValues()});
		
		
		return ret;

	}
	
	
	//TODO remove??
	@Test
	public void formattedCheckedTest() throws Exception {
		
		PgnGameSuite pgnGamesOriginal = PgnGame.parse(getClass().getResourceAsStream(path));
		

		StringWriter stringWriter = new StringWriter();
		pgnGamesOriginal.write(stringWriter);		
		String stringRepresentation = stringWriter.toString();	
		
	}
	
	@Test
	public void parsedFormatParsed() throws Exception {
		
		PgnGameSuite pgnGamesOriginal = PgnGame.parse(getClass().getResourceAsStream(path));

		
		StringWriter stringWriter = new StringWriter();

		pgnGamesOriginal.write(stringWriter);		
		String stringRepresentation = stringWriter.toString();		
		InputStream istream = new ByteArrayInputStream(stringRepresentation.getBytes());
		
		PgnGameSuite pgnGamesCopy = PgnGame.parse(istream);
		
		Assert.assertEquals(pgnGamesOriginal.pgnGames, pgnGamesCopy.pgnGames);
		
	}
	

	@Test
	public void originalParseFormat() throws Exception {
		
		//TODO original sample_pgn.pgn has '0-1' at the end, 
		// but it is indeed mate (#). What is the proper formatting ? #implementation #standard 
		
		StringBuilder sb = inputStreamAsStringBuilder(getClass().getResourceAsStream(path));		
		PgnGameSuite pgnGamesOriginal = PgnGame.parse(getClass().getResourceAsStream(path));
		
		StringWriter stringWriter = new StringWriter();
		pgnGamesOriginal.write(stringWriter);		
		String originalStringRepresentation = stringWriter.toString();		
		/*
		String expectedWithNoWhiteSpaces = sb.toString().replaceAll("\\s", "");
		String realWithNoWhiteSpaces = originalStringRepresentation.replaceAll("\\s", "");
		Assert.assertEquals(expectedWithNoWhiteSpaces, realWithNoWhiteSpaces);

		String expectedWithNoLineBreaks = sb.toString().replaceAll("\n", "");
		String realWithNoLineBreaks = originalStringRepresentation.replaceAll("\n", "");
		Assert.assertEquals(expectedWithNoLineBreaks, realWithNoLineBreaks);
		*/
		Assert.assertEquals(sb.toString(), originalStringRepresentation);
		
	}


	@Test
	public void parsedCheckedTest() throws Exception {
		
		PgnGameSuite pgnGamesOriginal = PgnGame.parse(getClass().getResourceAsStream(path));
		
		if(expectedValues.numberOfGamesInsuite != null) {
			Assert.assertEquals(expectedValues.numberOfGamesInsuite.intValue(), pgnGamesOriginal.pgnGames.size());
			System.out.println("Expected number of games: " + expectedValues.numberOfGamesInsuite);
		}
		
		for(Map.Entry<Integer, ExpectedValuesForGame> entry : expectedValues.expectedValuesForGames.entrySet()) {
			PgnGame pgnGame = pgnGamesOriginal.pgnGames.get(entry.getKey());
			ExpectedValuesForGame expectedValuesForGame = entry.getValue();				
			assertExpectedValuesForGame(expectedValuesForGame, pgnGame);				
		}		
		
	}
	
	@Test
	public void formatParseFormat() throws Exception {
		
		PgnGameSuite pgnGamesOriginal = PgnGame.parse(getClass().getResourceAsStream(path));
		
		StringWriter stringWriter = new StringWriter();
		pgnGamesOriginal.write(stringWriter);		
		String originalStringRepresentation = stringWriter.toString();
		
		
		InputStream istream = new ByteArrayInputStream(originalStringRepresentation.getBytes());
		
		PgnGameSuite pgnGamesCopy = PgnGame.parse(istream);

		StringWriter copyWriter = new StringWriter();
		pgnGamesCopy.write(copyWriter);		
		String copyStringRepresentation = copyWriter.toString();
		
		Assert.assertEquals(originalStringRepresentation, copyStringRepresentation);

		
	}
	
	private void assertExpectedValuesForGame(ExpectedValuesForGame expectedValuesForGame, PgnGame pgnGame) {
		
		assertEqualsToStringIfExpectedNotNull(expectedValuesForGame.pgnDate, pgnGame.getDate());
		assertEqualsToStringIfExpectedNotNull(expectedValuesForGame.pgnRound, pgnGame.getPgnRound());
		assertEqualsIfExpectedNotNull(expectedValuesForGame.result, pgnGame.getResult());
		assertEqualsIfExpectedNotNull(expectedValuesForGame.resultExplanation, pgnGame.getResultExplanation());
		if(expectedValuesForGame.otherProperties != null) {
			Assert.assertTrue(pgnGame.getProperties().entrySet().containsAll(expectedValuesForGame.otherProperties.entrySet()));
		}
		
	}
	
	private static void assertEqualsIfExpectedNotNull(Object expected, Object actual) {
		if(expected != null) {
			Assert.assertEquals(expected, actual); 
		}
	}

	private static void assertEqualsToStringIfExpectedNotNull(Object expected, Object actual) {
		if(expected != null) {
			Assert.assertEquals(expected.toString(), actual.toString()); 
		}
	}

	

	
	private StringBuilder inputStreamAsStringBuilder(InputStream input) throws IOException {
		StringBuilder ret = new StringBuilder();
		
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input)); 
		String line = null;
		while((line = bufferedReader.readLine()) != null) {
			ret.append(line);
			ret.append(LINE_SEPARATOR);
		}	
		
		IOUtils.closeQuietly(input);
		
		return ret;
	}
	
	
}
