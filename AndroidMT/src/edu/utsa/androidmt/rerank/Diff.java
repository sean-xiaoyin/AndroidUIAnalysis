package edu.utsa.androidmt.rerank;

public class Diff {
    private String id;
    private String ori;
    private String oldTrans;
    private String newTrans;
    private String refTrans;
    public Diff(String id, String ori, String old, String newTrans, String refTrans){
	this.id = id;
	this.ori = ori;
	this.oldTrans = old;
	this.newTrans = newTrans;
	this.refTrans = refTrans;
    }
    public String toString(){
	return "------------------------\n"
		+ "String_ID:" + this.id + "\n"
		+ "English:" + this.ori + "\n"
		+ "Old:" + this.oldTrans + "\n"
		+ "New:" + this.newTrans + "\n"
		+ "Reference:" + this.refTrans + "\n";
    }
}
