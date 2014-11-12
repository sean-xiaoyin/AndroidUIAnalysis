package eruiz.analysis.parser;

import java.util.ArrayList;
import java.util.List;

import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.SpecialInvokeExpr;
import droidblaze.analyses.netsignature.visitors.StmtVisitor;

public class CAStmtVisitor extends StmtVisitor {
	private SootMethod caller;
	private List<SootMethod> callees;
	public CAStmtVisitor(SootMethod caller) {
		this.caller = caller;
		this.callees = new ArrayList<SootMethod>();
	}
	public SootMethod getCaller(){
		return this.caller;
	}
	public List<SootMethod> getCallees(){
		return this.callees;
	}
	
	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
		
		SootMethod sm = arg0.getMethod();
		this.callees.add(sm);
		
	}
	
//	@Override
//	public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
//		
//	}
	
}
