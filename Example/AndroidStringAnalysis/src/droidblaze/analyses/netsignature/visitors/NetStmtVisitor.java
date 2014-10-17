package droidblaze.analyses.netsignature.visitors;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import soot.Local;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import droidblaze.analyses.netsignature.api.NetAPI;
import droidblaze.analyses.netsignature.api.NetAPIInvoke;

public class NetStmtVisitor extends StmtVisitor {

	List<NetAPIInvoke> netVbs;
	Hashtable<String, NetAPI> sigList;
	private Hashtable<SootMethod, List<ReturnStmt>> methodReturnTable = new Hashtable<SootMethod, List<ReturnStmt>>();
	private SootMethod currentMethod;
	private List<ReturnStmt> retStmts;
	private List<Value> outputstreams;
	private List<VirtualInvokeExpr> writeStmts;
	public NetStmtVisitor(List<NetAPIInvoke> vbs, Hashtable<String, NetAPI> netAPItable) {
		super();  
		this.netVbs = vbs;
		this.sigList = netAPItable;
		this.currentMethod = null;
		this.outputstreams = new ArrayList<Value>();
		this.writeStmts = new ArrayList<VirtualInvokeExpr>();
	}
	
/*	public void gatherImplicitNetArgs(){
		//TODO adding other net arguments
		for(VirtualInvokeExpr write:writeStmts){
			Value base = write.getBase();
			for(Value v:outputstreams){
				if(equive(v, base)){
					this.netVbs.add(new OutputStreamInvoke(write.getArgs()));
				}
			}
		}
	}*/

/*	private boolean equive(Value v, Value base) {
		// TODO Auto-generated method stub
		if(v==base){return true;}
		else{
			v.
		}
		return false;
	}*/

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
	public void caseAssignStmt(AssignStmt arg0) {
		// TODO Auto-generated method stub
		if(arg0.getRightOp() instanceof VirtualInvokeExpr){
			VirtualInvokeExpr invoke = (VirtualInvokeExpr)arg0.getRightOp();
			if(invoke.getMethod().toString().equals("<getOutputStream>")&&(arg0.getLeftOp() instanceof Local)){
				this.outputstreams.add(arg0.getLeftOp());
			}
		}
	}

	
	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr arg0) {
		// TODO Auto-generated method stub
		NetAPI api = this.sigList.get(arg0.getMethod().toString());
		if(api!=null){
			this.netVbs.add(NetAPI.createNetAPIInvoke(arg0));
		}
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}
	
	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
		// TODO Auto-generated method stub
		NetAPI api = this.sigList.get(arg0.getMethod().toString());
		if(api!=null){
			this.netVbs.add(NetAPI.createNetAPIInvoke(arg0));
		}else if(arg0.getMethod().toString().equals("<outputstream.write>")){
			this.writeStmts.add(arg0);
		}
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}
	
	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr arg0) {
		// TODO Auto-generated method stub
		NetAPI api = this.sigList.get(arg0.getMethod().toString());
		if(api!=null){
			this.netVbs.add(NetAPI.createNetAPIInvoke(arg0));
		}
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
	}
	
	@Override
	public void caseReturnStmt(ReturnStmt arg0) {
		// TODO Auto-generated method stub
		this.retStmts.add(arg0);
		arg0.getOp().apply(this);
	}



	public Hashtable<SootMethod, List<ReturnStmt>> getMethodReturnTable() {
		return methodReturnTable;
	}

	public void setCurrentMethod(SootMethod currentMethod) {
		this.currentMethod = currentMethod;
		this.retStmts = new ArrayList<ReturnStmt>();
		this.methodReturnTable.put(this.currentMethod, this.retStmts);
	}
}
