/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utsa.cs.sootutil;

import edu.utsa.cs.sootutil.visitors.FlowCheck;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import soot.SootMethod;

/**
 *
 * @author xue
 */
public class FlowCheckStmt {
    
    public static Set<FlowCheck> fcLoader( String path ) throws Exception {        
        
        File filer = new File(path);
        FileReader fileReader = new FileReader(filer);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        
        FlowCheck fc = new FlowCheck();
        Set<FlowCheck> fcSet = new HashSet<>();
        int n  = 0;
        while ((line = bufferedReader.readLine()) != null) {
            n = n + 1;
            if ( n % 5 == 1){
                fc.setclassName(line);
            }
            if ( n % 5 == 2){
                fc.setmethodName(line);
            }
            if ( n % 5 == 3){
                fc.setparaList(line);
            }
            if ( n % 5 == 4){
                fc.setoperations(line);
            }
            if ( n % 5 == 0){
                fcSet.add(fc);
                fc = new FlowCheck();
            }
            
        }
        
        fileReader.close();
        
        return fcSet;
        
    }
    

    public static FlowCheck fcMatch(SootMethod sm, Set<FlowCheck> fcSet) {
           
        for(FlowCheck fc : fcSet) {            
            //match className
            if(sm.getDeclaringClass().getName().equalsIgnoreCase(fc.className)) {
                // match methodName
                if (sm.getName().equalsIgnoreCase(fc.methodName)) {
                    //match paraList size
                    if(sm.getParameterCount() == fc.paraList.size()) {
                        if(sm.getName().equalsIgnoreCase("SetText"))
                           System.out.println("SetText\n");
                        // match paraList                   
                        for (int i = 0; i < fc.paraList.size(); i++) {
                            if (sm.getParameterTypes().get(i).toString().equalsIgnoreCase(fc.paraList.get(i))){
                                System.out.print("ClassName: " + fc.className + " " + sm.getDeclaringClass().getName() + "\n");
                                System.out.print("MethodName: " + sm.getName() + " " + fc.methodName + "\n");
                                System.out.print("ParaCount: " + sm.getParameterCount() + " " + fc.paraList.size() + "\n");
                                System.out.print("ParaTypes: " + sm.getParameterTypes() + "\n");
                                System.out.print("ParaTypes: " + fc.paraList + "\n"); 
                                return fc;
                            }
                        }
                     }
                }
            }
        }
        return null;  //not found
    }
    
    public static boolean fcMatchMethodName(SootMethod sm, Set<FlowCheck> fcSet) {
        for(FlowCheck fc : fcSet) {            
            //match className
            if(sm.getDeclaringClass().getName().equalsIgnoreCase(fc.className)) {
                // match methodName
                if (sm.getName().equalsIgnoreCase(fc.methodName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
