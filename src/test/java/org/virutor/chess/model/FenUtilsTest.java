package org.virutor.chess.model;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.virutor.chess.standard.FenUtils;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class FenUtilsTest {

    private String fenString;

    public FenUtilsTest(String fenString) {
        this.fenString = fenString;
    }

    @Parameters
    public static Collection<String[]> getData() {

        return Arrays.asList(new String[][]{new String[]{"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"}, new String[]{"r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1"}, new String[]{"8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1"}, new String[]{"r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"}, new String[]{"8/7p/5k2/5p2/p1p2P2/Pr1pPK2/1P1R3P/8 b - -"}});
    }

    @Test
    public void positionToFen() {

        Position position = new Position();
        FenUtils.setFen(fenString, position);
        String fenBack = FenUtils.positionToFen(position);
        //little hack
        String fenToCheck = fenString;
        if (fenToCheck.split(" ").length == 4) {
            fenToCheck += " 0 1";
        }
        Assert.assertEquals(fenToCheck, fenBack);

    }

}
