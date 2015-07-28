package edu.usta.androidmt.model;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import edu.utsa.androidmt.loader.DataLoader;

public class PropModel implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Hashtable<String, List<PhrasePair>> targetTable;
    private Hashtable<String, Integer> contextWordPropTable;
    private int sentenceSum;

    private PropModel(Hashtable<String, List<PhrasePair>> targetTable, Hashtable<String, Integer> contextWordPropTable, int sentenceSum){
	this.targetTable = targetTable;
	this.setContextWordPropTable(contextWordPropTable);
	this.sentenceSum = sentenceSum;
    }
    public static PropModel extractModel(DataLoader loader){	
	Hashtable<String, Integer> contextWordPropTable = new Hashtable<String, Integer>();
	for(String id : loader.getIdSentenceTable().keySet()){
	    SentencePair sp = loader.getIdSentenceTable().get(id);
	    List<String> contextWords = loader.getIdContextTable().get(id);
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
		for(PhrasePair pair : pairs){
		    if(toPhrases.contains(pair.getTo())){
			pair.addCount();
			for(String word : contextWords){
			    pair.addContext(word);
			}
		    }
		}
	    }
	}
	return new PropModel(loader.getPhraseTable(), contextWordPropTable, loader.getIdSentenceTable().keySet().size());
    }
    
    public PhrasePair getCategory(String from, List<String> context){
	List<PhrasePair> pairs = this.targetTable.get(from);
	PhrasePair cat = null; 
	double maxProp = 0.0;
	for(PhrasePair pair : pairs){
	    double prop = getContextsProp(pair, context);
	    if(prop > maxProp){
		cat = pair;
		maxProp = prop;
	    }
	}
	return cat;
    }
    public double getContextsProp(PhrasePair pair, List<String> context){
	double prop = pair.getBasicValue();
	for(String word : context){
	    prop = prop * pair.getContextValue(word, 1.0*this.contextWordPropTable.get(word)/this.sentenceSum);
	}
	return prop;
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
}
