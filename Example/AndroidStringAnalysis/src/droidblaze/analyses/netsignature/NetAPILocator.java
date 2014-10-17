package droidblaze.analyses.netsignature;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.util.Chain;
import droidblaze.analyses.netsignature.api.NetAPI;
import droidblaze.analyses.netsignature.api.NetAPIInvoke;
import droidblaze.analyses.netsignature.visitors.NetStmtVisitor;

public class NetAPILocator {

	private Hashtable<SootMethod, List<ReturnStmt>> methodReturnTable;
	
	public NetAPILocator() {
		// TODO Auto-generated constructor stub
	}

	public List<NetAPIInvoke> locate(Chain<SootClass> scs, Hashtable<String, NetAPI> netAPItable) {
		// TODO Auto-generated method stub
		Iterator<SootClass> it = scs.iterator();
		List<NetAPIInvoke> vbs = new ArrayList<NetAPIInvoke>();
//		HashMap options = new HashMap();
//		options.put("propagator", "worklist");
		
//		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		NetStmtVisitor nsv = new NetStmtVisitor(vbs, netAPItable);
		
		//TODO including catch blocks
		
		while(it.hasNext()){
			SootClass sc = it.next();
			System.out.println("locating net API in "+ sc.getName()+"...");
			List<SootMethod> sms = sc.getMethods();
			for(SootMethod sm: sms){
				if(sm.isConcrete()){
					Body bd = sm.getActiveBody();
					nsv.setCurrentMethod(sm);
					for(Unit ut:bd.getUnits()){
						Stmt st = (Stmt)ut;
					
						st.apply(nsv);
					}
				}
			}
		}
		this.methodReturnTable = nsv.getMethodReturnTable();
		return vbs;
	}

	public Hashtable<SootMethod, List<ReturnStmt>> getMethodReturnTable() {
		return methodReturnTable;
	}

}
