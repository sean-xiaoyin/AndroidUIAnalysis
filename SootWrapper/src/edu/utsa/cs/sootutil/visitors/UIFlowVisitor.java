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
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;

public class UIFlowVisitor extends SootVisitor{
    public Hashtable<Object, Set<Object>> flowGraph;
    public Hashtable<SootMethod, List<Parameter>> paraTable;
    public Set<Object> sourceGraph;
    public Set<Object> sinkGraph;
    public List <String> list;
    
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
 
    @Override
    public boolean beforeAssignStmt(AssignStmt arg0){  
        
        Object left = arg0.getLeftOp();
	left = checkField(left);
		
	addEdge(this.flowGraph,arg0.getRightOp(), left);
	return true;
    }
    
    @Override
    public boolean beforeInstanceFieldRef(InstanceFieldRef ref){
        addEdge(this.flowGraph, ref.getField(), ref);	
	return true;	
    }
    
    @Override
    public boolean beforeReturnStmt(ReturnStmt arg0) {	
        Object value = getCurrentMethod();
    	Object key = arg0.getOp();
    	addEdge(this.flowGraph, key, value);
    	
    	return true;
    }
    
    @Override
    public boolean beforeVirtualInvokeExpr(VirtualInvokeExpr arg0) {
	handleInvoke(arg0);
        return true;
    }
    
    @Override
    public boolean beforeStaticInvokeExpr(StaticInvokeExpr arg0) {
	handleInvoke(arg0);
        return true;
    }
    
    @Override
    public boolean beforeSpecialInvokeExpr(SpecialInvokeExpr arg0) {
	handleInvoke(arg0);
        return true;
    }
    
    public boolean beforeInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
	handleInvoke(arg0);
        return true;
    }
    
    public boolean beforeIdentityStmt(IdentityStmt arg0) {
	Value v = arg0.getRightOp();
        
        if (v instanceof ParameterRef) {
            
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
        System.out.println(key + ":" + value);
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

    /* handleInvoke for VirtualInvokeExpr  */
    private boolean handleInvoke (VirtualInvokeExpr arg0) {
        
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getJavaStyleName();
        List <String> slist = paraTypeList();
        
        // add activity
        if (sn.equalsIgnoreCase("Activity")){
            sinkGraph.add(sm);
        }
        
        // add source
        if (sn.equalsIgnoreCase("Resources")){
            sourceGraph.add(sm);
        }
        
        
        // first checking if in the Designed classes
        if (list.contains(sn) || sm.getParameterTypes().contains(sn)) {
            
            // second checking whether in the API list
            FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
            Value key = null;
            Value value = null;
            if ( fc != null) { // found
                // check operations
                List<String> operations = fc.operations;
                int i = 0;
            
                while(i < operations.size()){
                List<String> ops = operations.subList(i, i+2);
                for (int j = 0; j < ops.size(); j++){
                    if ( j == 0){ //key
                        int k = Integer.parseInt(ops.get(0));
                        switch (k) {
                            case -1:
                                key = arg0;
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
                        int k = Integer.parseInt(ops.get(1));
                        switch (k) {
                            case -1:
                                System.out.println("arg0:" + arg0);
                                value = arg0;
                                break;
                            case 0:
                                System.out.println("arg0base:" + arg0.getBase());
                                value = arg0.getBase();
                                break;
                            default:
                                System.out.println("argk:" + arg0.getArg(k));
                                value = arg0.getArg(k);  
                                break;
                        }
                    }
                }
                addEdge(this.flowGraph, key, value);
                
                i = i + 2;
                }
            }
            else {
                
                addEdge(this.flowGraph, arg0.getBase(), sc);
                List<Parameter> paras = paraTable.get(sm);
                if (paras == null) {
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
                    System.out.println("para:" + paras.get(i));
                    addEdge(this.flowGraph, arg0.getArg(i), paras.get(i));
                }
                
            } 
        }
        return true;
    }
        
            
    /* handleInvoke for SpecialInvokeExpr */
    private boolean handleInvoke (SpecialInvokeExpr arg0) {
        
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getJavaStyleName();
        List <String> slist = paraTypeList();
        
        // add activity
        if (sn.equalsIgnoreCase("Activity")){
            sinkGraph.add(sm);
        }
        
        // add source
        if (sn.equalsIgnoreCase("Resources")){
            sourceGraph.add(sm);
        }
        
        // first checking if in the Designed classes
        if (list.contains(sn) || sm.getParameterTypes().contains(sn)) {
            
            // second checking whether in the API list
            FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
            Value key = null;
            Value value = null;
            if ( fc != null) { // found
                // check operations
                List<String> operations = fc.operations;
                int i = 0;
            
                while(i < operations.size()){
                List<String> ops = operations.subList(i, i+2);
                for (int j = 0; j < ops.size(); j++){
                    if ( j == 0){ //key
                        int k = Integer.parseInt(ops.get(0));
                        switch (k) {
                            case -1:
                                key = arg0;
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
                        int k = Integer.parseInt(ops.get(1));
                        switch (k) {
                            case -1:
                                value = arg0;
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
            else {
                
                addEdge(this.flowGraph, arg0.getBase(), sc);
                List<Parameter> paras = paraTable.get(sm);
                if (paras == null) {
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
                    System.out.println("para:" + paras.get(i));
                    addEdge(this.flowGraph, arg0.getArg(i), paras.get(i));
                   
                }
                
            } 
        }
        return true;
    }
    
    /* handleInvoke for StaticInvokeExpr */
    private boolean handleInvoke (StaticInvokeExpr arg0) {
        
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getJavaStyleName();
        List <String> slist = paraTypeList();
        
        // add activity
        if (sn.equalsIgnoreCase("Activity")){
            sinkGraph.add(sm);
        }
        
        // add source
        if (sn.equalsIgnoreCase("Resources")){
            sourceGraph.add(sm);
        }
        
        // first checking if in the Designed classes
        if (list.contains(sn) || sm.getParameterTypes().contains(sn)) {
            
            // second checking whether in the API list
            FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
            Value key = null;
            Value value = null;
            if ( fc != null) { // found
                // check operations
                List<String> operations = fc.operations;
                int i = 0;
            
                while(i < operations.size()){
                List<String> ops = operations.subList(i, i+2);
                for (int j = 0; j < ops.size(); j++){
                    if ( j == 0){ //key
                        int k = Integer.parseInt(ops.get(0));
                        switch (k) {
                            case -1:
                                key = arg0;
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
                        int k = Integer.parseInt(ops.get(1));
                        switch (k) {
                            case -1:
                                value = arg0;
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
            else {
                
                //addEdge(this.flowGraph, arg0.getBase(), sc);
                arg0.getMethodRef();
                
                
                
                List<Parameter> paras = paraTable.get(sm);
                if (paras == null) {
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
                    System.out.println("para:" + paras.get(i));                               
                    addEdge(this.flowGraph, arg0.getArg(i), paras.get(i));
                }
                
            }
        }
        return true;
    }
    
    /* handleInvoke for InterfaceInvokeExpr */
    private boolean handleInvoke (InterfaceInvokeExpr arg0) {
        
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getJavaStyleName();
        List <String> slist = paraTypeList();
        
        // add activity
        if (sn.equalsIgnoreCase("Activity")){
            sinkGraph.add(sm);
        }
        
        // add source
        if (sn.equalsIgnoreCase("Resources")){
            sourceGraph.add(sm);
        }
        
        // first checking if in the Designed classes
        if (list.contains(sn) || sm.getParameterTypes().contains(sn)) {
            
            // second checking whether in the API list
            FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
            Value key = null;
            Value value = null;
            if ( fc != null) { // found
                // check operations
                List<String> operations = fc.operations;
                int i = 0;
            
                while(i < operations.size()){
                List<String> ops = operations.subList(i, i+2);
                for (int j = 0; j < ops.size(); j++){
                    if ( j == 0){ //key
                        int k = Integer.parseInt(ops.get(0));
                        switch (k) {
                            case -1:
                                key = arg0;
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
                        int k = Integer.parseInt(ops.get(1));
                        switch (k) {
                            case -1:
                                value = arg0;
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
            else {
                
                addEdge(this.flowGraph, arg0.getBase(), sc);
                List<Parameter> paras = paraTable.get(sm);
                if (paras == null) {
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
                    System.out.println("para:" + paras.get(i));            
                    addEdge(this.flowGraph, arg0.getArg(i), paras.get(i));
                }
                
            }
        }
        return true;
    }

}

    
