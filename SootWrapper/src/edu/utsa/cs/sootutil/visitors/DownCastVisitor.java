package edu.utsa.cs.sootutil.visitors;

import soot.jimple.CastExpr;

public class DownCastVisitor extends CollectVisitor{
	
	@Override
	public void caseCastExpr(CastExpr arg0) {
		this.collected.add(arg0);
		arg0.getOp().apply(this);
	}
	
}
