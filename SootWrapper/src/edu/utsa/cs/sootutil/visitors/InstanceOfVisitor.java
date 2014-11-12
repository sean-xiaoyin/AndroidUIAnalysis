package edu.utsa.cs.sootutil.visitors;

import soot.jimple.InstanceOfExpr;

public class InstanceOfVisitor extends CollectVisitor{
		
	@Override
	public void caseInstanceOfExpr(InstanceOfExpr arg0) {
		this.collected.add(arg0);
		arg0.getOp().apply(this);
	}

}
