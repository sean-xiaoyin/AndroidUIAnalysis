package eruiz.analysis.parser;

import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

public class SampleAnalysis {

	public void analyzeCalls(SootClass sclass) {
		// TODO Auto-generated method stub
		List<SootMethod> methods = sclass.getMethods();
		for(SootMethod method : methods){
			CAStmtVisitor csv = new CAStmtVisitor(method);
			if(method.isConcrete()){
				Body bd = method.getActiveBody();
				System.out.println(bd);
				
				for(Unit ut:bd.getUnits()){
					Stmt st = (Stmt)ut;		
					st.apply(csv);
				}
			}
			System.out.println("Caller:" + csv.getCaller());
			System.out.println("Callees:" + csv.getCallees());

		}
	}

}
