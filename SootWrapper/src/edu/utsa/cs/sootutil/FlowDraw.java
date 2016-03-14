/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utsa.cs.sootutil;

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
    
    public static boolean DrawFlowGraph (FileWriter fileWriter, Hashtable<Object, Set<Object>> flowGraph) throws IOException {

        for(Object k : flowGraph.keySet()) {
            for(Object v : flowGraph.get(k)) {
                fileWriter.write("\t\"" + k.toString() + "\" -> " );
                fileWriter.write("\"" + v.toString() + "\";\n");
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
                fileWriter.write("\"" + k.toString() + "\" -> " );
            else
                fileWriter.write("\"" + k.toString() + "\";\n");
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
                fileWriter.write("\"" + k.toString() + "\" -> " );
            else
                fileWriter.write("\"" + k.toString() + "\";\n");
            i++;
        }
        fileWriter.write("\t\tlabel = \"Source\";\n\t}\n");
        
        return true;
        
    }
    
    public static boolean DrawParaTable (FileWriter fileWriter, Hashtable<SootMethod, List<ParameterRef>> paraTable) throws IOException {

        for(SootMethod k : paraTable.keySet()) {
            for(ParameterRef v : paraTable.get(k)) {
                fileWriter.write("\t\"" + k.toString() + "\" -> " );
                fileWriter.write("\"" + v.toString() + "\";\n");
            }
        }

        return true;
    }
    
    public static boolean DrawWholeGraph (FileWriter fileWriter, UIFlowVisitor uv) throws IOException {
        
        fileWriter.write("digraph G {\n\n");
        DrawSinkGraph(fileWriter, uv.sinkGraph);
        DrawSourceGraph(fileWriter, uv.sourceGraph);
        DrawFlowGraph(fileWriter,uv.flowGraph);
        DrawParaTable(fileWriter, uv.paraTable);
        fileWriter.write("}");
        return true;
    }

       
}
