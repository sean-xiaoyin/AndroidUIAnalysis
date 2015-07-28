package edu.utsa.androidmt.rerank;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import edu.usta.androidmt.model.PhrasePair;
import edu.usta.androidmt.model.PropModel;
import edu.usta.androidmt.model.SentencePair;

public class PropUpdater {
    private PropModel model;
    private String phraseTablePath;
    private Hashtable<PhrasePair, Integer> phraseLineTable;
    private List<String> phraseLines;
    
    public PropUpdater(PropModel model, String phraseTablePath, Hashtable<PhrasePair, Integer> phraseLineTable, List<String> phraseLines){
	this.model = model;
	this.phraseTablePath = phraseTablePath;
	this.phraseLineTable = phraseLineTable;
	this.phraseLines = phraseLines;
    }
    public void updateProp(List<String> sentences, Hashtable<String, List<String>> contexts) throws IOException{
	for(String sentence : sentences){
	    Set<String> phrases = SentencePair.getPhrases(sentence);
	    for(String phrase : phrases){
		List<PhrasePair> pairs = this.model.getPairs(phrase);
		for (PhrasePair pp : pairs){
		    updatePhraseFile(this.phraseLineTable.get(pp), this.model.getContextsProp(pp, contexts.get(sentence)));
		}
	    }
	}
	PrintWriter pw = new PrintWriter(new FileWriter(this.phraseTablePath));
	for(String line : this.phraseLines){
	    pw.println(line);
	}
	pw.close();
    }
    private void updatePhraseFile(int lineNum, double contextsProp) {
	String line = this.phraseLines.get(lineNum);
	int index1 = line.indexOf("|||" + 3, line.indexOf("|||") + 3);
	int index2 = line.indexOf(" ", index1);
	String updated = line.substring(0, index1) + contextsProp + line.substring(index2);
	this.phraseLines.set(lineNum, updated);
    }
}
