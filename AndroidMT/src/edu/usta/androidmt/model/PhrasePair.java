package edu.usta.androidmt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PhrasePair implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String from;
    private String to;
    private int count;
    double basicValue;
    private Hashtable<String, Integer> contexts;
    private List<List<String>> contextlist;

    public PhrasePair(String from, String to, double basicValue){
	this.count = 0;
	this.contexts = new Hashtable<String, Integer>();
	this.from = from;
	this.to = to;
	this.basicValue = basicValue;
	this.setContextlist(new ArrayList<List<String>>());
    }
    public String toString(){
	return "{" + this.from + "=>" + this.to + "}";
    }
    public double getBasicValue() {
        return basicValue;
    }
    public int hashCode(){
	return (this.from + "||" + this.to).hashCode();
    }
    public boolean equals(Object obj){
	if(obj instanceof PhrasePair){
	    PhrasePair pp = (PhrasePair)obj;
	    return pp.from.equals(this.from) && pp.to.equals(this.to);
	}
	return false;
    }
    public void addCount(){
	this.count = this.count + 1;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public int getCount() {
        return count;
    }
    public void addContextList(List<String> context){
	this.getContextlist().add(context);
    }
    public void addContext(String word){
	Integer x = this.contexts.get(word);
	if(x == null){
	    this.contexts.put(word, 1);
	}else{
	    this.contexts.put(word, x + 1);
	}
    }
    public Hashtable<String, Integer> getContexts(){
	return this.contexts;
    }
    public double getContextValue(String word, double wordprop){
	if(this.contexts.get(word) == null){
	    return 1.0;
	}
	double condprop = (1.0 * this.contexts.get(word) + 1) / (this.getCount() + (1.0/wordprop));
	return condprop / wordprop ;
    }
    public List<List<String>> getContextlist() {
	return contextlist;
    }
    public void setContextlist(List<List<String>> contextlist) {
	this.contextlist = contextlist;
    }
}
