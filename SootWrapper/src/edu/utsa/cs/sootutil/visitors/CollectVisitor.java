package edu.utsa.cs.sootutil.visitors;

import java.util.ArrayList;
import java.util.List;

import soot.jimple.Expr;

public class CollectVisitor extends SootVisitor{
	protected List<Expr> collected;
	public CollectVisitor(){
		this.collected = new ArrayList<Expr>();
	}
	
	public List<Expr> getCollected(){
		return this.collected;
	}

	public int getCollectionSize() {
		return this.collected.size();
	}
}
