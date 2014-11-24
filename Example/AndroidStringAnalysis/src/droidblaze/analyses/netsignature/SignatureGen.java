package droidblaze.analyses.netsignature;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.util.Chain;
import dk.brics.automaton.Automaton;
import eruiz.analysis.parser.SampleAnalysis;

public class SignatureGen {
	private static int SOOT_TRIAL_UPPERBOUND = 10;
	
	public static void main(String args[]){
		args = new String[2];
		
		args[0] = "/Users/eruiz/AndroidUI/AndroidUIAnalysis/Jars/1310178647_com.bejoy.minipaint-dex2jar.jar";
        args[1] = "-lib=/Users/eruiz/AndroidUI/AndroidUIAnalysis/Example/AndroidStringAnalysis/lib";
		if(args.length==0||args.length>2){
			System.err.println("Usage: java SignatureGen jarfile [-lib=libdir]");
			System.exit(0);
		}
		Options.v().set_keep_line_number(true);
		Options.v().set_whole_program(true);

		if(args.length==2){
			String analysisLib = args[1];
			if(!analysisLib.startsWith("-lib=")){
				System.err.println("Usage: java SignatureGen jarfile [-lib=libdir]");
				System.exit(0);
			}else{
//				sigGenLog.info("Loading libraries...");
				String libdir = analysisLib.substring(5);
				addJarsToClassPath(libdir);
			}
		}
		String analysisJar = args[0];		
		if(analysisJar.endsWith(".jar")){
			try{
//				sigGenLog.info("Loading analysis targets...");
//				String harnessJar = analysisJar.replaceAll(".jar", ".harness.jar");
//		        if (!Scene.v().getSootClassPath().contains(harnessJar)) {
//		            Scene.v().setSootClassPath(Scene.v().getSootClassPath() + File.pathSeparatorChar + harnessJar);
//		        }
		        if (!Scene.v().getSootClassPath().contains(analysisJar)) {
		            Scene.v().setSootClassPath(Scene.v().getSootClassPath() + File.pathSeparatorChar + analysisJar);
		        }
//		        	loadJar(harnessJar);
		        	loadJar(analysisJar);
			}catch(IOException e){
				System.err.println("Invalid Jar files for soot");
				System.exit(0);
			}
		}else{
			System.err.println("Can generate signatures for only Jar files (.jar)");
			System.exit(0);
		}
		Chain<SootClass> scs = Scene.v().getApplicationClasses();
		String filename = args[0].substring(args[0].indexOf("Jars/", 6), args[0].length());
		String fileArr[] = filename.split("/");
		filename = fileArr[1];
		System.out.println("FILENAME " + filename);
		
		try {
			PrintWriter writer = new PrintWriter("/Users/eruiz/Documents/AndroidUI/AndroidUIAnalysis/CallGraph/CG_" + filename + ".txt", "UTF-8");
			filename = "/Users/eruiz/Documents/AndroidUI/AndroidUIAnalysis/CallGraph/CG_" + filename + ".txt";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SampleAnalysis sample = new SampleAnalysis(filename);
		Iterator<SootClass> itClass  = scs.iterator();
		int count = 1;
		while(itClass.hasNext()){
			SootClass sclass = itClass.next();
			sample.analyzeCalls(sclass);
			count++;
//			if(count > 1){
//				break;
//			}
		}
//		sigGenLog.info("Locating network API calls...");
//		NetAPILocator locator = new NetAPILocator();
//		NetAPI.initiate();
//		List<NetAPIInvoke> netArguments = locator.locate(scs, NetAPI.NetAPItable);
		
//		sigGenLog.info("Starting Analysis for Signature Generation...");
//		JSADriver driver = new JSADriver(scs, locator.getMethodReturnTable());
//		int index = analysisJar.lastIndexOf('/');
//		String outputPath = analysisJar.substring(0, index)+"/output/"+analysisJar.subSequence(index+1, analysisJar.lastIndexOf('.'));
		
//		List<Automaton> automatons = driver.drive(netArguments, outputPath);
		
//		output(automatons, outputPath);
	}
	
	private static void output(List<Automaton> automatons, String outputPath) {
		// TODO Auto-generated method stub
		try{
			String filePath = outputPath;   
			File myFilePath = new File(filePath);  
			if (!myFilePath.exists()) {  
			     myFilePath.mkdirs();  
			}
			
			int count = 0;
			for(Automaton at:automatons){
				if(at.getStates().size()>1){
					count++;
					PrintWriter pw = new PrintWriter(new FileWriter(outputPath+"/aut"+count+".dot"));
					pw.print(at.toDot());
					pw.close();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static boolean addJarsToClassPath(String libDir) {
        File lib = new File(libDir);
        File[] jars = lib.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && file.getName().toLowerCase().endsWith(".jar");
            }
        });
        StringBuilder b = new StringBuilder(Scene.v().getSootClassPath());
        for (File jar : jars) {
        //	System.out.println("JAR NAME: " + jar.getName());
            b.append(File.pathSeparator);
            b.append(jar.getPath());
        }
        Scene.v().setSootClassPath(b.toString());
        return (jars.length > 0);
    }
	
	public static void loadJar(String jarPath) throws IOException {
        File file = new File(jarPath);
        
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();
         //   System.out.println("ENTRY HERE: " + entry.getName());
            if (entry.isDirectory() || !entry.getName().endsWith(".class"))
                continue;
                
            String name = entry.getName().substring(0, entry.getName().length() - 6);
            name = name.replace('/', '.');
            tryLoadClass(name, SignatureGen.SOOT_TRIAL_UPPERBOUND);        
        }
    }
	
	public static void tryLoadClass(String name, int trials) {
		while(trials > 0){
			try{
				loadClass(name);
				break;
			}catch(RuntimeException e){
				trials = trials - 1;
				String sms = e.getMessage();
				String className = sms.substring(sms.indexOf("but") + 3, sms.indexOf("is a")).trim();
				try{
					Scene.v().addBasicClass(className, SootClass.SIGNATURES);
					continue;
				}catch(RuntimeException e1){
					System.out.println(e1.getMessage());   
				}
			}
		}
    }

	private static void loadClass(String name) {
		SootClass c = Scene.v().loadClass(name, SootClass.BODIES);
		System.out.println(name);   // PRINTS PACKAGE NAME
		Scene.v().loadNecessaryClasses();
		
		c.setApplicationClass();
		Iterator<SootMethod> mi = c.getMethods().iterator();
		while (mi.hasNext()) {
		    SootMethod sm = (SootMethod) mi.next();
		    if (sm.isConcrete()) {
		        sm.retrieveActiveBody();
		    }
		}
	}

}
