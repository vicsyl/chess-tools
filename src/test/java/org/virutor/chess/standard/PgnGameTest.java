package org.virutor.chess.standard;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.model.Game.ResultExplanation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO:
 *  - improve the test comparing a parsed PGN game and an actual expected PgnGame object
 *  - implement export format of PGN game and test for byte equality
 *
 */
@RunWith(Parameterized.class)
public class PgnGameTest {

    private static class ExpectedValuesForGame {

        Result result;
        ResultExplanation resultExplanation;
        PgnRound pgnRound;
        PgnDate pgnDate;
        Map<String, Object> otherProperties = new HashMap<>();
    }

    private static class ExpectedValues {

        Integer numberOfGamesInSuite;
        Map<Integer, ExpectedValuesForGame> expectedValuesForGames = new HashMap<>();

        //TODO rename
        ExpectedValuesForGame getExpectedValuesForGame(Integer game) {
            if (!expectedValuesForGames.containsKey(game)) {
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

        List<Object[]> ret = new ArrayList<>();

        ret.add(new Object[]{"/adam_virutor_1.1_1_game.pgn", new ExpectedValues()});

        ExpectedValues exp = new ExpectedValues();
        exp.numberOfGamesInSuite = 9;

        ExpectedValuesForGame expectedValuesForGame = exp.getExpectedValuesForGame(0);

        //TODO redo with exact game
        PgnDate pgnDate = new PgnDate("2012.12.30");
        Assert.assertTrue(pgnDate.getExactDate() != null);
        expectedValuesForGame.pgnDate = pgnDate;
        expectedValuesForGame.pgnRound = PgnRound.UNKNOWN;
        expectedValuesForGame.resultExplanation = ResultExplanation.MATE;
        expectedValuesForGame.result = Result.WHITE_WINS;

        ret.add(new Object[]{"/adam_virutor_1.1_9_games.pgn", exp});

        ret.add(new Object[]{"/sample_pgn.pgn", new ExpectedValues()});
        ret.add(new Object[]{"/fritz_11_variation.pgn", new ExpectedValues()});
        ret.add(new Object[]{"/arena_not_starting_position.pgn", new ExpectedValues()});

        return ret;

    }

    @Test
    public void parsedFormatParsed() throws Exception {

        PgnGameSuite pgnGamesOriginal = PgnGame.parse(getClass().getResourceAsStream(path));

        StringWriter stringWriter = new StringWriter();

        pgnGamesOriginal.write(stringWriter);
        String stringRepresentation = stringWriter.toString();
        InputStream istream = new ByteArrayInputStream(stringRepresentation.getBytes());

        PgnGameSuite pgnGamesCopy = PgnGame.parse(istream);

        Assert.assertEquals(pgnGamesOriginal, pgnGamesCopy);

    }

    @Test
    public void parsedCheckedTest() throws Exception {

        PgnGameSuite pgnGamesOriginal = PgnGame.parse(getClass().getResourceAsStream(path));

        if (expectedValues.numberOfGamesInSuite != null) {
            Assert.assertEquals(expectedValues.numberOfGamesInSuite.intValue(), pgnGamesOriginal.pgnGames.size());
        }

        for (Map.Entry<Integer, ExpectedValuesForGame> entry : expectedValues.expectedValuesForGames.entrySet()) {
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
        if (expectedValuesForGame.otherProperties != null) {
            Assert.assertTrue(pgnGame.getProperties().entrySet().containsAll(expectedValuesForGame.otherProperties.entrySet()));
        }

    }

    private static void assertEqualsIfExpectedNotNull(Object expected, Object actual) {
        if (expected != null) {
            Assert.assertEquals(expected, actual);
        }
    }

    private static void assertEqualsToStringIfExpectedNotNull(Object expected, Object actual) {
        if (expected != null) {
            Assert.assertEquals(expected.toString(), actual.toString());
        }
    }

}
