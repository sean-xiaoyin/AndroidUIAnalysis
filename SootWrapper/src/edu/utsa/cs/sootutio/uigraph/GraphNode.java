/*
 * GraphNode is the node of UI Graph
 * It contains the value of the code and the current sootmethod of it
 */
package edu.utsa.cs.sootutio.uigraph;

import soot.SootMethod;

/**
 *
 * @author xue
 */
public class GraphNode {
    
    private Object value;
    private SootMethod cur;
    
    public GraphNode(Object value, SootMethod cur){
        this.value = value;
        this.cur = cur;
    }
    
    public Object getValue(){
        return this.value;
    }
    
    public SootMethod getMethod(){
        return this.cur;
    }
    
}
