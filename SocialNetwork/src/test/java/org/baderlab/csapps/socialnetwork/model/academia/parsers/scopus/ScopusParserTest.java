package org.baderlab.csapps.socialnetwork.model.academia.parsers.scopus;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.io.File;
import java.util.Set;
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
     * Return the author with the specified label in the collaboration array
     * 
     * @param String label
     * @param String[] authorArray
     * 
     * @return Author author
     */
    private Author getAuthor(String label, Collaboration[] collabArray) {
        Collaboration collab = null;
        Author author1 = null, author2 = null;
        for (int i = 0; i < collabArray.length; i++) {
            collab = collabArray[i];
            author1 = (Author) collab.getNode1();
            author2 = (Author) collab.getNode2();
            if (author1.getLabel().equals(label)) {
                return author1;
            }
            if (author2.getLabel().equals(label)) {
                return author2;
            }
        }
        return null;
    }

    /**
     * Confirm that the XML parser parses all the contents
     */
    @Test
    public void testTimesCited() {
        boolean status = false;
        String path = getClass().getResource("example_scopus_file.csv").getFile();
        File csvFile = new File(path);
        Scopus scopus = new Scopus(csvFile, taskMonitor);
        Interaction interaction = new Interaction(scopus.getPubList(), Category.ACADEMIA, 500);
        Set<Collaboration> collaboratorSet = interaction.getAbstractMap().keySet();
        Collaboration[] collabArray = new Collaboration[collaboratorSet.size()];
        collaboratorSet.toArray(collabArray);
        Author author = getAuthor("S Lotia", collabArray); 
        if (author != null) {
            status = author.getTimesCited() == 992;
        }
        assertTrue(status);
    }

}
