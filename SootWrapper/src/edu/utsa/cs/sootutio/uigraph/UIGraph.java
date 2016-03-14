/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utsa.cs.sootutio.uigraph;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import soot.SootMethod;

/**
 *
 * @author xue
 */
public class UIGraph {
    
    private Hashtable<GraphNode, Set<GraphNode>> flowGraph;
    private Hashtable<Object, GraphNode> objNodeMap;
    public Set<Object> sourceGraph;
    public Set<Object> sinkGraph;
    
    public UIGraph(){
        this.flowGraph = new Hashtable<>();
        this.objNodeMap = new Hashtable<>();
        this.sourceGraph = new HashSet<>();
        this.sinkGraph = new HashSet<>();    
    }
    
    public void addEdge(Object key, Object value, SootMethod cur) {
        
        GraphNode from = this.objNodeMap.get(key);
        GraphNode to = this.objNodeMap.get(value);
        
        if(from == null){
            addNode(key, cur);
            from = this.objNodeMap.get(key);
        }
        if(to == null){
            addNode(value, cur);
            to = this.objNodeMap.get(value);
        }
//        if(flowGraph.containsKey(from) == false) {  // from node is not in the flowGraph
//            HashSet<GraphNode> targets = new HashSet<>();
//            flowGraph.put(from, targets);
//            flowGraph.get(from).add(to);
//        }else{
        flowGraph.get(from).add(to); 
//        }
    }
    
    public void addNode(Object node, SootMethod cur){
        
        GraphNode gn = new GraphNode(node, cur);       
        if(this.objNodeMap.get(node) != null)
            if(this.objNodeMap.get(node).getValue().toString().equalsIgnoreCase(gn.getValue().toString())
                && this.objNodeMap.get(node).getMethod().getName().equalsIgnoreCase(gn.getMethod().getName()) == false) {
                System.out.println("overwrite objNodeMap happen\n");
                System.out.println("overwrite node is: " + node.toString() + "\n");
                System.out.println("origin node maps to: \n");
                System.out.println(this.objNodeMap.get(node).getMethod().getName() + "_" + this.objNodeMap.get(node).getValue().toString() + "\n");
                System.out.println("overwrite node maps to: \n");
                System.out.println(gn.getMethod().getName() + "_" + gn.getValue().toString() + "\n");
            }
        this.objNodeMap.put(node, gn);
        HashSet<GraphNode> targets = new HashSet<>();
        flowGraph.put(gn, targets);
    }
    
    public Hashtable<GraphNode, Set<GraphNode>> getFlowGraph(){
        return this.flowGraph;
    }
    
    
}
