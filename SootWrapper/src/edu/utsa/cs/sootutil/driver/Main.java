package edu.utsa.cs.sootutil.driver;

import java.io.IOException;

import soot.SootClass;
import soot.util.Chain;
import edu.utsa.cs.sootutil.SootClassLoader;
import edu.utsa.cs.sootutil.visitors.InstanceOfVisitor;
import edu.utsa.cs.sootutil.visitors.SootVisitor;

public class Main {
	public static void main(String[] args) throws IOException{
		Chain<SootClass> classes = SootClassLoader.loadAllClasses("/home/sean/AndroidUIAnalysis/Jars/1000_com.slacker.radio-dex2jar.jar", "/home/sean/AndroidUIAnalysis/AndroidLib", true);
		InstanceOfVisitor isv = new InstanceOfVisitor();
		SootVisitor.visitAll(classes, isv);
		
	}
}
