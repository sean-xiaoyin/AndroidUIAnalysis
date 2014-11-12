package edu.utsa.cs.sootutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import soot.PointsToAnalysis;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.ondemand.DemandCSPointsTo;
import soot.jimple.spark.pag.PAG;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.jimple.toolkits.pointer.DumbPointerAnalysis;

public class SootInterBasic {

	public static CallGraph getCallGraph(PointsToAnalysisType type){
 		CallGraphBuilder cbg = new CallGraphBuilder(getPointsToGraph(type).getAnalysis());
		cbg.build();
		return Scene.v().getCallGraph();
	}
	
	public static SootPointerAnalysis getPointsToGraph(PointsToAnalysisType type){
		if(!Scene.v().hasCustomEntryPoints()){
			loadDefaultEntries();			
		}
		HashMap<String, String> map = getDefaultOptions();

		if(type == PointsToAnalysisType.DUMB_CHA){
			return new SootPointerAnalysis(DumbPointerAnalysis.v());
		}else{
			PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
			if(type == PointsToAnalysisType.GEOM){
				if(pta instanceof GeomPointsTo){
					return new SootPointerAnalysis(pta);
				}
				map.put("geom-pta", "true");
			}else if(type == PointsToAnalysisType.SPARK){
				if(pta instanceof PAG && !(pta instanceof GeomPointsTo)){
					return new SootPointerAnalysis(pta);
				}
			}else if(type == PointsToAnalysisType.ONDEMAND){
				if(pta instanceof DemandCSPointsTo){
					return new SootPointerAnalysis(pta);
				}
				map.put("cs-demand", "true");
			}
		}
		SparkTransformer.v().transform("", map);
		return new SootPointerAnalysis(Scene.v().getPointsToAnalysis());
	}
	
	private static void loadDefaultEntries(){
		List<SootMethod> entries = new ArrayList<SootMethod>();
		for(SootClass sc : Scene.v().getApplicationClasses()){
			for (SootMethod sm : sc.getMethods()){
				if(sm.hasActiveBody() && sm.isPublic()){
					entries.add(sm);
				}
			}
		}
		Scene.v().setEntryPoints(entries);
	}
	
	private static HashMap<String, String> getDefaultOptions(){
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("enabled","true");
		map.put("verbose","true");
		map.put("propagator","worklist");
		map.put("simple-edges-bidirectional","false");
		map.put("on-fly-cg","true");
		map.put("set-impl","double");
		map.put("double-set-old","hybrid");
		map.put("double-set-new","hybrid");
		
		return map;
	}
}
