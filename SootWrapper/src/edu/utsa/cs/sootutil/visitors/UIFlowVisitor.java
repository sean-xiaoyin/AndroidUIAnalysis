package edu.utsa.cs.sootutil.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.VirtualInvokeExpr;

public class UIFlowVisitor extends SootVisitor{
    Hashtable<Object, Set<Object>> flowGraph;
    Hashtable<SootMethod, List<Parameter>> paraTable;
    public UIFlowVisitor(){
	this.flowGraph = new Hashtable<Object, Set<Object>>();
	this.paraTable = new Hashtable<SootMethod, List<Parameter>>();
    }
    
    public boolean beforeAssignStmt(AssignStmt arg0){
	Object left = arg0.getLeftOp();
	left = checkField(left);
		
	addEdge(this.flowGraph, left, arg0.getRightOp());
	return true;
    }
    
    public boolean beforeInstanceFieldRef(InstanceFieldRef ref){
	addEdge(this.flowGraph, ref.getField(), ref);	
	return true;	
    }

    public boolean beforeVirtualInvokeStmt (VirtualInvokeExpr arg0){
	Object base = arg0.getBase();
	SootMethod sm = arg0.getMethod();
	SootClass sc = sm.getDeclaringClass();
	
	addEdge(this.flowGraph, base, sc);
        List<Parameter> paras = paraTable.get(sm);
        if(paras == null){
            paras = new ArrayList<Parameter>();
    	    for(int i = 0; i< sm.getParameterTypes().size(); i++){
	        Parameter p = new Parameter(sm, i);
	        paras.add(p);
	    }
    	    paraTable.put(sm, paras);
        }
	
	paras = paraTable.get(sm);
	for(int i = 0; i< paras.size(); i++){
    	    addEdge(this.flowGraph, arg0.getArg(i), paras.get(i));
	}
	return true;
    }

    
    
    private Object checkField(
	    Object val)
    {
	if(val instanceof InstanceFieldRef){
	    InstanceFieldRef ref = (InstanceFieldRef)val;
	    return ref.getField();
	}
	return val;
    }

    private void addEdge(
	    Hashtable<Object, Set<Object>> graph,
	    Object key, Object value)
    {
	if(graph.containsKey(key)){
	    graph.get(key).add(value);
	}else{
	    HashSet<Object> targets = new HashSet<Object>();
	    targets.add(value);
	    graph.put(key, targets);
	}
    }
}
