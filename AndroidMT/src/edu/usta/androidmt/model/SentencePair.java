package edu.usta.androidmt.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class SentencePair {
    private String from;
    private String to;
    private String id;

    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getId() {
        return id;
    }
    public SentencePair(String from, String to, String id){
	this.from = from;
	this.to = to;
	this.id = id;
    }
    public Set<String> getFromPhrases() {
	return getPhrases(this.from, 4);
    }
    public Set<String> getToPhrases() {
	return getPhrases(this.to, 4);
    }
    public static Set<String> getPhrases(String sentence, int gramSize) {
	List<String> phraseList = new ArrayList<String>();
	StringTokenizer st = new StringTokenizer(sentence);
	while(st.hasMoreTokens()){
	    phraseList.add(st.nextToken());
	}
	HashSet<String> phrases = new HashSet<String>();
	for(int i = 0; i < phraseList.size(); i++){
	    String phrase = "";
	    for(int j = i; j >= 0 && j > i - gramSize; j--){
		phrase = phraseList.get(j) + " " + phrase;
		phrases.add(phrase.trim());
	    }
	}
	return phrases;
    }
}
