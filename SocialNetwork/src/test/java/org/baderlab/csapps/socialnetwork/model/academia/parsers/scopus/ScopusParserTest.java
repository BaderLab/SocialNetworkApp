package org.baderlab.csapps.socialnetwork.model.academia.parsers.scopus;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.baderlab.csapps.socialnetwork.CytoscapeUtilities;
import org.baderlab.csapps.socialnetwork.model.AbstractEdge;
import org.baderlab.csapps.socialnetwork.model.Category;
import org.baderlab.csapps.socialnetwork.model.Collaboration;
import org.baderlab.csapps.socialnetwork.model.SocialNetwork;
import org.baderlab.csapps.socialnetwork.model.SocialNetworkAppManager;
import org.baderlab.csapps.socialnetwork.model.academia.Author;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.ParseScopus;
import org.baderlab.csapps.socialnetwork.tasks.CreatePublicationNetworkFromPublications;
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
        String path = getClass().getResource("scopus.csv").getFile();
        path = URLDecoder.decode(path,"UTF-8");
        SocialNetwork socialNetwork = new SocialNetwork("test",Category.SCOPUS);
        SocialNetworkAppManager appManager = new SocialNetworkAppManager();
        File csvFile = new File(path);
        ParseScopus scopus = new ParseScopus(csvFile, socialNetwork, appManager);
        scopus.run(taskMonitor);;
        
        CreatePublicationNetworkFromPublications createnetwork = new CreatePublicationNetworkFromPublications(appManager,socialNetwork,"test");
        createnetwork.run(taskMonitor);       	
  
        Map<Collaboration, ArrayList<AbstractEdge>> map = appManager.getMap();

        Set<Collaboration> collaboratorSet = map.keySet();
        Collaboration[] collabArray = new Collaboration[collaboratorSet.size()];
        collaboratorSet.toArray(collabArray);
        Author author = CytoscapeUtilities.getAuthor("A Person", collabArray); 
        if (author != null) {
            status = author.getTimesCited() == 5981;
        }
        assertTrue(status);
    }

}
