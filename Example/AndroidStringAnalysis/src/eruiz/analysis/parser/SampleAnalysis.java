package eruiz.analysis.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

public class SampleAnalysis {
	
	private String filename;
	
	public SampleAnalysis(String filename){
		this.filename = filename;
	}

	public void analyzeCalls(SootClass sclass) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(filename, true));
			
			List<SootMethod> methods = sclass.getMethods();
			for(SootMethod method : methods){
				//System.out.println("SootMethod: " + method.toString() + "\n");
				CAStmtVisitor csv = new CAStmtVisitor(method);
				if(method.isConcrete()){
					Body bd = method.getActiveBody();
					System.out.println(bd);
					
					for(Unit ut:bd.getUnits()){
						Stmt st = (Stmt)ut;		
						st.apply(csv);
					}
				}
				//System.out.println("Caller:" + csv.getCaller());
				//System.out.println("Callees:" + csv.getCallees());
				
				CallStruct cs = new CallStruct(csv.getCaller().toString());
				
				List<SootMethod> callers = csv.getCallees();
				
				if(callers.isEmpty()){
					cs.addCaller("None");
				} else {
					for(SootMethod s : callers){
						cs.addCaller(s.toString());
					}	
				}
				
				System.out.println(cs.toString()); // print to screen
				writer.println(cs.toString());     // write to file
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
