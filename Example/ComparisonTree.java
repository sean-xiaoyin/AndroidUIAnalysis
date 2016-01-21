package org.apache.commons.io;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ComparisonTree {
	static HashMap<String, String> hmap = new HashMap<String, String>();
	static TreeMap<String, String> treemap = new TreeMap<String, String>();
	static int i=0,j=1,count=0;
	
    public static void main(String[] args) throws IOException {
 	
    	JsonParser parser = new JsonParser();
    	
    	File folderSource = new File("1.0.1");
    	File folderLook = new File("1.7.9");
    	
    	
    	File[] listOfFilesOfBase = folderSource.listFiles();
    	File[] listOfFilesCom = folderLook.listFiles();
    	
    	Map<String, File> filemapBase = new HashMap<String,File>();
    	Map<String, File> filemapCom = new HashMap<String,File>();
    	
    	int fileNumBase = listOfFilesOfBase.length;
    	int fileNumCom = listOfFilesCom.length;
    	int methodDiff = fileNumBase-fileNumCom;
    	
    	System.out.println("Number of methods Source: "+fileNumBase);
    	System.out.println("Number of methods Source: "+fileNumCom);
    	
    	System.out.println("Number of different methods: "+Math.abs(methodDiff));
    	
   	 for(File fileBase: listOfFilesOfBase){  		
   		filemapBase.put(fileBase.getName(), fileBase);
   			//System.out.println(fileBase.getName());
   	 	}
   	 for(File fileCom: listOfFilesCom){
   		filemapCom.put(fileCom.getName(), fileCom);
   		 if(!fileCom.getName().toString().endsWith(".text")) continue;
   		 	if(filemapBase.containsKey(fileCom.getName())){
   		 		//System.out.println("Comparing File Name: "+fileCom.getName());
   		 		i++;
   		        File inputBaseFile =filemapBase.get(fileCom.getName());
   		        File inputComFile =filemapCom.get(fileCom.getName());
   		        //String inputCom = fileCom.getName();
   		        
   		        //System.out.println("Comparing File Name: "+inputBaseFile);
   		        //System.out.println("Comparing File Name: "+inputComFile);
   		        
   		        
   		        InputStream inputSourceCom = new FileInputStream(inputBaseFile);
   		        InputStream inputLookBase = new FileInputStream(inputComFile);
   		        //InputStream inputSource = new FileInputStream(file);
   		        
   		        String genreJsonBase = IOUtils.toString(inputSourceCom);
   		        String genreJsonCom = IOUtils.toString(inputLookBase);

   		        String jsonNull = "";
   		        
   		        JsonElement jsonElementBase = parser.parse(new StringReader(genreJsonBase));
   		        JsonElement jsonElementCom = parser.parse(new StringReader(genreJsonCom));
   		        
   		        TreeNode baseRoot= new TreeNode();
   		        TreeNode comRoot= new TreeNode();
   		        
   		        generateJSonBaseTree(jsonElementBase, baseRoot,"rootnode");
   		        generateJSonComTree(jsonElementCom, comRoot,"rootnodess");
   		        
   		        compareTree(baseRoot, comRoot);
   		        //System.out.println("After Changing input file ");
   		        //compareTree(comRoot, baseRoot);     
   		        
   		 	}
   		 	else{
   		 		//System.out.println("Nothing New");	
   		 	}
   	 	}
   	System.out.println("Done");
    }
public static void compareTree(TreeNode baseRoot, TreeNode comRoot){
    	
    	if(baseRoot.isPrimitiveElement()||comRoot.isPrimitiveElement()){
    		//System.out.println("Object final value       "+baseRoot.value+"  "+comRoot.value+"  "+baseRoot.key+"  "+comRoot.key);
    		if((baseRoot.value.equalsIgnoreCase(comRoot.value))&&(baseRoot.key.equalsIgnoreCase(comRoot.key))){
    			//System.out.println("Same");
    		}
    		else{
    			if(!baseRoot.value.toString().equalsIgnoreCase(comRoot.value.toString())){
    				//System.out.println("Value Of the Object are different: "+"Key: "+comRoot.key+"  Value: "+comRoot.value);
    				//System.out.println("Value Of the Object are different: "+"Key: "+baseRoot.key+"  Value: "+baseRoot.value);
    			}
    			else if(!baseRoot.key.toString().equalsIgnoreCase(comRoot.key.toString())){
    				//System.out.println("Key Of the Object are different: "+" Key "+comRoot.key+ " Value " +comRoot.key);
    				//System.out.println("Key Of the Object are different: "+"Key: "+baseRoot.key+"  Value: "+baseRoot.value);
    			}
    			else{
    				//System.out.println(comRoot.key+" "+comRoot.value);
    				//System.out.println(baseRoot.key+" "+baseRoot.value);
    			}   				
    		}
    	}
    	else if(baseRoot.isArrayElement()||comRoot.isArrayElement())
    	{  
    		Set<String> baseArrayList = new HashSet<String>();
    	    Set<String> comArrayList = new HashSet<String>();
    		for(TreeNode baseChildren:baseRoot.arrayChild){
    			baseArrayList.add(baseChildren.getValue());
    		}
    			for(TreeNode comChildren:comRoot.arrayChild){
    				comArrayList.add(comChildren.getValue());
    			}
    			for(String arrayInfo:baseArrayList)
    			{
    				if(!comArrayList.contains(arrayInfo)){
    					System.out.println("Array value missing: "+arrayInfo);
    				}
    			}
    				//compareTree(baseChildren, comChildren);
    	}
    	else
    	{
    		for(String keyBase:baseRoot.childs.keySet()){
    			TreeNode child=baseRoot.childs.get(keyBase);
    			
    			if(comRoot.childs.get(keyBase)!=null){
    				//System.out.println("Children  :"+keyBase);
    				//System.out.println("The Path From the root for child " +keyBase+"   is:   " + child.getParent());
    				compareTree(baseRoot.childs.get(keyBase), comRoot.childs.get(keyBase));
    				//System.out.println("The Path From the root for child " +keyBase+" is: " + child.getParent());
    				//System.out.println("Name of the Children: "+ child.getParent());
    				//System.out.println("Children  :"+keyBase);
    			}else{
    				System.out.println("Not Found  "+keyBase);
    				//System.out.println(" Children: "+ child.getParent());
    				//System.out.println("Different Root value : "+keyBase+"  "+baseRoot.childs.get(keyBase).key+"  "+baseRoot.childs.get(keyBase).value);
    				//compareTree(baseRoot.childs.get(keyBase), comRoot.childs.get(keyBase));
    				//count++;
    				//System.out.println("Number of Object mismatch  "+count);
    			}
    		}
    		
    	}
    	
    }
    public static void generateJSonBaseTree(JsonElement jsonElementBase, TreeNode root, String paraentname) {
        
        // Check whether jsonElement is JsonObject or not 
        if (jsonElementBase.isJsonObject()) {
            Set<Entry<String, JsonElement>> ens = ((JsonObject) jsonElementBase).entrySet();
            //root.setValue("");
            root.setKey(jsonElementBase.toString());
            
            root.setParent(paraentname);
            
            //System.out.println(root.getKey().toString());
            
            if (ens != null) {
                // Iterate JSON Elements with Key values
                for (Entry<String, JsonElement> en : ens) {               	
                	TreeNode child = new TreeNode();
                	if(root.value!=null){
                		child.setParent(root.value.toString());
                //System.out.println(en.getKey());
                	}
                	child.setKey(en.getKey());
                	child.setValue(en.getValue().toString());
                	//child.setParent(parent);
                	root.childs.put(en.getKey().toString(), child);            	
                	generateJSonBaseTree(en.getValue(), child,en.getKey());
                }
            }
        } 
        
        // Check whether jsonElement is Arrary or not
        else if (jsonElementBase.isJsonArray()) {
        			root.setArrayElement(true);
                    JsonArray jarr = jsonElementBase.getAsJsonArray();
                    
                    // Iterate JSON Array to JSON Elements
                    for (JsonElement je : jarr) {
                    	TreeNode child = new TreeNode();
                    	root.arrayChild.add(child);
                    	generateJSonBaseTree(je,child,root.getKey());
                    }
        }
        
        // Check whether jsonElement is NULL or not
        else if (jsonElementBase.isJsonNull()) {
            // print null
            //System.out.println("null");
        } 
        // Check whether jsonElement is Primitive or not
        else if (jsonElementBase.isJsonPrimitive()) {
        	root.setValue(jsonElementBase.getAsString());
        	root.setPrimitiveElement(true);
            // Find and print value as String
            //System.out.println("Value of the Key: "+jsonElement.getAsString());
        } 
        
    }
    public static void generateJSonComTree(JsonElement jsonElementCom, TreeNode root,String paraentname) {
        
        // Check whether jsonElement is JsonObject or not 
        if (jsonElementCom.isJsonObject()) {
            Set<Entry<String, JsonElement>> ens = ((JsonObject) jsonElementCom).entrySet();
            root.setKey(jsonElementCom.toString());
            root.setParent(paraentname);
            
            //System.out.println(root.getKey().toString());
            if (ens != null) {
                // Iterate JSON Elements with Key values
                for (Entry<String, JsonElement> en : ens) {
                	
                	TreeNode child = new TreeNode();
                	if(root.value!=null)
                	{
                	child.setParent(root.value.toString());
                	}
                	else
                	{
                		child.setParent("Root");
                	}
                	child.setKey(en.getKey());
                	child.setValue(en.getValue().toString());
                	root.childs.put(en.getKey().toString(), child);            	
                	generateJSonComTree(en.getValue(), child, en.getKey());
                }
            }
        } 
        
        // Check whether jsonElement is Arrary or not
        else if (jsonElementCom.isJsonArray()) {
        			root.setArrayElement(true);
                    JsonArray jarr = jsonElementCom.getAsJsonArray();
                    
                    // Iterate JSON Array to JSON Elements
                    for (JsonElement je : jarr) {
                    	TreeNode child = new TreeNode();
                    	root.arrayChild.add(child);
                    	generateJSonComTree(je,child,root.getKey());
                    }
        }
        
        // Check whether jsonElement is NULL or not
        else if (jsonElementCom.isJsonNull()) {
            // print null
            //System.out.println("null");
        } 
        // Check whether jsonElement is Primitive or not
        else if (jsonElementCom.isJsonPrimitive()) {
        	root.setValue(jsonElementCom.getAsString());
        	root.setPrimitiveElement(true);
            // Find and print value as String
            //System.out.println("Value of the Key: "+jsonElement.getAsString());
        } 
        
    }
    
}