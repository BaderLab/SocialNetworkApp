package org.baderlab.csapps.socialnetwork;

import org.baderlab.csapps.socialnetwork.model.academia.AuthorTest;
import org.baderlab.csapps.socialnetwork.model.academia.CollaborationTest;
import org.baderlab.csapps.socialnetwork.model.academia.InteractionTest;
import org.baderlab.csapps.socialnetwork.model.academia.SearchTest;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.incites.IncitesParserTest;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.pubmed.PubMedParserTest;
import org.baderlab.csapps.socialnetwork.model.academia.parsers.scopus.ScopusParserTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Run all the Social Network App tests at once (for devs)
 * 
 * @author Victor Kofia
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={AuthorTest.class, CollaborationTest.class, IncitesParserTest.class, InteractionTest.class, SearchTest.class,
        PubMedParserTest.class, ScopusParserTest.class})
public class AllTests {

  /**
   * Sets the environment
   */
  @BeforeClass public static void setUp() {}

}
