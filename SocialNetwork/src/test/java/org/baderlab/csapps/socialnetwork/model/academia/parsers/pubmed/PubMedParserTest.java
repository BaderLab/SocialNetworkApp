package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.util.ArrayList;
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
import org.baderlab.csapps.socialnetwork.model.academia.Query;
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
     * Confirm that the SAX parser parses all the contents in a tag, 
     * and does not truncate anything.
     */
    public void testSAXParserTruncation() {
        String path = getClass().getResource("bachman_e.xml").getFile();
        File xmlFile = new File(path);
        PubMedXmlParser xmlParser = new PubMedXmlParser(xmlFile, taskMonitor);
        ArrayList<Publication> pubList = xmlParser.getPubList();
        Query query = new Query(PubMed.getEutilsPMIDs(pubList));
        EutilsSearchParser eUtilsSearchParser = new EutilsSearchParser(query);
        int retStart = eUtilsSearchParser.getRetStart();
        int retMax = eUtilsSearchParser.getRetMax();
        String queryKey = eUtilsSearchParser.getQueryKey();
        String webEnv = eUtilsSearchParser.getWebEnv();
        EutilsTimesCitedParser eUtilsTimesCitedParser = new EutilsTimesCitedParser(pubList, queryKey, webEnv, retStart, retMax);
        assertTrue(eUtilsTimesCitedParser.getNumSaxConflicts() == 0);
    }

}
