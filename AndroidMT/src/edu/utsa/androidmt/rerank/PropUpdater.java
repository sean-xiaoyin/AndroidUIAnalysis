package edu.utsa.androidmt.rerank;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import edu.usta.androidmt.model.PhrasePair;
import edu.usta.androidmt.model.PropModel;
import edu.usta.androidmt.model.SentencePair;
import edu.utsa.androidmt.loader.StopWords;

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
    public String updateProp(List<String> sentences, Hashtable<String, List<String>> contexts, boolean islast) throws IOException, InterruptedException{
	List<Integer> recoverList = new ArrayList<Integer>();
	List<String> recoverStrs = new ArrayList<String>();
	if(true){
	    for(String sentence : sentences){
		Set<String> phrases = SentencePair.getPhrases(sentence, 4);
		for(String phrase : phrases){
		    if(!StopWords.isStopword(phrase)){
			List<PhrasePair> pairs = this.model.getPairs(phrase);
/*			if(phrase.equals("alarm") || phrase.equals("faces") || phrase.equals("try")){
			    System.out.println();
			}*/
			if(pairs!=null){
			    for (PhrasePair pp : pairs){
				Integer lineNum = this.phraseLineTable.get(pp);
				if(lineNum!=null){
				    double rawProb = this.model.getContextsProb(pp, contexts.get(sentence));
				    if(rawProb!=pp.getBasicValue()){
					recoverList.add(lineNum);
					recoverStrs.add(this.phraseLines.get(lineNum));
					double amplifier = rawProb / pp.getBasicValue();
//					if(amplifier > 1.0){
//					    double logamp  = Math.log(amplifier - 1 + Math.E);
					    double newProbProd = amplifier * pp.getBasicValue();
					    double newProbsmooth = 1 - 1 / (1 + newProbProd);
//					    double newProb = newProbProd > newProbsmooth ? newProbsmooth : newProbProd;
//					    if(newProbsmooth > pp.getBasicValue()){
						updatePhraseFile(lineNum, newProbsmooth);
//					    }
//					}
				    }
				}
			    }
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
	System.out.println("recover phrase lines ...");
	recoverLines(recoverList, recoverStrs);
	System.out.println("prepare phrase table");
	CommandRunner.runCommand("rm -rf " + newPhraseTablePath + ".gz", true);
	CommandRunner.runCommand("gzip " + newPhraseTablePath, true);
	return newPhraseTablePath;
    }
    private void recoverLines(List<Integer> recoverList,
	    List<String> recoverStrs) {
	for(int i = 0; i < recoverList.size(); i++){
	    this.phraseLines.set(recoverList.get(i), recoverStrs.get(i));
	}
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
