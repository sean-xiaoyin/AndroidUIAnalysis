package edu.utsa.cs.sootutil;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.util.Chain;
import edu.utsa.cs.latentincomp.util.FileManager;

public class SootClassLoader {

	/**
	 * Load all classes in a list of jar files as Soot Classes
	 * 
	 * @param jarPaths
	 *            Absolute Paths to a list of jar files to be analyzed
	 * @param libDir
	 *            Absolute Path to the support library jar files
	 * @param robust
	 *            Set to true to skip the classes that cannot be loaded
	 * @throws IOException
	 */
	public static Chain<SootClass> loadAllClasses(List<String> jarPaths, String libDir, boolean robust)
			throws IOException {
		initSoot(libDir);
		for (String jarPath : jarPaths){
			loadAllClasses(jarPath, libDir, robust, true);
		}
		Scene.v().loadNecessaryClasses();
		return Scene.v().getApplicationClasses();
	}

	
	/**
	 * Load all classes in a jar file as Soot Classes
	 * 
	 * @param jarPath
	 *            Absolute Path to the jar file to be analyzed
	 * @param libDir
	 *            Absolute Path to the support library jar files
	 * @param robust
	 *            Set to true to skip the classes that cannot be loaded
	 * @throws IOException
	 */
	public static Chain<SootClass> loadAllClasses(String jarPath, String libDir, boolean robust)
			throws IOException {
		return loadAllClasses(jarPath, libDir, robust, false);
	}
	
	
	private static Chain<SootClass> loadAllClasses(String jarPath, String libDir, boolean robust, boolean basicLoaded)
			throws IOException {
		if(!basicLoaded){
			initSoot(libDir);
		}

		System.out.println("Loading Classes for " + jarPath);
		int base = Scene.v().getApplicationClasses().size();

		if (!Scene.v().getSootClassPath().contains(jarPath)) {
			Scene.v().setSootClassPath(
				Scene.v().getSootClassPath() + File.pathSeparator + jarPath);
		}
		int tobeLoad = loadJar(jarPath, robust);
		Chain<SootClass> scs = Scene.v().getApplicationClasses();
		System.out.println((scs.size() - base) + " of " + tobeLoad + " classes loaded");
		if(!basicLoaded){
			Scene.v().loadNecessaryClasses();
		}
		loadClassBodies(scs);
		return scs;
	}


	private static void initSoot(String libDir) {
		addJarsToClassPath(libDir);
		Options.v().set_keep_line_number(true);
		Options.v().set_whole_program(true);
	}

	private static void addJarsToClassPath(String libDir) {
		HashSet<String> libJars = new HashSet<String>();
		FileManager.recursiveFetchFile(libDir, libJars, ".jar");
		String classPath = Scene.v().getSootClassPath();
		for (String jar : libJars) {
			classPath = classPath + File.pathSeparator + jar;
		}
		Scene.v().setSootClassPath(classPath);
	}

	private static int loadJar(String jarPath, boolean robust) throws IOException {
		File file = new File(jarPath);

		JarFile jar = new JarFile(file);
		Enumeration<JarEntry> e = jar.entries();
		int count = 0;
		while (e.hasMoreElements()) {
			JarEntry entry = e.nextElement();
			if (entry.isDirectory() || !entry.getName().endsWith(".class"))
				continue;
			count = count + 1;

			String name = entry.getName().substring(0,
					entry.getName().length() - 6);
			name = name.replace('/', '.');
//			System.out.println("Loading class: " + name);
			loadClass(name, robust);
		}		
		jar.close();
		return count;
	}

	private static void loadClass(String name, boolean robust) {
		try {
			SootClass c = Scene.v().loadClass(name, SootClass.BODIES);

			c.setApplicationClass();
		} catch (RuntimeException e) {
			String sms = e.getMessage();
			if(sms.indexOf("but") !=-1 && sms.indexOf(" is at ")!=-1){
				String className = sms.substring(sms.indexOf("but") + 3,
						sms.indexOf("is at ")).trim();
				Scene.v().addBasicClass(className, SootClass.SIGNATURES);
				loadClass(name, robust);
			}else{
				System.err.println(sms);
				if(!robust){
					throw e;
				}
			}
		}
	}
	
	private static void loadClassBodies(Chain<SootClass> classes){
		for(SootClass sc : classes){
			Iterator<SootMethod> mi = sc.getMethods().iterator();
			while (mi.hasNext()) {
				SootMethod sm = (SootMethod) mi.next();
				if (sm.isConcrete()) {
					sm.retrieveActiveBody();
				}
			}
		}
	}
}