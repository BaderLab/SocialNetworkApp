package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.io.File;
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test PubMed data parsing feature
 * 
 * @author Victor Kofia
 */
public class PubMedParserTest {

    private TaskMonitor taskMonitor = mock(TaskMonitor.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    /**
     * Confirm that the XML parser parses all the contents
     */
    public void testHitCount() {
        String path = getClass().getResource("bachman_e.xml").getFile();
        File xmlFile = new File(path);
        PubMed xmlParser = new PubMed(xmlFile, taskMonitor);
        assertTrue(xmlParser.getTotalHits() == 33);
    }

}
