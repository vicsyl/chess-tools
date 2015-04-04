package org.virutor.chess.headless;

import com.virutor.chess.tournament.TournamentData;
import com.virutor.chess.tournament.TournamentEndListener;
import com.virutor.chess.tournament.TournamentGame;
import com.virutor.chess.ui.config.UciEngine;
import org.junit.Assert;
import org.junit.Test;
import org.virutor.chess.standard.time.IncrementalTimeControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HeadlessApplicationTest {

    private static String AGENT_PATH = HeadlessApplication.class.getResource("/engines/VirutorChess_1.1.1/bin/VirutorChessUci_1.1.1.exe").getPath();

    private static UciEngine getUciEngine() {
        UciEngine ret = new UciEngine();
        ret.setName("Virutor Chess 1.1.1");
        ret.setPath(AGENT_PATH);
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
        Assert.assertEquals(0, actualTimeControl.getBase());
        Assert.assertEquals(15, actualTimeControl.getIncrement());
        Assert.assertEquals(2, tournamentData.getUciEngines().size());
        assertUciEngine(getUciEngine(), tournamentData.getUciEngines().get(0));
        assertUciEngine(getUciEngine(), tournamentData.getUciEngines().get(1));
    }

    @Test
    public void runHeadlessApplicationTest() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Exception[] exceptions = new Exception[1];

        TournamentEndListener listener = new TournamentEndListener() {
            @Override
            public void onTournamentEnd(List<TournamentGame> tournamentGames, String resultFileName) {
                //TODO move to java 8 -
                // a) resource try
                // b) lambdas read lines

                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(resultFileName)));
                    String line;
                    System.out.println("Saved to file " + resultFileName);
                    System.out.println("Contents");
                    while ((line = bufferedReader.readLine())!=null) {
                        System.out.println(line);
                    }
                    bufferedReader.close();
                    File file = new File(resultFileName);
                    file.delete();
                } catch (Exception e) {
                    exceptions[0] = e;
                } finally {
                    countDownLatch.countDown();
                }
            }
        };

        TournamentData tournamentData = HeadlessApplication.parseTournamentData(("e " + AGENT_PATH + " rc 1").split(" "));
        new HeadlessApplication().startTournament(tournamentData, listener);
        countDownLatch.await();

        //TODO how to do it more elegantly?
        if(exceptions[0] != null) {
            throw exceptions[0];
        }
    }

}
