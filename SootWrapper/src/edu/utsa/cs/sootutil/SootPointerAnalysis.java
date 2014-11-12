package edu.utsa.cs.sootutil;


import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Value;
import soot.jimple.FieldRef;

public class SootPointerAnalysis {
	private PointsToAnalysis pta;
	public SootPointerAnalysis(PointsToAnalysis pta){
		this.pta = pta;
	}
	
	public PointsToAnalysis getAnalysis(){
		return this.pta;
	}
	
    public PointsToSet reachingObjects(Value v) throws ExpressionPointerException{
    	if(v instanceof Local){
    		Local l = (Local)v;
    		return this.pta.reachingObjects(l);
    	}else if(v instanceof FieldRef){
    		FieldRef ref = (FieldRef) v;
    		return this.pta.reachingObjects(ref.getField());
    	}else{
    		throw new ExpressionPointerException("Trying to query points to set of an expression");
    	}
    }
}
