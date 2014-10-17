package droidblaze.analyses.netsignature.visitors;

import soot.Local;
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

public class StmtVisitor implements StmtSwitch, JimpleValueSwitch{

	public StmtVisitor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void caseAssignStmt(AssignStmt arg0) {
		// TODO Auto-generated method stub
		arg0.getRightOp().apply(this);
	}

	@Override
	public void caseBreakpointStmt(BreakpointStmt arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void caseEnterMonitorStmt(EnterMonitorStmt arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseExitMonitorStmt(ExitMonitorStmt arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseGotoStmt(GotoStmt arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseIdentityStmt(IdentityStmt arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseIfStmt(IfStmt arg0) {
		// TODO Auto-generated method stub
		arg0.getCondition().apply(this);
		arg0.getTarget().apply(this);
	}

	@Override
	public void caseInvokeStmt(InvokeStmt arg0) {
		// TODO Auto-generated method stub
		arg0.getInvokeExpr().apply(this);
	}

	@Override
	public void caseLookupSwitchStmt(LookupSwitchStmt arg0) {
		// TODO Auto-generated method stub
		arg0.getDefaultTarget().apply(this);
		arg0.getKey().apply(this);
		for(int i = 0; i<arg0.getTargetCount(); i++){
			Unit ut = arg0.getTarget(i);
			ut.apply(this);
		}
	}

	@Override
	public void caseNopStmt(NopStmt arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseRetStmt(RetStmt arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void caseReturnStmt(ReturnStmt arg0) {
		// TODO Auto-generated method stub
		arg0.getOp().apply(this);
	}

	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt arg0) {
		// TODO Auto-generated method stub
		arg0.getDefaultTarget().apply(this);
		arg0.getKey().apply(this);
		for(int i = 0; i<arg0.getTargets().size(); i++){
			Unit ut = arg0.getTarget(i);
			ut.apply(this);
		}
	}

	@Override
	public void caseThrowStmt(ThrowStmt arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defaultCase(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseAddExpr(AddExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseAndExpr(AndExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseCastExpr(CastExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp().apply(this);
	}

	@Override
	public void caseCmpExpr(CmpExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseCmpgExpr(CmpgExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseCmplExpr(CmplExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseDivExpr(DivExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseEqExpr(EqExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseGeExpr(GeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseGtExpr(GtExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseInstanceOfExpr(InstanceOfExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp().apply(this);
	}

	@Override
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}

	@Override
	public void caseLeExpr(LeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseLengthExpr(LengthExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp().apply(this);
	}

	@Override
	public void caseLtExpr(LtExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseMulExpr(MulExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseNeExpr(NeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseNegExpr(NegExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp().apply(this);
	}

	@Override
	public void caseNewArrayExpr(NewArrayExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getSize().apply(this);
	}

	@Override
	public void caseNewExpr(NewExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNewMultiArrayExpr(NewMultiArrayExpr arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseOrExpr(OrExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseRemExpr(RemExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseShlExpr(ShlExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseShrExpr(ShrExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}

	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr arg0) {
		// TODO Auto-generated method stub
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}

	@Override
	public void caseSubExpr(SubExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseUshrExpr(UshrExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}

	@Override
	public void caseXorExpr(XorExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getOp1().apply(this);
		arg0.getOp2().apply(this);
	}

	@Override
	public void caseClassConstant(ClassConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseDoubleConstant(DoubleConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseFloatConstant(FloatConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseIntConstant(IntConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseLongConstant(LongConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseNullConstant(NullConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseStringConstant(StringConstant arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseArrayRef(ArrayRef arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
	}

	@Override
	public void caseCaughtExceptionRef(CaughtExceptionRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseInstanceFieldRef(InstanceFieldRef arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
	}

	@Override
	public void caseParameterRef(ParameterRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseStaticFieldRef(StaticFieldRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseThisRef(ThisRef arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void caseLocal(Local arg0) {
		// TODO Auto-generated method stub
		
	}

}
