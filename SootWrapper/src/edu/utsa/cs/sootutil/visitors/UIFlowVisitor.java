package edu.utsa.cs.sootutil.visitors;

import edu.utsa.cs.sootutil.FlowCheckStmt;
import edu.utsa.cs.sootutio.uigraph.UIGraph;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;

public class UIFlowVisitor extends SootVisitor{
    public UIGraph graph;
    public List <String> list;
    public Hashtable<String, String> ArgIndexTable; // methodname+parametertye+number
    
    public UIFlowVisitor() throws FileNotFoundException{
	this.graph = new UIGraph();
        this.list = classList();
        this.ArgIndexTable = new Hashtable<>();
    }
    
    public List <String> classList() throws FileNotFoundException{
        
        List <String> clist = new ArrayList();
        try (Scanner s = new Scanner(new File("/home/xue/AndroidUIAnalysis/SootWrapper/classname.txt"))) {
            while (s.hasNextLine()){
                clist.add(s.nextLine());
            }
        }
        
        return clist;
    }
 
    @Override
    public boolean beforeAssignStmt(AssignStmt arg0){  
        
        Object left = arg0.getLeftOp();
	left = checkField(left);
	this.graph.addNode(left, curMethod);
        this.graph.addNode(arg0.getRightOp(), curMethod);	
	this.graph.addEdge(arg0.getRightOp(), left, curMethod);
	return true;
    }
    
    @Override
    public boolean beforeInstanceFieldRef(InstanceFieldRef ref){       
        this.graph.addNode(ref, curMethod);
        this.graph.addNode(ref.getField(), curMethod);
        this.graph.addEdge(ref.getField(), ref, curMethod);
	return true;	
    }
    
    @Override
    public boolean beforeReturnStmt(ReturnStmt arg0) {	
        
        Object key = arg0.getOp();
        Object value = checkField (key);
        this.graph.addNode(key, curMethod);
        this.graph.addNode(value, curMethod);
    	this.graph.addEdge(key, value, curMethod);
    	
    	return true;
    }
    
    private Object checkField (Object val) {
	
        if(val instanceof InstanceFieldRef){
	    InstanceFieldRef ref = (InstanceFieldRef)val;
	    return ref.getField();
	}
	return val;
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

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean beforeIdentityStmt(IdentityStmt arg0) {
	//System.out.println("lineNUmber: " + arg0.getTag("LineNumberTag") + "\n");
        Stmt s = getCurStmt();
        Value v = arg0.getRightOp();
        int Num = 0;
        
        // Get Parameter type and name
        String paraType = arg0.getRightOp().getType().toString();       
        
        if (list.contains(paraType)) {   // parameter type is subtype of View
            
            System.out.print("beforeIdentityStmt\n");
            String methodName = getCurrentMethod().getName();
            String packageName = getCurrentClass().getJavaPackageName();
            String className = getCurrentClass().getName();
            
            if (v instanceof ParameterRef) {
                
                ParameterRef parameterRef = (ParameterRef) v;               
                String key = packageName + "_" + className + "_"  + methodName;
                String Right = arg0.getRightOp().toString()+ "_" + arg0.getRightOp().getType().toString();
                String Left = arg0.getLeftOp().toString()+ "_" + arg0.getLeftOp().getType().toString();
                // add ParameterRef to ArgIndexTable with modified type
                if ( ArgIndexTable.containsKey(key) == false ) {  //does not contain 
                    key = key + Integer.toString(Num);
                    ArgIndexTable.put(key, Right);    
                }
                else {
                    while (ArgIndexTable.containsKey(key)) {
                        Num = Num + 1;
                        key = key + Integer.toString(Num);
                    }                       
                    ArgIndexTable.put(key, Right);
                }
                
                this.graph.addEdge(Right, Left, curMethod);
                System.out.print("key: " + Right + "\n");
                System.out.print("value: " + Left + "\n");
                
        /*        
                    Hashtable<Object, ArrayList<Value>> paraList = new Hashtable<Object, ArrayList<Value>>();
                    ArgIndexTable.put(methodName, paraList);
                    if (ArgIndexTable.get(methodName).containsKey(paraType) == false) { //does not contain paraType
                        ArrayList<Value> reList = new ArrayList<Value>();
                        ArgIndexTable.get(methodName).put(paraType, reList);
                        ArgIndexTable.get(methodName).get(paraType).add(arg0.getLeftOp());
                    }
                }
                else {                                                        // contain methodName
                    if (ArgIndexTable.get(methodName).containsKey(paraType) == false) { //does not contain paraType
                        ArrayList<Value> reList = new ArrayList<Value>();
                        ArgIndexTable.get(methodName).put(paraType, reList);
                        ArgIndexTable.get(methodName).get(paraType).add(arg0.getLeftOp());
                    }
                    else {                                                              // contain paraType
                        ArgIndexTable.get(methodName).get(paraType).add(arg0.getLeftOp());
                    }
                }*/
                
                // Also add this identity to edge if it belongs to Designed classes or has certain paratypes
                
                //if(list.contains(methodName) || compareArrays (sm))
                System.out.print("ArgIndex key: " + key + "\n");
                System.out.print("ArgIndex value: " + ArgIndexTable.get(key) + "\n");
            }
  
            System.out.println("ArgIndexTable: " + ArgIndexTable + "\n"); 
        }
        
        
        return true;
    }
    
    private List <String> paraTypeList () {
        
        List <String> slist = new ArrayList();
        slist.add("java.lang.String");
        slist.add("android.view.View");
        slist.add("java.lang.CharSequence");
        slist.add("char[]");
        
        return slist;
    }
    
    // Check paratypes if it contains certain type.
    private boolean compareArrays (SootMethod sm) {
        List <String> slist = paraTypeList();
        for (String s : slist) {
            if(sm.getParameterTypes().contains(s))
                return true;
        }    
        return false;
    }

    /* handleInvoke for VirtualInvokeExpr  */
    private boolean handleInvoke (VirtualInvokeExpr arg0) {

        System.out.print("VirtualInvoke\n");
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getName();
        
        // add activity
        if (sn.contains("Activity")){
            this.graph.sinkGraph.add(sm);
        }
        
        // add source
        if (sn.contains("Resources")){
            this.graph.sourceGraph.add(sm);
        }
            
        // first check whether in the API list
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found in API List
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
                                key = arg0.getArg(k-1);
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
                                value = arg0.getArg(k-1);  
                                break;
                        }
                    }
                }
                       
                Stmt s = getCurStmt();
                String APIkey = key.toString() + "_" + key.getType().toString();
                String APIvalue = value.toString() + "_" + value.getType().toString();
                this.graph.addEdge(APIkey, APIvalue, curMethod);
                System.out.print("API key: " + APIkey + "\n");
                System.out.print("API value:" + APIvalue +"\n");                
                i = i + 2;
            }
        }
        
        if (FlowCheckStmt.fcMatchMethodName(sm, fcSet) == false){  // MethodName is not in API list
            // checking if in the Designed classes or has certain paratypes
            Stmt s = getCurStmt();
            if (list.contains(sn) || compareArrays (sm)) { 
                System.out.print("VirtualInvokeExpr Find Sepcial Paratypes or in subclass of View \n");
                String methodName = getCurrentMethod().getName();
                String packageName = getCurrentClass().getJavaPackageName();
                String className = getCurrentClass().getName();
                
                for(int i = 0; i < sm.getParameterTypes().size(); i++){ // check parameter one by one
                    String ArgKey = packageName + "_" + className + "_"  + methodName + Integer.toString(i);
                    String sArg = arg0.getArg(i).toString() + "_" + arg0.getArg(i).getType().toString();
                    if (ArgIndexTable.containsKey(ArgKey) == true) { //found in ArgIndexTable
                        System.out.print("ArgIndex key: " + ArgKey + "\n");
                        System.out.print("ArgIndex value: " + ArgIndexTable.get(ArgKey) + "\n");

                        //this.graph.addEdge(sArg, ArgIndexTable.get(ArgKey), curMethod);
                        this.graph.addEdge(sArg, ArgKey, curMethod);
                        System.out.print("arg0.getArg(i): " + sArg + "\n");
                    }
                    else{  // not found in ArgIndexTable
                        // Then add to ArgIndexTable
                        int Num = 0;
                        String newArgKey = packageName + "_" + className + "_"  + methodName;
                        if ( ArgIndexTable.containsKey(newArgKey) == false ) {  //does not contain 
                            newArgKey = newArgKey + Integer.toString(Num);
                            ArgIndexTable.put(newArgKey, sArg);    
                        }
                        else {
                            while (ArgIndexTable.containsKey(newArgKey)) {
                                Num = Num + 1;
                                newArgKey = newArgKey + Integer.toString(Num);
                            }                       
                            ArgIndexTable.put(newArgKey, sArg);
                        }

                        this.graph.addEdge(sArg, newArgKey, curMethod);
                        System.out.print("key: " + arg0.getArg(i) + "\n");
                        System.out.print("value: " + arg0.getBase() + "\n");
                    } // end else
                } // end for
            }
        } // end MethodName is not in API list

        return true;
    }
        
            
    /* handleInvoke for SpecialInvokeExpr */
    private boolean handleInvoke (SpecialInvokeExpr arg0) {
        
        System.out.print("SpecialInvoke\n");
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getName();
        
        // add activity
        if (sn.contains("Activity")){
            this.graph.sinkGraph.add(sm);
        }
        
        // add source
        if (sn.contains("Resources")){
            this.graph.sourceGraph.add(sm);
        }
            
        // first check whether in the API list
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found in API List
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
                                key = arg0.getArg(k-1);
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
                                value = arg0.getArg(k-1);  
                                break;
                        }
                    }
                }
                       
                Stmt s = getCurStmt();
                String APIkey = key.toString() + "_" + key.getType().toString();
                String APIvalue = value.toString() + "_" + value.getType().toString();
                this.graph.addEdge(APIkey, APIvalue, curMethod);
                System.out.print("API key: " + APIkey + "\n");
                System.out.print("API value:" + APIvalue +"\n");                
                i = i + 2;
            }
        }
            
        if (FlowCheckStmt.fcMatchMethodName(sm, fcSet) == false){  // MethodName is not in API list
            // checking if in the Designed classes or has certain paratypes
            Stmt s = getCurStmt();
            if (list.contains(sn) || compareArrays (sm)) { 
                System.out.print("SpecialInvokeExpr Find Sepcial Paratypes or in subclass of View \n");
                String methodName = getCurrentMethod().getName();
                String packageName = getCurrentClass().getJavaPackageName();
                String className = getCurrentClass().getName();
                
                for(int i = 0; i < sm.getParameterTypes().size(); i++){ // check parameter one by one
                    String ArgKey = packageName + "_" + className + "_"  + methodName + Integer.toString(i);
                    String sArg = arg0.getArg(i).toString() + "_" + arg0.getArg(i).getType().toString();
                    if (ArgIndexTable.containsKey(ArgKey) == true) { //found in ArgIndexTable
                        System.out.print("ArgIndex key: " + ArgKey + "\n");
                        System.out.print("ArgIndex value: " + ArgIndexTable.get(ArgKey) + "\n");

                        this.graph.addEdge(sArg, ArgKey, curMethod);
                        //this.graph.addEdge(sArg, ArgIndexTable.get(ArgKey), curMethod);
                        System.out.print("arg0.getArg(i): " + sArg + "\n");
                    }
                    else{  // not found in ArgIndexTable
                        // Then add to ArgIndexTable
                        int Num = 0;
                        String newArgKey = packageName + "_" + className + "_"  + methodName;
                        if ( ArgIndexTable.containsKey(newArgKey) == false ) {  //does not contain 
                            newArgKey = newArgKey + Integer.toString(Num);
                            ArgIndexTable.put(newArgKey, sArg);    
                        }
                        else {
                            while (ArgIndexTable.containsKey(newArgKey)) {
                                Num = Num + 1;
                                newArgKey = newArgKey + Integer.toString(Num);
                            }                       
                            ArgIndexTable.put(newArgKey, sArg);
                        }

                        this.graph.addEdge(sArg, newArgKey, curMethod);
                        System.out.print("key: " + arg0.getArg(i) + "\n");
                        System.out.print("value: " + arg0.getBase() + "\n");
                    } // end else
                } // end for
            }
        } // end MethodName is not in API list
        return true;
    }
    
    /* handleInvoke for StaticInvokeExpr */
    private boolean handleInvoke (StaticInvokeExpr arg0) {
        
        System.out.print("StaticInvoke\n");
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getName();
        
        // add activity
        if (sn.contains("Activity")){
            this.graph.sinkGraph.add(sm);
        }
        
        // add source
        if (sn.contains("Resources")){
            this.graph.sourceGraph.add(sm);
        }
            
        // first check whether in the API list
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found in API List
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
                                key = arg0.getArg(k-1);
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
                                value = arg0.getArg(k-1);  
                                break;
                        }
                    }
                }
                       
                Stmt s = getCurStmt();
                String APIkey = key.toString() + "_" + key.getType().toString();
                String APIvalue = value.toString() + "_" + value.getType().toString();
                this.graph.addEdge(APIkey, APIvalue, curMethod);
                System.out.print("API key: " + APIkey + "\n");
                System.out.print("API value:" + APIvalue +"\n");                
                i = i + 2;
            }
        }
        
        if (FlowCheckStmt.fcMatchMethodName(sm, fcSet) == false){  // MethodName is not in API list
            // checking if in the Designed classes or has certain paratypes
            Stmt s = getCurStmt();
            if (list.contains(sn) || compareArrays (sm)) { 
                System.out.print("StaticInvokeExpr Find Sepcial Paratypes or in subclass of View \n");
                String methodName = getCurrentMethod().getName();
                String packageName = getCurrentClass().getJavaPackageName();
                String className = getCurrentClass().getName();
                
                for(int i = 0; i < sm.getParameterTypes().size(); i++){ // check parameter one by one
                    String ArgKey = packageName + "_" + className + "_"  + methodName + Integer.toString(i);
                    String sArg = arg0.getArg(i).toString() + "_" + arg0.getArg(i).getType().toString();
                    if (ArgIndexTable.containsKey(ArgKey) == true) { //found in ArgIndexTable
                        System.out.print("ArgIndex key: " + ArgKey + "\n");
                        System.out.print("ArgIndex value: " + ArgIndexTable.get(ArgKey) + "\n");

                        this.graph.addEdge(sArg, ArgKey, curMethod);
                        //this.graph.addEdge(sArg, ArgIndexTable.get(ArgKey), curMethod);
                        System.out.print("arg0.getArg(i): " + sArg + "\n");
                    }
                    else{  // not found in ArgIndexTable
                        // Then add to ArgIndexTable
                        int Num = 0;
                        String newArgKey = packageName + "_" + className + "_"  + methodName;
                        if ( ArgIndexTable.containsKey(newArgKey) == false ) {  //does not contain 
                            newArgKey = newArgKey + Integer.toString(Num);
                            ArgIndexTable.put(newArgKey, sArg);    
                        }
                        else {
                            while (ArgIndexTable.containsKey(newArgKey)) {
                                Num = Num + 1;
                                newArgKey = newArgKey + Integer.toString(Num);
                            }                       
                            ArgIndexTable.put(newArgKey, sArg);
                        }

                        this.graph.addEdge(sArg, newArgKey, curMethod);
                        System.out.print("key: " + arg0.getArg(i) + "\n");
                        //System.out.print("value: " + arg0.getBase() + "\n");
                    } // end else
                } // end for
            }
        } // end MethodName is not in API list
        return true;
    }
    
    /* handleInvoke for InterfaceInvokeExpr */
    private boolean handleInvoke (InterfaceInvokeExpr arg0) {
        
        System.out.print("InterfaceInvoke\n");
        SootMethod sm = arg0.getMethod();
        SootClass sc = sm.getDeclaringClass();
        String sn = sc.getName();
        
        // add activity
        if (sn.contains("Activity")){
            this.graph.sinkGraph.add(sm);
        }
        
        // add source
        if (sn.contains("Resources")){
            this.graph.sourceGraph.add(sm);
        }
            
        // first check whether in the API list
        FlowCheck fc = FlowCheckStmt.fcMatch(sm,fcSet);
        Value key = null;
        Value value = null;
        if ( fc != null) { // found in API List
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
                                key = arg0.getArg(k-1);
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
                                value = arg0.getArg(k-1);  
                                break;
                        }
                    }
                }
                       
                Stmt s = getCurStmt();
                String APIkey = key.toString() + "_" + key.getType().toString();
                String APIvalue = value.toString() + "_" + value.getType().toString();

                this.graph.addEdge(APIkey, APIvalue, curMethod);
                System.out.print("API key: " + APIkey + "\n");
                System.out.print("API value:" + APIvalue +"\n");                
                i = i + 2;
            }
        }
            
        if (FlowCheckStmt.fcMatchMethodName(sm, fcSet) == false){  // MethodName is not in API list
            // checking if in the Designed classes or has certain paratypes
            Stmt s = getCurStmt();
            if (list.contains(sn) || compareArrays (sm)) { 
                System.out.print("InterfaceInvokeExpr Find Sepcial Paratypes or in subclass of View \n");
                String methodName = getCurrentMethod().getName();
                String packageName = getCurrentClass().getJavaPackageName();
                String className = getCurrentClass().getName();
                
                for(int i = 0; i < sm.getParameterTypes().size(); i++){ // check parameter one by one
                    String ArgKey = packageName + "_" + className + "_"  + methodName + Integer.toString(i);
                    String sArg = arg0.getArg(i).toString() + "_" + arg0.getArg(i).getType().toString();
                    if (ArgIndexTable.containsKey(ArgKey) == true) { //found in ArgIndexTable
                        System.out.print("ArgIndex key: " + ArgKey + "\n");
                        System.out.print("ArgIndex value: " + ArgIndexTable.get(ArgKey) + "\n");
                        this.graph.addEdge(sArg, ArgKey, curMethod);
                        //this.graph.addEdge(sArg, ArgIndexTable.get(ArgKey), curMethod);
                        System.out.print("arg0.getArg(i): " + sArg + "\n");
                    }
                    else{  // not found in ArgIndexTable
                        // Then add to ArgIndexTable
                        int Num = 0;
                        String newArgKey = packageName + "_" + className + "_"  + methodName;
                        if ( ArgIndexTable.containsKey(newArgKey) == false ) {  //does not contain 
                            newArgKey = newArgKey + Integer.toString(Num);
                            ArgIndexTable.put(newArgKey, sArg);    
                        }
                        else {
                            while (ArgIndexTable.containsKey(newArgKey)) {
                                Num = Num + 1;
                                newArgKey = newArgKey + Integer.toString(Num);
                            }                       
                            ArgIndexTable.put(newArgKey, sArg);
                        }
                        this.graph.addEdge(sArg, newArgKey, curMethod);
                        System.out.print("key: " + arg0.getArg(i) + "\n");
                        System.out.print("value: " + arg0.getBase() + "\n");
                    } // end else
                } // end for
            }
        } // end MethodName is not in API list
        return true;
                
                
                /*
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
                
            }*/
        
    }

}

    
