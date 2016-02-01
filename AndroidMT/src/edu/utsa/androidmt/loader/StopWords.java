package edu.utsa.androidmt.loader;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class StopWords {
	
	public static String[] stopwords = {"a", "as", "about", "am", "an", "and", "another", "any",
	    "are", "as", "at", "be", "been", "being", "but", "by", "co", "com", "did", "didnt", "do", 
	    "does", "doesnt", "doing", "dont", "done", "for", "in", "is", "isnt", "it","of", 
	    "on", "or", "the", "that", "this", "those", "to", "were", "what", "which", "with"};
	public static Set<String> stopWordSet = new HashSet<String>(Arrays.asList(stopwords));
	
	public static boolean isStopword(String word) {
		if(word.length() < 2 || !containsLetter(word)) return true;
		if(word.startsWith("&") || word.startsWith("%") || word.startsWith("$")) return true;
		if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
		if(stopWordSet.contains(word.toLowerCase())) return true;
		else return false;
	}
	public static boolean containsLetter(String word){
	    for(char c : word.toCharArray()){
		if(Character.isLetter(c)){
		    return true;
		}
	    }
	    return false;
	}
}