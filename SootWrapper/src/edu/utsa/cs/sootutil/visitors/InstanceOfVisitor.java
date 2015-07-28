package edu.utsa.cs.sootutil.visitors;

import soot.jimple.InstanceOfExpr;

public class InstanceOfVisitor extends CollectVisitor{
		
	@Override
	public boolean beforeInstanceOfExpr(InstanceOfExpr arg0) {
		this.collected.add(arg0);
		return true;
	}

}
