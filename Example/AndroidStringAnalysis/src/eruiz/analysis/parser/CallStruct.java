package eruiz.analysis.parser;

import java.util.ArrayList;

public class CallStruct {

	private String callee;
	private ArrayList<String> callers;
	
	public CallStruct(String callee){
		this.callee = callee;
		callers = new ArrayList<String>();
	}
	
	public void addCaller(String caller){
		callers.add(caller);
	}

	public String getCallee() {
		return callee;
	}
	
	public ArrayList<String> getCallers(){
		return callers;
	}
	
	// DOT graph description language
	// Graphviz
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("Callee: " + callee + "\n" + "Callees:\n");
		
		if(callers != null){
			for(String s : callers){
				result.append(s + "\n");
			}
		}
		
		return result.toString();
	}
	
}
