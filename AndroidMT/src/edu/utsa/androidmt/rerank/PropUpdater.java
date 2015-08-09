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
    public String updateProp(List<String> sentences, Hashtable<String, List<String>> contexts) throws IOException, InterruptedException{
	for(String sentence : sentences){
	    Set<String> phrases = SentencePair.getPhrases(sentence, 4);
	    for(String phrase : phrases){
		List<PhrasePair> pairs = this.model.getPairs(phrase);
		if(pairs!=null){
		    for (PhrasePair pp : pairs){
			if(this.phraseLineTable.get(pp)!=null){
			    updatePhraseFile(this.phraseLineTable.get(pp), this.model.getContextsProp(pp, contexts.get(sentence)));
			}
		    }
		}
	    }
	}
	String newPhraseTablePath = this.phraseTablePath.substring(0, this.phraseTablePath.lastIndexOf('/')) + "/phrase-table.0-0.1.1";
	PrintWriter pw = new PrintWriter(new FileWriter(newPhraseTablePath));
	for(String line : this.phraseLines){
	    pw.println(line);
	}
	pw.close();
	CommandRunner.runCommand("rm -rf " + newPhraseTablePath + ".gz", true);
	CommandRunner.runCommand("gzip " + newPhraseTablePath, true);
	return newPhraseTablePath;
    }
    private void updatePhraseFile(int lineNum, double contextsProp) {
	String line = this.phraseLines.get(lineNum);
	int index1 = line.indexOf("|||", line.indexOf("|||") + 3) + 3;
	int index2 = line.indexOf("|||", index1);
	String scorePart = line.substring(index1, index2);
	String[] scores = scorePart.trim().split(" ");
	scores[2] = contextsProp + "";
	String newScorePart = " " + scores[0] + " " + scores[1] + " " + scores[2] + " " + scores[3] + " "; 
	String updated = line.substring(0, index1) + newScorePart + line.substring(index2);
	this.phraseLines.set(lineNum, updated);
    }
    public String getPhraseTablePath() {
	return phraseTablePath;
    }
    public void setPhraseTablePath(String phraseTablePath) {
	this.phraseTablePath = phraseTablePath;
    }
}
