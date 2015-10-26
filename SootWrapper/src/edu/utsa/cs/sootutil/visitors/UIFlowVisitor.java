package edu.utsa.cs.sootutil.visitors;

import edu.utsa.cs.sootutil.FlowCheckStmt;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;

public class UIFlowVisitor extends SootVisitor{
    Hashtable<Object, Set<Object>> flowGraph;
    Hashtable<SootMethod, List<Parameter>> paraTable;
    Set<Object> sourceGraph;
    Set<Object> sinkGraph;
    List <String> list;
    
    public UIFlowVisitor() throws FileNotFoundException{
	this.flowGraph = new Hashtable<Object, Set<Object>>();
	this.paraTable = new Hashtable<SootMethod, List<Parameter>>();
        this.sourceGraph = new HashSet<Object>();
        this.sinkGraph = new HashSet<Object>();
        this.list = classList();
    }
    
    public List <String> classList() throws FileNotFoundException{
        
        List <String> clist = new ArrayList();
        Scanner s = new Scanner(new File("/home/xue/AndroidUIAnalysis/SootWrapper/classname.txt"));
        while (s.hasNextLine()){
            clist.add(s.nextLine());
        }
        s.close();
        
        return clist;
    }
 
    public boolean beforeAssignStmt(AssignStmt arg0){
        Object left = arg0.getLeftOp();
	left = checkField(left);
		
	addEdge(this.flowGraph,arg0.getRightOp(), left);
	return true;
    }
    
    public boolean beforeInstanceFieldRef(InstanceFieldRef ref){
        addEdge(this.flowGraph, ref.getField(), ref);	
	return true;	
    }
    
    public boolean beforeReturnStmt(ReturnStmt arg0) {	
    	Object value = getCurrentMethod();
    	Object key = arg0.getOp();
    	addEdge(this.flowGraph, key, value);
    	
    	return true;
    }
    
    public boolean beforeVirtualInvokeStmt (VirtualInvokeExpr arg0){
        Object base = arg0.getBase();
	SootMethod sm = arg0.getMethod();
	SootClass sc = sm.getDeclaringClass();
        List <String> slist = paraTypeList();
 
	
        if (sm.getName().contains("Activity")){
            sinkGraph.add(sm);
        }
        /*
        if (){
            sinkGraph.add(sm);
        }
                */
        String sn = sc.getShortName();
        if (list.contains(sn)) {
            
            addEdge(this.flowGraph, base, sc);
            List<Parameter> paras = paraTable.get(sm);
            if(paras == null){
                paras = new ArrayList<Parameter>();
                for(int i = 0; i< sm.getParameterTypes().size(); i++){
                    if (slist.contains(sm.getParameterType(i).toString())) {
                        Parameter p = new Parameter(sm, i);
                        paras.add(p);
                    }    
                }
                paraTable.put(sm, paras);
            }
            paras = paraTable.get(sm);
            for(int i = 0; i< paras.size(); i++){
                addEdge(this.flowGraph, arg0.getArg(i), paras.get(i));
            }
            
        }
	return true;
    }

    public boolean beforeVirtualInvokeExpr(VirtualInvokeExpr arg0) {
	
        SootMethod sm = arg0.getMethod();
        if (sm.getName().contains("Activity")){
            sinkGraph.add(sm);
        }
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found
            // check operations
            String operations = fc.operations;
            int i = 0;
            
            while(i < operations.length()){
            String ops = operations.substring(i, i+2);
            for (int j = 0; j < ops.length(); j++){
                if ( j == 0){ //key
                    int k = Integer.parseInt(ops.substring(0, 1));
                    switch (k) {
                        case -1:
                            //key = arg0.
                            break;
                        case 0:
                            key = arg0.getBase();
                            break;
                        default:
                            key = arg0.getArg(k);
                            break;
                    }
                }
                else if (j == 1){ //value
                    int k = Integer.parseInt(ops.substring(1, 2));
                    switch (k) {
                        case -1:
                            //value = arg0.
                            break;
                        case 0:
                            value = arg0.getBase();
                            break;
                        default:
                            value = arg0.getArg(k);  
                            break;
                    }
                }
            }
            
            addEdge(this.flowGraph, key, value);
            i = i + 2;
            }
        }
        return true;
    }
    
    public boolean beforeStaticInvokeExpr(StaticInvokeExpr arg0) {
	SootMethod sm = arg0.getMethod();
        if (sm.getName().contains("Activity")){
            sinkGraph.add(sm);
        }
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found
            // check operations
            String operations = fc.operations;
            int i = 0;
            
            while(i < operations.length()){
            String ops = operations.substring(i, i+2);
            for (int j = 0; j < ops.length(); j++){
                if ( j == 0){ //key
                    int k = Integer.parseInt(ops.substring(0, 1));
                    switch (k) {
                        case -1:
                            //key = arg0.
                            break;
                        case 0:
                            //key = arg0.getBase();
                            break;
                        default:
                            key = arg0.getArg(k);
                            break;
                    }
                }
                else if (j == 1){ //value
                    int k = Integer.parseInt(ops.substring(1, 2));
                    switch (k) {
                        case -1:
                            //value = arg0.
                            break;
                        case 0:
                            //value = arg0.getBase();
                            break;
                        default:
                            value = arg0.getArg(k);  
                            break;
                    }
                }
            }
            
            addEdge(this.flowGraph, key, value);
            i = i + 2;
            }
        }
        return true;
    }
    
    public boolean beforeSpecialInvokeExpr(SpecialInvokeExpr arg0) {
	SootMethod sm = arg0.getMethod();
        if (sm.getName().contains("Activity")){
            sinkGraph.add(sm);
        }
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found
            // check operations
            String operations = fc.operations;
            int i = 0;
            
            while(i < operations.length()){
            String ops = operations.substring(i, i+2);
            for (int j = 0; j < ops.length(); j++){
                if ( j == 0){ //key
                    int k = Integer.parseInt(ops.substring(0, 1));
                    switch (k) {
                        case -1:
                            //key = arg0.
                            break;
                        case 0:
                            key = arg0.getBase();
                            break;
                        default:
                            key = arg0.getArg(k);
                            break;
                    }
                }
                else if (j == 1){ //value
                    int k = Integer.parseInt(ops.substring(1, 2));
                    switch (k) {
                        case -1:
                            //value = arg0.
                            break;
                        case 0:
                            value = arg0.getBase();
                            break;
                        default:
                            value = arg0.getArg(k);  
                            break;
                    }
                }
            }
            
            addEdge(this.flowGraph, key, value);
            i = i + 2;
            }
        }
        return true;
    }
    
    public boolean beforeInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
	SootMethod sm = arg0.getMethod();
        if (sm.getName().contains("Activity")){
            sinkGraph.add(sm);
        }
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found
            // check operations
            String operations = fc.operations;
            int i = 0;
            
            while(i < operations.length()){
            String ops = operations.substring(i, i+2);
            for (int j = 0; j < ops.length(); j++){
                if ( j == 0){ //key
                    int k = Integer.parseInt(ops.substring(0, 1));
                    switch (k) {
                        case -1:
                            //key = arg0.
                            break;
                        case 0:
                            key = arg0.getBase();
                            break;
                        default:
                            key = arg0.getArg(k);
                            break;
                    }
                }
                else if (j == 1){ //value
                    int k = Integer.parseInt(ops.substring(1, 2));
                    switch (k) {
                        case -1:
                            //value = arg0.
                            break;
                        case 0:
                            value = arg0.getBase();
                            break;
                        default:
                            value = arg0.getArg(k);  
                            break;
                    }
                }
            }
            
            addEdge(this.flowGraph, key, value);
            i = i + 2;
            }
        }
        return true;
    }
    
    private Object checkField (Object val) {
	
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
    
    private List <String> paraTypeList () {
        
        List <String> slist = new ArrayList();
        slist.add("String");
        slist.add("View");
        slist.add("CharSequence");
        slist.add("Char");
        
        return slist;
    }




}

    
