package org.baderlab.csapps.socialnetwork.model.academia.parsers.scopus;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.io.File;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.baderlab.csapps.socialnetwork.model.academia.Scopus;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test Scopus data parsing feature
 * 
 * @author Victor Kofia
 */
public class ScopusParserTest {

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
    public void testTimesCited() {
        String path = getClass().getResource("example_scopus_file.csv").getFile();
        File csvFile = new File(path);
        Scopus scopus = new Scopus(csvFile, taskMonitor);
        Interaction interaction = new Interaction(scopus.getPubList(), Category.ACADEMIA, 500);
        interaction.getAbstractMap();
        assertTrue(false);
    }

}
