package eruiz.analysis.parser;

import java.util.ArrayList;

public class CallStruct {

	private String callee;
	private ArrayList<String> callers;
	
	public CallStruct(String callee){
		this.callee = callee;
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
}
