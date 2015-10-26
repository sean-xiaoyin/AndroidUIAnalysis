package edu.utsa.cs.sootutil.driver;

import java.io.IOException;

import soot.SootClass;
import soot.util.Chain;
import edu.utsa.cs.sootutil.SootClassLoader;
import edu.utsa.cs.sootutil.visitors.InstanceOfVisitor;
import edu.utsa.cs.sootutil.visitors.SootVisitor;

public class Main {
	public static void main(String[] args) throws IOException, Exception{
		Chain<SootClass> classes = SootClassLoader.loadAllClasses("/home/xue/Documents/pro_layout.jar", "/home/xue/AndroidUIAnalysis/AndroidLib", true);
		InstanceOfVisitor isv = new InstanceOfVisitor();
		SootVisitor.visitAll(classes, isv);
		
	}
}
