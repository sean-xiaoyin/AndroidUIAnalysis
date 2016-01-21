package edu.usta.androidmt.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import edu.utsa.androidmt.loader.DataLoader;
import edu.utsa.androidmt.loader.Stemmer;
import edu.utsa.androidmt.loader.StopWords;

public class PropModel implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Hashtable<String, List<PhrasePair>> targetTable; // 
    private Hashtable<String, Integer> contextWordPropTable; // a table whose key is a word in the context, and the value is the number of times the word appears
    private Hashtable<String, Set<PhrasePair>> topPhrasePairTable;
    private int sentenceSum;

    private PropModel(Hashtable<String, List<PhrasePair>> targetTable, Hashtable<String, Integer> contextWordPropTable, 
	    Hashtable<String, Set<PhrasePair>> topPhrasePairTable, int sentenceSum){
	this.targetTable = targetTable;
	this.setContextWordPropTable(contextWordPropTable);
	this.sentenceSum = sentenceSum;
	this.setTopPhrasePairTable(topPhrasePairTable);
    }
    public static PropModel extractModel(DataLoader loader){	
	Hashtable<String, Integer> contextWordPropTable = new Hashtable<String, Integer>();
	Hashtable<String, Set<PhrasePair>> topPhrasePairTable = new Hashtable<String, Set<PhrasePair>>();
	for(String id : loader.getIdSentenceTable().keySet()){
	    SentencePair sp = loader.getIdSentenceTable().get(id);
	    List<String> contextWords = loader.getIdContextTable().get(id);
	    if(contextWords == null){
		contextWords = new ArrayList<String>();
	    }
	    for(String word : contextWords){
		Integer i = contextWordPropTable.get(word);
		if(i == null){
		    contextWordPropTable.put(word, 1);
		}else{
		    contextWordPropTable.put(word, i + 1);
		}
	    }
	    Set<String> fromPhrases = sp.getFromPhrases();
	    Set<String> toPhrases = sp.getToPhrases();
	    for(String from : fromPhrases){
		List<PhrasePair> pairs = loader.getPhraseTable().get(from);
//		if(from.equals("drain")){
//		    System.out.println();
//		}
		if(pairs!=null){
		    PhrasePair ppMax = null;
		    double maxProb = 0.0;
		    for(PhrasePair pair : pairs){
			if(toPhrases.contains(pair.getTo())){
			    if(ppMax == null || pair.getBasicValue() > maxProb){
				ppMax = pair;
				maxProb = pair.getBasicValue();
			    }			    
			    pair.addCount();
			    if(contextWords.size()>0){
				pair.addContextList(contextWords);
			    }
			    for(String word : contextWords){
				pair.addContext(word);
			    }
			    
			    for(String phrase : fromPhrases){
				if(phrase.trim().split(" ").length == 1 && from.indexOf(phrase)==-1){
				    if(!StopWords.isStopword(phrase)){
					pair.addContext(Stemmer.stemWord(phrase.trim().toLowerCase()));
				    }
				}
			    }
			}
		    }
		    Set<PhrasePair> topPPs = topPhrasePairTable.get(from);
		    if(topPPs == null){
			topPPs = new HashSet<PhrasePair>();
			topPhrasePairTable.put(from, topPPs);
		    }
		    if(ppMax!=null){
			topPPs.add(ppMax);
		    }
		}
	    }
	}
	return new PropModel(loader.getPhraseTable(), contextWordPropTable, topPhrasePairTable, loader.getIdSentenceTable().keySet().size());
    }
    
    public PhrasePair getCategory(String from, List<String> context){
	List<PhrasePair> pairs = this.targetTable.get(from);
	PhrasePair cat = null; 
	double maxProp = 0.0;
	for(PhrasePair pair : pairs){
	    double prop = getContextsProb(pair, context);
	    if(prop > maxProp){
		cat = pair;
		maxProp = prop;
	    }
	}
	return cat;
    }
    public double getContextsProb(PhrasePair pair, List<String> context){
	double prob = pair.getBasicValue();
	for(String word : context){
	    if(this.contextWordPropTable.get(word)!=null){
		prob = prob * pair.getContextValue(word, 1.0*this.contextWordPropTable.get(word)/this.sentenceSum);
	    }
	}
	return prob;
    }
    
    public void reportTrainingData(String path) throws IOException{
	HashSet<PhrasePair> pairs = new HashSet<PhrasePair>();	
	for(String key : this.targetTable.keySet()){
	    pairs.addAll(this.targetTable.get(key));
	}
	
	PrintWriter pw = new PrintWriter(new FileWriter(path));
	for(PhrasePair ip : pairs){
	    for(List<String> context : ip.getContextlist()){
		String features = "";
		for(String str : context){
		    features = features + str + "|||";
		}
		if(features.endsWith("|||")){
		    features = features.substring(0, features.length() - 3);
		}
		pw.println(ip.toString() + "|||" + features);
	    }
	}
	pw.close();
    }
    
    public void reportInterestingPhrases(String path) throws IOException{
	List<InterPhrase> phrases = new ArrayList<InterPhrase>();
	for(String key : this.topPhrasePairTable.keySet()){
	    Set<PhrasePair> pps = this.topPhrasePairTable.get(key);
	    if(pps.size() > 1){
		Set<PhrasePair> primePPs = new HashSet<PhrasePair>();
		for(PhrasePair pp : pps){
		    boolean flag = true;
		    for(PhrasePair pp1 : pps){
			if(pp1!=pp && pp1.getTo().contains(pp.getTo())){
			    flag = false;
			}
		    }
		    if(flag && !StopWords.isStopword(pp.getFrom())) primePPs.add(pp);
		}
		double inter = 1.0;
		for(PhrasePair pp : primePPs){
		    for(PhrasePair pp1 : primePPs){
			inter = inter*simi(pp, pp1)*wordSimi(pp.getFrom(), pp1.getFrom());
		    }
		}
		if(primePPs.size() > 1){
		    phrases.add(new InterPhrase(key, 1.0 - inter, primePPs));
		}
	    }
	}
	Collections.sort(phrases);
	PrintWriter pw = new PrintWriter(new FileWriter(path));
	for(InterPhrase ip : phrases){
	    pw.println(ip.toString());
	}
	pw.close();
    }

    
    private double wordSimi(String str1, String str2) {
	Set<Character> match = new HashSet<Character>();
	Set<Character> chars1 = new HashSet<Character>();
	Set<Character> chars2 = new HashSet<Character>();
	for(char c: str2.toCharArray()){
	    chars2.add(c);
	}
	for(char c : str1.toCharArray()){
	    chars1.add(c);
	    for(char c1 : str2.toCharArray()){
		if(c1==c){
		    match.add(c);
		}
	    }
	}
	return match.size()*1.0 / Math.sqrt(chars1.size() * chars2.size());
    }
    private double simi(PhrasePair pp, PhrasePair pp1) {
	int matches = 0;
	for(String word : pp.getContexts().keySet()){
	    for(String word1 : pp1.getContexts().keySet()){
		if(word.equals(word1)){
		    matches = matches + 1;
		}
	    }
	}
	if(pp.getContexts().size() == 0 || pp1.getContexts().size() == 0){
	    return 1.0;
	}
	return matches*1.0 / Math.sqrt(pp.getContexts().size() * pp1.getContexts().size());
    }
    public List<PhrasePair> getPairs(String phrase) {
	return this.targetTable.get(phrase);
    }
    public Hashtable<String, Integer> getContextWordPropTable() {
	return contextWordPropTable;
    }
    public void setContextWordPropTable(Hashtable<String, Integer> contextWordPropTable) {
	this.contextWordPropTable = contextWordPropTable;
    }
    public int getSentenceSum() {
	return sentenceSum;
    }
    public void setSentenceSum(int sentenceSum) {
	this.sentenceSum = sentenceSum;
    }
    public Hashtable<String, Set<PhrasePair>> getTopPhrasePairTable() {
	return topPhrasePairTable;
    }
    public void setTopPhrasePairTable(Hashtable<String, Set<PhrasePair>> topPhrasePairTable) {
	this.topPhrasePairTable = topPhrasePairTable;
    }    
}
