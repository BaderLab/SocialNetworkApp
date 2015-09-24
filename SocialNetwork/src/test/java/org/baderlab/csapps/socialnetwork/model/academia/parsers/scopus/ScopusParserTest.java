package org.baderlab.csapps.socialnetwork.model.academia.parsers.scopus;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
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

    /**
     * Confirm that the calculated times cited value for the main author is correct
     */
    @Test
    public void testTimesCited() throws Exception{
        boolean status = false;
        String path = getClass().getResource("scopus_times_cited.csv").getFile();
        path = URLDecoder.decode(path,"UTF-8");
        File csvFile = new File(path);
        Scopus scopus = new Scopus(csvFile, taskMonitor);
        Interaction interaction = new Interaction(scopus.getPubList(), Category.ACADEMIA, 500);
        Set<Collaboration> collaboratorSet = interaction.getAbstractMap().keySet();
        Collaboration[] collabArray = new Collaboration[collaboratorSet.size()];
        collaboratorSet.toArray(collabArray);
        Author author = CytoscapeUtilities.getAuthor("A Person", collabArray); 
        if (author != null) {
            status = author.getTimesCited() == 992;
        }
        assertTrue(status);
    }

}
