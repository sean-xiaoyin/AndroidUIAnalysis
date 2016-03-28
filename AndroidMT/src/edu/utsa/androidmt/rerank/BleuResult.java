package edu.utsa.androidmt.rerank;

import java.util.ArrayList;
import java.util.List;

public class BleuResult implements Comparable<BleuResult>{
    private ResultItem oldres;
    private ResultItem newres;
    private String name;
    private double gain;
    private List<Diff> diffs;
    public List<Diff> getDiffs(){
	return this.diffs;
    }
    public BleuResult(String name, String oldline, String newline){
	this.name = name;
	this.oldres = new ResultItem(oldline);
	this.newres = new ResultItem(newline);
	this.gain = this.newres.getValue() - this.oldres.getValue();
	this.diffs = new ArrayList<Diff>();
    }
    public ResultItem getOldres() {
	return oldres;
    }
    public ResultItem getNewres() {
	return newres;
    }
    public String toString(){
	String ret = "-------------------------------------------------------\n";
	ret = ret + name + "\n";
	ret = ret + "New:" + "\n";
	ret = ret + newres.getLine() + "\n";
	ret = ret + "Old:" + "\n";
	ret = ret + oldres.getLine();
	return ret;
    }
    private class ResultItem{
	private String line;
	private double value;
	public ResultItem(String line){
	    this.line = line;
	    if(this.line.startsWith("\n")){
		this.line = line.substring(1);
	    }
	    if(this.line.startsWith("BLEU = ")){
		String value = line.substring(7, line.indexOf(','));
		this.value = Double.parseDouble(value);
	    }else{
		this.value = 0;
	    }
	}
	public String getLine(){
	    return this.line;
	}
	public double getValue(){
	    return this.value;
	}
    }
    @Override
    public int compareTo(BleuResult arg0) {
	return Double.compare(this.gain, arg0.gain);
    }
    public String getName() {
	return name;
    }
    
}
