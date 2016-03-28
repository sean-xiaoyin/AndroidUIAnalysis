/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utsa.cs.sootutio.uigraph;

import edu.utsa.cs.sootutil.visitors.Parameter;
import edu.utsa.cs.sootutil.visitors.UIFlowVisitor;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import soot.SootMethod;
import soot.jimple.ParameterRef;

/**
 *
 * @author xue
 */
public class FlowDraw {
       
    public static boolean DrawFlowGraph (FileWriter fileWriter, UIGraph graph) throws IOException {

        Hashtable<GraphNode, Set<GraphNode>> flowGraph = graph.getFlowGraph();
        for(GraphNode k : flowGraph.keySet()) {
            for(GraphNode v : flowGraph.get(k)) {
                fileWriter.write("\t\"" + k.getValue().toString().replaceAll("\"", "") + "--" + k.getMethod().getName().replaceAll("\"", "") + "\" -> " );
                fileWriter.write("\"" + v.getValue().toString().replaceAll("\"", "") + "--" + v.getMethod().getName().replaceAll("\"", "") + "\";\n");
            }
        }

        return true;
    }
    
    public static boolean DrawSinkGraph (FileWriter fileWriter, Set<Object> sinkGraph) throws IOException {
        
        int i = 0;
        fileWriter.write("\tsubgraph cluster_0 {\n");
        fileWriter.write("\t\tstyle=filled;\n");
        fileWriter.write("\t\tcolor=lightgrey;\n");
        fileWriter.write("\t\tnode [style=filled,color=white];\n\t\t");
        for(Object k : sinkGraph) {
            if ( i != (sinkGraph.size() - 1))
                fileWriter.write("\"" + k.toString().replaceAll("\"", "") + "\" -> " );
            else
                fileWriter.write("\"" + k.toString().replaceAll("\"", "") + "\";\n");
            i++;
        }
        fileWriter.write("\t\tlabel = \"Sink\";\n\t}\n");
        
        return true;
    }
    
    public static boolean DrawSourceGraph (FileWriter fileWriter, Set<Object> sourceGraph) throws IOException {
        
        int i = 0;
        fileWriter.write("\tsubgraph cluster_1 {\n");
        fileWriter.write("\t\tnode [style=filled];\n\t\t");
        for(Object k : sourceGraph) {
            if ( i != (sourceGraph.size() - 1))
                fileWriter.write("\"" + k.toString().replaceAll("\"", "") + "\" -> " );
            else
                fileWriter.write("\"" + k.toString().replaceAll("\"", "") + "\";\n");
            i++;
        }
        fileWriter.write("\t\tlabel = \"Source\";\n\t}\n");
        
        return true;
        
    }
    
    public static boolean DrawArgIndexTable (FileWriter fileWriter, Hashtable<GraphNode, GraphNode> ArgIndexTable) throws IOException {

        for(GraphNode k : ArgIndexTable.keySet()) {
            GraphNode v = ArgIndexTable.get(k);
            fileWriter.write("\t\"" + k.getValue().toString().replaceAll("\"", "") + "--" + k.getMethod().getName().replaceAll("\"", "") + "\" -> " );
            fileWriter.write("\"" + v.getValue().toString().replaceAll("\"", "") + "--" + v.getMethod().getName().replaceAll("\"", "")  + "\";\n");
            
        }

        return true;
    }
    
    public static boolean DrawWholeGraph (FileWriter fileWriter, UIFlowVisitor uv) throws IOException {
        
        fileWriter.write("digraph G {\n\n");
        DrawSinkGraph(fileWriter, uv.graph.sinkGraph);
        DrawSourceGraph(fileWriter, uv.graph.sourceGraph);
        DrawFlowGraph(fileWriter,uv.graph);
        DrawArgIndexTable (fileWriter, uv.ArgIndexTable);
        fileWriter.write("}");
        return true;
    }

       
}
