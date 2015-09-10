package org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.Interaction;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.PubMed;
import org.baderlab.csapps.socialnetwork.model.academia.Publication;
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
     * Confirm that the XML parser parses all the tag contents
     */
   public void testHitCount() throws Exception {
        String path = getClass().getResource("pubmed_hit_count.xml").getFile();
        File xmlFile = new File(path);
        SocialNetwork socialNetwork = new SocialNetwork("test", Category.PUBMED);
        SocialNetworkAppManager appmanager = new SocialNetworkAppManager();
        appmanager.setNetworkFile(xmlFile);
        PubMedXmlParserTask xmlParser = new PubMedXmlParserTask(appmanager, socialNetwork);
        xmlParser.run(taskMonitor);
        ArrayList<Publication> pubList = socialNetwork.getPublications();
        //PubMed xmlParser = new PubMed(xmlFile, taskMonitor);
        assertTrue(pubList.size() == 33);
    }
    
    /**
     * Confirm that the calculated times cited value for the main author is correct
     */
    // TODO: Temporarily disabled test for times cited value
    /*
    @Test
    public void testTimesCited() {
        boolean status = false;
        String path = getClass().getResource("pubmed_times_cited.xml").getFile();
        File xmlFile = new File(path);
        PubMed pubmed = new PubMed(xmlFile, taskMonitor);
        Interaction interaction = new Interaction(pubmed.getPubList(), Category.ACADEMIA, 500);
        Set<Collaboration> collaboratorSet = interaction.getAbstractMap().keySet();
        Collaboration[] collabArray = new Collaboration[collaboratorSet.size()];
        collaboratorSet.toArray(collabArray);
        Author author = CytoscapeUtilities.getAuthor("A Person", collabArray); 
        if (author != null) {
            status = author.getTimesCited() == 706;
        }
        assertTrue(status);
    }
    */

}
