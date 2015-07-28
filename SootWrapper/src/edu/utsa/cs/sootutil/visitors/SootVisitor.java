package edu.utsa.cs.sootutil.visitors;

import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StmtSwitch;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.util.Chain;

public abstract class SootVisitor implements StmtSwitch, JimpleValueSwitch{
	private SootClass curClass;
	private SootMethod curMethod;
		
	public static void visitAll(Chain<SootClass> classes, SootVisitor sv){
		for(SootClass sc : classes){
			if(sc.getName().indexOf("PlaybackEqualizer")!=-1){
				System.out.println();
			}
			sv.setCurrentClass(sc);
			for (SootMethod sm : sc.getMethods()){
				sv.setCurrentMethod(sm);
				if(sm.hasActiveBody()){
					for(Unit u : sm.getActiveBody().getUnits()){
						u.apply(sv);
					}
				}
			}
		}
	}
	
	public SootClass getCurrentClass(){
		return this.curClass;
	}
	public SootMethod getCurrentMethod(){
		return this.curMethod;
	}
	public void setCurrentClass(SootClass sc){
		this.curClass = sc;
	}
	public void setCurrentMethod(SootMethod	sm){
		this.curMethod = sm;
	}
	
	@Override
	public void caseAssignStmt(AssignStmt arg0) {
		if(beforeAssignStmt(arg0)){
			arg0.getRightOp().apply(this);
		}
		afterAssigntmt(arg0);
	}
	public boolean beforeAssignStmt(AssignStmt arg0){
		return true;
	}
	public void afterAssigntmt(AssignStmt arg0){
	}

	@Override
	public void caseBreakpointStmt(BreakpointStmt arg0) {
	}

	@Override
	public void caseEnterMonitorStmt(EnterMonitorStmt arg0) {
	}

	@Override
	public void caseExitMonitorStmt(ExitMonitorStmt arg0) {
	}

	@Override
	public void caseGotoStmt(GotoStmt arg0) {
	}

	@Override
	public void caseIdentityStmt(IdentityStmt arg0) {
	}

	@Override
	public void caseIfStmt(IfStmt arg0) {
		if(beforeIfStmt(arg0)){
			arg0.getCondition().apply(this);
			arg0.getTarget().apply(this);
		}
		afterIfStmt(arg0);
	}
	public boolean beforeIfStmt(IfStmt arg0){
		return true;
	}
	public void afterIfStmt(IfStmt arg0){
	}

	@Override
	public void caseInvokeStmt(InvokeStmt arg0) {
		if(beforeInvokeStmt(arg0)){
			arg0.getInvokeExpr().apply(this);
		}
		afterInvokeStmt(arg0);
	}
	public boolean beforeInvokeStmt(InvokeStmt arg0) {
		return true;
	}
	public void afterInvokeStmt(InvokeStmt arg0) {
	}

	@Override
	public void caseLookupSwitchStmt(LookupSwitchStmt arg0) {
		if(beforeLookupSwitchStmt(arg0)){
			arg0.getDefaultTarget().apply(this);
			arg0.getKey().apply(this);
			for(int i = 0; i<arg0.getTargetCount(); i++){
				Unit ut = arg0.getTarget(i);
				ut.apply(this);
			}
		}
		afterLookupSwitchStmt(arg0);
	}
	public boolean beforeLookupSwitchStmt(LookupSwitchStmt arg0) {
		return true;
	}
	public void afterLookupSwitchStmt(LookupSwitchStmt arg0) {
	}

	@Override
	public void caseNopStmt(NopStmt arg0) {
		
	}

	@Override
	public void caseRetStmt(RetStmt arg0) {
	}

	@Override
	public void caseReturnStmt(ReturnStmt arg0) {
		if(beforeReturnStmt(arg0)){
			arg0.getOp().apply(this);
		}
		afterReturnStmt(arg0);
	}
	public boolean beforeReturnStmt(ReturnStmt arg0) {
		return true;
	}
	public void afterReturnStmt(ReturnStmt arg0) {
	}


	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt arg0) {
		if(beforeTableSwitchStmt()){
			arg0.getDefaultTarget().apply(this);
			arg0.getKey().apply(this);
			for(int i = 0; i<arg0.getTargets().size(); i++){
				Unit ut = arg0.getTarget(i);
				ut.apply(this);
			}
		}
		afterTableSwitchStmt();
	}
	public boolean beforeTableSwitchStmt() {
		return true;
	}
	public void afterTableSwitchStmt() {		
	}


	@Override
	public void caseThrowStmt(ThrowStmt arg0) {
	}

	@Override
	public void defaultCase(Object arg0) {
	}

	@Override
	public void caseAddExpr(AddExpr arg0) {
		if(beforeAddExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterAddExpr(arg0);
	}
	public boolean beforeAddExpr(AddExpr arg0) {
		return true;
	}
	public void afterAddExpr(AddExpr arg0) {
	}

	@Override
	public void caseAndExpr(AndExpr arg0) {
		if(beforeAndExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterAndExpr(arg0);
	}
	public boolean beforeAndExpr(AndExpr arg0) {
		return true;
	}
	public void afterAndExpr(AndExpr arg0) {
	}

	@Override
	public void caseCastExpr(CastExpr arg0) {
		if(beforeCastExpr(arg0)){
			arg0.getOp().apply(this);
		}
		afterCastExpr(arg0);
	}
	public boolean beforeCastExpr(CastExpr arg0) {
		return true;
	}
	public void afterCastExpr(CastExpr arg0) {
	}


	@Override
	public void caseCmpExpr(CmpExpr arg0) {
		if(beforeCmpExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterCmpExpr(arg0);
	}
	public boolean beforeCmpExpr(CmpExpr arg0) {
		return true;
	}
	public void afterCmpExpr(CmpExpr arg0) {
	}


	@Override
	public void caseCmpgExpr(CmpgExpr arg0) {
		if(beforeCmpgExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterCmpgExpr(arg0);
	}
	public boolean beforeCmpgExpr(CmpgExpr arg0) {
		return true;
	}
	public void afterCmpgExpr(CmpgExpr arg0) {
	}


	@Override
	public void caseCmplExpr(CmplExpr arg0) {
		if(beforeCmplExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterCmplExpr(arg0);
	}
	public boolean beforeCmplExpr(CmplExpr arg0) {
		return true;
	}
	public void afterCmplExpr(CmplExpr arg0) {
	}


	@Override
	public void caseDivExpr(DivExpr arg0) {
		if(beforeDivExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterDivExpr(arg0);
	}
	public boolean beforeDivExpr(DivExpr arg0) {
		return true;
	}
	public void afterDivExpr(DivExpr arg0) {
	}


	@Override
	public void caseEqExpr(EqExpr arg0) {
		if(beforeEqExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterEqExpr(arg0);
	}
	public boolean beforeEqExpr(EqExpr arg0) {
		return true;
	}
	public void afterEqExpr(EqExpr arg0) {
	}


	@Override
	public void caseGeExpr(GeExpr arg0) {
		if(beforeGeExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterGeExpr(arg0);
	}
	public boolean beforeGeExpr(GeExpr arg0) {
		return true;
	}
	public void afterGeExpr(GeExpr arg0) {
	}


	@Override
	public void caseGtExpr(GtExpr arg0) {
		if(beforeGtExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterGtExpr(arg0);
	}
	public boolean beforeGtExpr(GtExpr arg0) {
		return true;
	}
	public void afterGtExpr(GtExpr arg0) {
	}
	
	@Override
	public void caseInstanceOfExpr(InstanceOfExpr arg0) {
		if(beforeInstanceOfExpr(arg0)){
			arg0.getOp().apply(this);
		}
		afterInstanceOfExpr(arg0);
	}
	public boolean beforeInstanceOfExpr(InstanceOfExpr arg0) {
		return true;
	}
	public void afterInstanceOfExpr(InstanceOfExpr arg0) {
	}


	@Override
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
		if(beforeInterfaceInvokeExpr(arg0)){
			arg0.getBase().apply(this);
			for(int i = 0; i<arg0.getArgCount(); i++){
				Value ut = arg0.getArg(i);
				ut.apply(this);
			}
		}
		afterInterfaceInvokeExpr(arg0);
	}
	private boolean beforeInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
		return true;
	}
	private void afterInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
	}
	

	@Override
	public void caseLeExpr(LeExpr arg0) {
		if(beforeLeExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterLeExpr(arg0);
	}
	private boolean beforeLeExpr(LeExpr arg0) {
		return true;
	}
	private void afterLeExpr(LeExpr arg0) {
	}

	@Override
	public void caseLengthExpr(LengthExpr arg0) {
		if(beforeLengthExpr(arg0)){
			arg0.getOp().apply(this);
		}
		afterLengthExpr(arg0);
	}
	private boolean beforeLengthExpr(LengthExpr arg0) {
		return true;
	}
	private void afterLengthExpr(LengthExpr arg0) {
	}

	@Override
	public void caseLtExpr(LtExpr arg0) {
		if(beforeLtExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterLtExpr(arg0);
	}
	private boolean beforeLtExpr(LtExpr arg0) {
		return true;
	}
	private void afterLtExpr(LtExpr arg0) {
	}

	@Override
	public void caseMulExpr(MulExpr arg0) {
		if(beforeMulExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterMulExpr(arg0);
	}
	private boolean beforeMulExpr(MulExpr arg0) {
		return true;
	}
	private void afterMulExpr(MulExpr arg0) {
	}
	

	@Override
	public void caseNeExpr(NeExpr arg0) {
		if(beforeNeExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterNeExpr(arg0);
	}
	private boolean beforeNeExpr(NeExpr arg0) {
		return true;
	}
	private void afterNeExpr(NeExpr arg0) {
	}
	

	@Override
	public void caseNegExpr(NegExpr arg0) {
		if(beforeNegExpr(arg0)){
			arg0.getOp().apply(this);
		}
		afterNegExpr(arg0);
	}
	private boolean beforeNegExpr(NegExpr arg0) {
		return true;
	}
	private void afterNegExpr(NegExpr arg0) {
	}

	@Override
	public void caseNewArrayExpr(NewArrayExpr arg0) {
		if(beforeNewArrayExpr(arg0)){
			arg0.getSize().apply(this);
		}
		afterNewArrayExpr(arg0);
	}
	private boolean beforeNewArrayExpr(NewArrayExpr arg0) {
		return true;
	}
	private void afterNewArrayExpr(NewArrayExpr arg0) {
	}

	@Override
	public void caseNewExpr(NewExpr arg0) {
	}

	@Override
	public void caseNewMultiArrayExpr(NewMultiArrayExpr arg0) {
	}

	@Override
	public void caseOrExpr(OrExpr arg0) {
		if(beforeOrExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterOrExpr(arg0);
	}
	private boolean beforeOrExpr(OrExpr arg0) {
		return true;
	}
	private void afterOrExpr(OrExpr arg0) {
	}
	
	@Override
	public void caseRemExpr(RemExpr arg0) {
		if(beforeRemExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterRemExpr(arg0);
	}
	private boolean beforeRemExpr(RemExpr arg0) {
		return true;
	}
	private void afterRemExpr(RemExpr arg0) {
	}

	@Override
	public void caseShlExpr(ShlExpr arg0) {
		if(beforeShlExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterShlExpr(arg0);
	}
	private boolean beforeShlExpr(ShlExpr arg0) {
		return true;
	}
	private void afterShlExpr(ShlExpr arg0) {
	}

	@Override
	public void caseShrExpr(ShrExpr arg0) {
		if(beforeShrExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterShrExpr(arg0);
	}
	private boolean beforeShrExpr(ShrExpr arg0) {
		return true;
	}
	private void afterShrExpr(ShrExpr arg0) {
	}

	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr arg0) {
		if(beforeSpecialInvokeExpr(arg0)){
			arg0.getBase().apply(this);
			for(int i = 0; i<arg0.getArgCount(); i++){
				Value ut = arg0.getArg(i);
				ut.apply(this);
			}
		}
		afterSpecialInvokeExpr(arg0);
	}
	private boolean beforeSpecialInvokeExpr(SpecialInvokeExpr arg0) {
		return true;
	}
	private void afterSpecialInvokeExpr(SpecialInvokeExpr arg0) {
	}

	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr arg0) {
		if(beforeStaticInvokeExpr(arg0)){
			for(int i = 0; i<arg0.getArgCount(); i++){
				Value ut = arg0.getArg(i);
				ut.apply(this);
			}
		}
		afterStaticInvokeExpr(arg0);
	}
	private boolean beforeStaticInvokeExpr(StaticInvokeExpr arg0) {
		return true;
	}
	private void afterStaticInvokeExpr(StaticInvokeExpr arg0) {
	}	

	@Override
	public void caseSubExpr(SubExpr arg0) {
		if(beforeSubExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterSubExpr(arg0);
	}
	private boolean beforeSubExpr(SubExpr arg0) {
		return true;
	}
	private void afterSubExpr(SubExpr arg0) {
	}

	@Override
	public void caseUshrExpr(UshrExpr arg0) {
		if(beforeUshrExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterUshrExpr(arg0);
	}
	private boolean beforeUshrExpr(UshrExpr arg0) {
		return true;
	}
	private void afterUshrExpr(UshrExpr arg0) {
	}
	
	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
		if(beforeVirtualInvokeExpr(arg0)){
			arg0.getBase().apply(this);
			for(int i = 0; i<arg0.getArgCount(); i++){
				Value ut = arg0.getArg(i);
				ut.apply(this);
			}
		}
		afterVirtualInvokeExpr(arg0);
	}
	private boolean beforeVirtualInvokeExpr(VirtualInvokeExpr arg0) {
		return true;
	}
	private void afterVirtualInvokeExpr(VirtualInvokeExpr arg0) {
	}	

	@Override
	public void caseXorExpr(XorExpr arg0) {
		if(beforeXorExpr(arg0)){
			arg0.getOp1().apply(this);
			arg0.getOp2().apply(this);
		}
		afterXorExpr(arg0);
	}
	private boolean beforeXorExpr(XorExpr arg0) {
		return true;
	}
	private void afterXorExpr(XorExpr arg0) {
	}	

	@Override
	public void caseClassConstant(ClassConstant arg0) {
	}

	@Override
	public void caseDoubleConstant(DoubleConstant arg0) {
	}

	@Override
	public void caseFloatConstant(FloatConstant arg0) {
	}

	@Override
	public void caseIntConstant(IntConstant arg0) {
	}

	@Override
	public void caseLongConstant(LongConstant arg0) {
	}

	@Override
	public void caseNullConstant(NullConstant arg0) {
	}

	@Override
	public void caseStringConstant(StringConstant arg0) {
	}

	@Override
	public void caseArrayRef(ArrayRef arg0) {
		if(beforeArrayRef(arg0)){
			arg0.getBase().apply(this);
		}
		afterArrayRef(arg0);
		
	}
	private boolean beforeArrayRef(ArrayRef arg0) {
		return true;
	}
	private void afterArrayRef(ArrayRef arg0) {
	}	

	@Override
	public void caseCaughtExceptionRef(CaughtExceptionRef arg0) {
	}

	@Override
	public void caseInstanceFieldRef(InstanceFieldRef arg0) {
		if(beforeInstanceFieldRef(arg0)){
			arg0.getBase().apply(this);
		}
		afterInstanceFieldRef(arg0);
		
	}
	public boolean beforeInstanceFieldRef(InstanceFieldRef arg0) {
		return true;
	}
	public void afterInstanceFieldRef(InstanceFieldRef arg0) {
	}


	@Override
	public void caseParameterRef(ParameterRef arg0) {
	}

	@Override
	public void caseStaticFieldRef(StaticFieldRef arg0) {
	}

	@Override
	public void caseThisRef(ThisRef arg0) {
	}

	@Override
	public void caseLocal(Local arg0) {
	}

	@Override
	public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
	}

}