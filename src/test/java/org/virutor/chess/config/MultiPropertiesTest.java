package org.virutor.chess.config;

import junit.framework.Assert;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MultiPropertiesTest {

    private static URL FILE_URL = MultiPropertiesTest.class.getResource("/config/EnginesPlayers.txt");

    //FIXME both test and implementation are horrible ;-(
    //@Test
    public void readSaveReadTest() throws Exception {

        MultiProperties multiProperties = new MultiProperties(FILE_URL.getFile());

        Map<String, List<Map<String, String>>> ret = multiProperties.load();

        String parent = new File(FILE_URL.getFile()).getParent();
        String out = new File(new File(parent), "out.txt").getPath();

        MultiProperties.save(out, ret);

        multiProperties = new MultiProperties(out);
        Map<String, List<Map<String, String>>> ret2 = multiProperties.load();

        Assert.assertEquals(ret, ret2);
    }

}
