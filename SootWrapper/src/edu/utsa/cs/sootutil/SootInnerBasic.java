package edu.utsa.cs.sootutil;

import soot.SootMethod;
import soot.toolkits.graph.pdg.EnhancedBlockGraph;

public class SootInnerBasic {
	public static EnhancedBlockGraph generateCFG(SootMethod sm){
		return new EnhancedBlockGraph(sm.getActiveBody());
	}
}
