package edu.utsa.cs.sootutil.driver;

import java.io.IOException;

import soot.SootClass;
import soot.util.Chain;
import edu.utsa.cs.sootutil.SootClassLoader;
import edu.utsa.cs.sootutil.visitors.InstanceOfVisitor;
import edu.utsa.cs.sootutil.visitors.SootVisitor;

public class Main {
	public static void main(String[] args) throws IOException, Exception{
		Chain<SootClass> classes = SootClassLoader.loadAllClasses("/home/xue/Documents/LayoutTest.jar", "/home/xue/AndroidUIAnalysis/AndroidLib", true);
		InstanceOfVisitor isv = new InstanceOfVisitor();
                String API_path = "/home/xue/Documents/FlowGraph/API_graph_v4.txt";
		SootVisitor.visitAll(classes, isv, API_path);
		
	}
}
