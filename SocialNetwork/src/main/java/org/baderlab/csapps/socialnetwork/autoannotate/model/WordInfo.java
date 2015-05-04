package org.baderlab.csapps.socialnetwork.autoannotate.model;

public class WordInfo implements Comparable<WordInfo>{

    private String word;
    private double size;
    private int cluster;
    private int number;

    public WordInfo(String word, double size, int cluster, int number) {
        this.word = word;
        this.size = size;
        this.cluster = cluster;
        this.number = number;
    }

    @Override
    public WordInfo clone() {
        WordInfo w = new WordInfo(this.word, this.size, this.cluster, this.number);
        return w;
    }

    public int compareTo(WordInfo otherWordInfo) {
        // Sorts descending by size
        return (int) Math.signum(otherWordInfo.getSize() - this.size);
    }

    @Override
    public boolean equals(Object other) {
        return (other.getClass() == WordInfo.class
                && this.word.equals(((WordInfo) other).getWord())
                && this.size == ((WordInfo) other).getSize()
                && this.cluster == ((WordInfo) other).getCluster()
                && this.number == ((WordInfo) other).getNumber());
    }

    public int getCluster() {
        return this.cluster;
    }

    public int getNumber() {
        return this.number;
    }

    public double getSize() {
        return this.size;
    }

    public String getWord() {
        return this.word;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSize(double d) {
        this.size = d;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
