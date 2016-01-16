package org.virutor.chess.headless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.virutor.chess.config.UciEngine;
import org.virutor.chess.standard.time.IncrementalTimeControl;
import org.virutor.chess.tournament.TournamentData;
import org.virutor.chess.tournament.TournamentEndListener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class HeadlessApplicationTest {

    private static Logger logger = LogManager.getLogger(HeadlessApplicationTest.class);

    private static String AGENT_PATH = HeadlessApplication.class.getResource("/engines/VirutorChess_1.1.1/bin/VirutorChessUci_1.1.1.exe").getPath();

    private static UciEngine getUciEngine() {
        UciEngine ret = new UciEngine();
        ret.setPath(AGENT_PATH);
        //FIXME for the time being name is always the same as path
        ret.setName(AGENT_PATH);
        return ret;
    }

    private void assertUciEngine(UciEngine actual, UciEngine expected) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getPath(), actual.getPath());
    }

    @Test
    public void runHeadlessApplicationRunMainTest() throws InterruptedException {

        TournamentData tournamentData = HeadlessApplication.parseTournamentData(("e " + AGENT_PATH + " rc 8").split(" "));

        Assert.assertEquals(8, tournamentData.getRoundCount());
        Assert.assertEquals(1, tournamentData.getTimeControls().size());
        Assert.assertTrue(tournamentData.getTimeControls().get(0) instanceof IncrementalTimeControl);
        IncrementalTimeControl actualTimeControl = (IncrementalTimeControl)tournamentData.getTimeControls().get(0);
        Assert.assertEquals(15, actualTimeControl.getBase());
        Assert.assertEquals(0, actualTimeControl.getIncrement());
        Assert.assertEquals(2, tournamentData.getUciEngines().size());
        assertUciEngine(getUciEngine(), tournamentData.getUciEngines().get(0));
        assertUciEngine(getUciEngine(), tournamentData.getUciEngines().get(1));
    }

    @Test
    public void runHeadlessApplicationTest() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Exception[] exceptions = new Exception[1];

        TournamentEndListener listener = (tournamentGames, resultFileName) -> {

            try (Stream<String> lines = Files.lines(Paths.get(resultFileName))) {
                lines.forEach(logger::info);
                File file = new File(resultFileName);
                file.delete();
            } catch (Exception e) {
                exceptions[0] = e;
            } finally {
                countDownLatch.countDown();
            }
        };

        TournamentData tournamentData = HeadlessApplication.parseTournamentData(("e " + AGENT_PATH + " rc 1").split(" "));
        new HeadlessApplication().startTournament(tournamentData, listener);
        countDownLatch.await();

        if(exceptions[0] != null) {
            throw exceptions[0];
        }
    }

}
