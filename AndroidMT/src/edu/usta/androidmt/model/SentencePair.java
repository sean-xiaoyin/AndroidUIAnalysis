package edu.usta.androidmt.model;

import java.util.HashSet;
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
	return getPhrases(this.from);
    }
    public Set<String> getToPhrases() {
	return getPhrases(this.to);
    }
    public static Set<String> getPhrases(String sentence) {
	HashSet<String> phrases = new HashSet<String>();
	StringTokenizer st = new StringTokenizer(sentence);
	while(st.hasMoreTokens()){
	    phrases.add(st.nextToken());
	}
	return phrases;
    }
}
