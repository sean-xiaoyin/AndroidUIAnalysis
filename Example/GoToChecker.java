package main.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import analysis.graph.MethodUtil;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.LongType;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.JastAddJ.SwitchStmt;
import soot.baf.SpecialInvokeInst;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.toolkits.graph.LoopNestTree;
import soot.util.Chain;

public class GoToChecker {

	public static void main(String[] args) {

		Options.v().set_soot_classpath(
			/*	Scene.v().defaultClassPath() + File.pathSeparator+ "/home/alamin/Desktop/Maven/Joda-time/joda-time-2.7/target/joda-time-2.7.jar"+File.pathSeparator
						+ "/home/alamin/Desktop/Maven/Joda-time/joda-time-2.7/target/test-classes"+ File.pathSeparator+"/home/alamin/Desktop/MSThesis/joda-convert-1.7.jar");
		
		Options.v().set_keep_line_number(true);
		String[] sootArgs = {"-pp", "-process-dir",
				"/home/alamin/Desktop/Maven/Joda-time/joda-time-2.7/target/test-classes", "-output-jar", "-d",
				"/home/alamin/Desktop/Output/joda-time/2.71.jar" };

				Scene.v().defaultClassPath() + File.pathSeparator+ "/home/alamin/Desktop/Maven/jsoup/jsoup-jsoup-1.7.3/target/jsoup-1.7.3.jar"+File.pathSeparator
				+ "/home/alamin/Desktop/Maven/jsoup/jsoup-jsoup-1.6.3/target/test-classes"+ File.pathSeparator+"/home/alamin/Desktop/MSThesis/hamcrest-all-1.3.jar");

		Options.v().set_keep_line_number(true);
		String[] sootArgs = {"-pp", "-process-dir","/home/alamin/Desktop/Maven/jsoup/jsoup-jsoup-1.7.3/target/test-classes", "-output-jar","-d","/home/alamin/Desktop/Instrumented File/Jsoup/1.7.3.jar" };
		
				Scene.v().defaultClassPath() + File.pathSeparator+ "/home/alamin/Desktop/Maven/felix/felix4.6/target/org.apache.felix.framework-4.6.0.jar"+File.pathSeparator
				+ "/home/alamin/Desktop/Maven/felix/felix4.6/target/test-classes"+File.pathSeparator+"/home/alamin/Desktop/MSThesis/com.springsource.org.easymock-2.5.1.jar"+File.pathSeparator
				+"/home/alamin/Desktop/MSThesis/mockito-core-1.7.jar"+ File.pathSeparator +"/home/alamin/Desktop/MSThesis/hamcrest-all-1.3.jar"+File.pathSeparator
				+"/home/alamin/Desktop/MSThesis/asm-3.1.jar"+File.pathSeparator+"/home/alamin/Desktop/MSThesis/asm-tree-3.0.jar");
				
				
Options.v().set_keep_line_number(true);
String[] sootArgs = {"-pp", "-process-dir","/home/alamin/Desktop/Maven/felix/felix4.6/target/test-classes", "-output-jar","-d","/home/alamin/Desktop/Instrumented File/Felix/4.6.0.1.jar" };
		*/

				Scene.v().defaultClassPath() + File.pathSeparator+ "/home/alamin/Desktop/Maven/Bukkit/Bukkit-1.1-R1/target/original-bukkit-1.1-R1.jar"+File.pathSeparator
				+ "/home/alamin/Desktop/Maven/Bukkit/Bukkit-1.1-R1/target/test-classes"+File.pathSeparator+"/home/alamin/Desktop/MSThesis/log4j-1.2.13.jar"+ File.pathSeparator 
				+"/home/alamin/Desktop/MSThesis/hamcrest-all-1.3.jar");
				
				
		Options.v().set_keep_line_number(true);
		String[] sootArgs = {"-pp", "-process-dir","/home/alamin/Desktop/Maven/Bukkit/Bukkit-1.1-R1/target/test-classes", "-output-jar","-d","/home/alamin/Desktop/Instrumented File/Bukkit/1.1.1.jar" };

		GotoInstrumenter instrumenter = GotoInstrumenter.getInstance();
		PackManager.v().getPack("jtp").add(new Transform("jtp.instrumenter", instrumenter));

		// Just in case, resolve the PrintStream and System SootClasses.
		Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
       
		soot.Main.main(sootArgs);
		
	}
}

class GotoInstrumenter extends BodyTransformer {
	private static GotoInstrumenter instance = new GotoInstrumenter() ;
	static Map<String, Integer> indexMap;
	
	static SootClass  dumperClass;
	static SootMethod reportDump;
	static {
		dumperClass = Scene.v().loadClassAndSupport("main.test.Dumper");
		reportDump = dumperClass.getMethod("void dump(java.lang.Object,java.lang.String)");
		
			}

	private GotoInstrumenter() {
	//	indexMap = MapIndexLoader.getInstance().indexMap;		
		
	}

	public static GotoInstrumenter getInstance() {
		if(instance==null){
			instance = new GotoInstrumenter();
		}
		return instance;
	}

	protected void internalTransform(Body body, String phaseName, Map options) {
	
		
	
	   if(body.getMethod().isAbstract()) return;
	   if(!isComplexMethod(body)) return;
		SootClass sClass = body.getMethod().getDeclaringClass();
		SootField gotoCounter = null;
		boolean addedLocals = false;
		Local tmpRef = null, tmpLong = null;
		Chain units = body.getUnits();
	
		if(body.getMethod().getName().equalsIgnoreCase("<init>")|| body.getMethod().getName().equalsIgnoreCase("<clinit>")
				||(!body.getMethod().getSubSignature().startsWith("void test"))){
			System.out.println(body.getMethod().getSubSignature());
			return;
		}
	
		{
			boolean isMainMethod = body.getMethod().getSubSignature().startsWith("test");
			
			String packageName = body.getMethod().getDeclaringClass().getJavaPackageName();
			String testClassName = body.getMethod().getDeclaringClass().getName();
			String methodName = body.getMethod().getName(); 
			
			Local referenceOfObject = getReferenceOfObject(body, testClassName);
			
			if(referenceOfObject==null)
				return;
			InvokeExpr recordDumpExp = Jimple.v().newStaticInvokeExpr(reportDump.makeRef(), referenceOfObject,StringConstant.v(methodName));
			Iterator stmtIt = units.snapshotIterator();
			
			int i = 0;
			Stmt prev = null;
			while (stmtIt.hasNext()) {

				Stmt s = (Stmt) stmtIt.next();

				if (s instanceof InvokeStmt) {
					InvokeExpr iexpr = (InvokeExpr) ((InvokeStmt) s).getInvokeExpr();
					if (iexpr instanceof StaticInvokeExpr) {
						SootMethod target = ((StaticInvokeExpr) iexpr).getMethod();

					}
				} else if (!isMainMethod && (s instanceof ReturnStmt || s instanceof ReturnVoidStmt)) {
					
					Stmt dumpStmt = Jimple.v().newInvokeStmt(recordDumpExp);
				    units.insertBefore(dumpStmt, s);
					

				} else if (isMainMethod && (s instanceof ReturnStmt || s instanceof ReturnVoidStmt)) {

					Stmt dumpStmt = Jimple.v().newInvokeStmt(recordDumpExp);
				    units.insertBefore(dumpStmt, s);
										

				}
				i++;

				prev = s;

			}
		}
	}
	
	String getObjectType(String className, String packageName){
		className =className.replace("Test", "");
		String[] split = className.split("_");
		return packageName+"."+split[0];
	}
	
	
	Local getReferenceOfObject(Body body, String objectType){
		Chain<Local> locals = body.getLocals();
		for (Local local : locals) {
			
			if(local.getType().toString().equalsIgnoreCase(objectType)){
				
				return local;			
			}
		}
		return null;
	}
	boolean isComplexMethod(Body body) {

		PatchingChain<Unit> units = body.getUnits();
		Iterator stmtIt = units.snapshotIterator();

		while (stmtIt.hasNext()) {
			Stmt s = (Stmt) stmtIt.next();
			if (s instanceof InvokeStmt) {
				return true;
			}
			

			if (s instanceof DefinitionStmt) {

				Value lhsOp = ((DefinitionStmt) s).getLeftOp();
				Value rhsOp = ((DefinitionStmt) s).getRightOp();
				if (rhsOp instanceof InvokeExpr) {
					return true;

				}
			}

		}

		Chain<Trap> traps = body.getTraps();
		for (Trap trap : traps) {
			return true;

		}

		return false;

	}
	
	private boolean addedFieldToMainClassAndLoadedPrintStream = false;
	private SootClass javaIoPrintStream;

	private Local addTmpRef(Body body, int i) {
		Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
		body.getLocals().add(tmpRef);
		return tmpRef;
	}

	private Local addTmpLong(Body body) {
		Local tmpLong = Jimple.v().newLocal("tmpLong", LongType.v());
		body.getLocals().add(tmpLong);
		return tmpLong;
	}

	private void addCallRecorder(Chain units, Stmt rcallStmt) {
		Iterator iterator = units.snapshotIterator();
		while (iterator.hasNext()) {
			Stmt prev = (Stmt) iterator.next();
			if (prev instanceof IdentityStmt) {
 
			} else {
				 if(prev instanceof IfStmt ||prev instanceof InvokeStmt || prev instanceof GotoStmt || prev instanceof ReturnStmt || prev instanceof ThrowStmt || prev instanceof ReturnVoidStmt){  
					if(prev instanceof InvokeStmt){
						if(prev.getInvokeExpr() instanceof SpecialInvokeExpr){
							//System.out.println(prev.toString());
							 units.insertAfter(rcallStmt, prev); 
						}else
							 units.insertBefore(rcallStmt, prev); 
						break;
						
					}else
					 units.insertBefore(rcallStmt, prev); 
					break;
				 }else if(prev instanceof NopStmt){
					 units.insertAfter(rcallStmt, prev); 
				 }else if(prev instanceof AssignStmt){
					
				
				    units.insertBefore(rcallStmt, prev); 
					break;
						 
						
					 
				 }
				 
				
			}
		}

	}

	private Map<Stmt, Boolean> getLoopStmt(Body body , Set<Unit> traps) {
		LoopNestTree loopNestTree = new LoopNestTree(body);
		Map<Stmt, Boolean> loops = new HashMap<Stmt, Boolean>();
		for (Loop loop : loopNestTree) {
			
			Stmt head = loop.getHead();
			if(!traps.contains(head)){
			loops.put(head, true);
			}

		}
		return loops;
	}

	private void insertIdentityStmts(Body body) {
		int i = 0;

		Iterator parIt = body.getMethod().getParameterTypes().iterator();
		while (parIt.hasNext()) {
			Type t = (Type) parIt.next();
			Local l = Jimple.v().newLocal("parameter" + i, t);
			body.getLocals().add(l);
			body.getUnits().addFirst(Jimple.v().newIdentityStmt(l, Jimple.v().newParameterRef(l.getType(), i)));
			i++;
		}

		// add this-ref before everything else
		if (!body.getMethod().isStatic()) {
			Local l = Jimple.v().newLocal("this", RefType.v(body.getMethod().getDeclaringClass()));
			body.getLocals().add(l);
			body.getUnits().addFirst(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef((RefType) l.getType())));
		}
	}
}
