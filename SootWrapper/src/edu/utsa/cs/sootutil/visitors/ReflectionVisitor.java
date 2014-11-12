package edu.utsa.cs.sootutil.visitors;

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import soot.jimple.Expr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;

public class ReflectionVisitor extends CollectVisitor{
	private List<Expr> flecInvokes = new ArrayList<Expr>();

	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr arg0) {
		
		if(arg0.getMethodRef().getSignature().indexOf("<java.lang.Class: java.lang.Class forName(java.lang.String)>")!=-1){
			this.collected.add(arg0);
		}
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}
	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
		if(arg0.getMethodRef().getSignature().indexOf("java.lang.reflect")!=-1){
			this.collected.add(arg0);
		}
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}

	public void caseSpecialInvokeExpr(VirtualInvokeExpr arg0) {
		if(arg0.getMethodRef().getSignature().indexOf("java.lang.reflect")!=-1){
			this.collected.add(arg0);
		}
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}

	public void caseInterfaceInvokeExpr(VirtualInvokeExpr arg0) {
		if(arg0.getMethodRef().getSignature().indexOf("java.lang.reflect")!=-1){
			this.collected.add(arg0);
		}
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}
	
	public List<Expr> getFlecInvokes(){
		return this.flecInvokes;
	}
}
