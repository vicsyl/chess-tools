package org.virutor.chess.headless;

import com.virutor.chess.tournament.TournamentData;
import com.virutor.chess.tournament.TournamentEndListener;
import com.virutor.chess.tournament.TournamentManager;
import com.virutor.chess.ui.config.UciEngine;
import org.apache.log4j.xml.DOMConfigurator;
import org.virutor.chess.standard.time.IncrementalTimeControl;
import org.virutor.chess.standard.time.TimeControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TODO time control and other options
 */
public class HeadlessApplication {

    private static void printUsageAndExit() {

        System.out.println("Usage: ");
        System.out.println(HeadlessApplication.class.getName() + "  e <engine_path> rc <round_count> [<options>]");
        System.out.println(" Options: ");
        System.out.println(" e <other_engine_path>");
        System.exit(-1);
    }

    private static void tryMatchUciEngine(List<String> args, List<UciEngine> uciEngines) {

        if(args.isEmpty() || !"e".equals(args.get(0))) {
            return;
        }

        args.remove(0);
        if(args.isEmpty()) {
            System.out.println("No path found fot engine");
            printUsageAndExit();
        }
        String path  = args.remove(0);
        UciEngine uciEngine = new UciEngine();
        uciEngine.setName(path);
        uciEngine.setPath(path);
        uciEngines.add(uciEngine);

    }

    private static int tryMatchRoundCount(List<String> args) {

        if (args.isEmpty() || !"rc".equals(args.get(0))) {
            return -1;
        }

        args.remove(0);
        if (args.isEmpty()) {
            System.out.println("No value found round count");
            printUsageAndExit();
        }
        String rc = args.remove(0);
        try {
            return Integer.parseInt(rc);
        } catch (NumberFormatException e) {
            System.out.println("Cannot parse round count from value " + rc);
            printUsageAndExit();
            return -1;
        }
    }

    static TournamentData parseTournamentData(String[] args) {

        if(args.length < 4) {
            printUsageAndExit();
        }

        List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        List<UciEngine> uciEngines = new ArrayList<UciEngine>();
        int roundCount = -1;

        while(!argsList.isEmpty()) {
            int argsLength = argsList.size();
            tryMatchUciEngine(argsList, uciEngines);
            roundCount = tryMatchRoundCount(argsList);
            if(argsLength == argsList.size()) {
                System.out.println("Unknown parameter: " + argsList.get(0));
            }
        }

        if(uciEngines.isEmpty()) {
            System.out.println("Uci engine not defined");
            printUsageAndExit();
        }
        if(roundCount == -1) {
            System.out.println("Round count not defined");
            printUsageAndExit();
        }

        //if we have a single uci engine, just create a clone and let it play against itself
        if(uciEngines.size() == 1) {
            UciEngine uciEngineClone = new UciEngine();
            uciEngineClone.setName(uciEngines.get(0).getName());
            uciEngineClone.setPath(uciEngines.get(0).getPath());
            uciEngines.add(uciEngineClone);
        }

        return TournamentData.createBuilder()
                .setRoundCount(roundCount)
                .setTimeControls(Collections.<TimeControl>singletonList(new IncrementalTimeControl(15, 0)))
                .setUciEngines(uciEngines).build();

    }

    public static void main(String[] args) {
        TournamentData tournamentData = parseTournamentData(args);
        HeadlessApplication headlessApplication = new HeadlessApplication();
        headlessApplication.startTournament(tournamentData, null);
    }

    public void startTournament(TournamentData tournamentData, TournamentEndListener tournamentEndListener) {

        DOMConfigurator.configure(HeadlessApplication.class.getResource("/headless/headless-log4j.xml").getPath());

        TournamentManager manager = new TournamentManager(tournamentData, tournamentEndListener);
        manager.start();

    }

}
