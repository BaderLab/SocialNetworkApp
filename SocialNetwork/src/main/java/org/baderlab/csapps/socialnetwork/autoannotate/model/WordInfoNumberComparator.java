package org.baderlab.csapps.socialnetwork.autoannotate.model;

import java.util.Comparator;

// Class used to sort the WordInfos by number to preserve word order
public class WordInfoNumberComparator implements Comparator<WordInfo> {

    public static WordInfoNumberComparator getInstance() {
        if (instance == null) {
            instance = new WordInfoNumberComparator();
        }
        return instance;
    }

    private static WordInfoNumberComparator instance = null;

    public int compare(WordInfo arg0, WordInfo arg1) {
        return (int) Math.signum(arg0.getNumber() - arg1.getNumber());
    }
}
