package edu.utsa.androidmt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class ChineseSeg {
    public static void main(String[] args)throws Exception{
	
	Hashtable<String, String> table = new Hashtable<String, String>();
        
        File f = new File("/home/sean/projects/ASE_ML/uitrans/test_final_zh_withgoogle");
        File seg = new File("/home/sean/projects/ASE_ML/uitrans/test_final_zh_withgoogle_seg");
        
        seg.mkdirs();
        String segin = "/home/sean/projects/ASE_ML/temp-seg-in";
        String seginID = "/home/sean/projects/ASE_ML/temp-seg-id";
        String segout = "/home/sean/projects/ASE_ML/temp-seg-out";

        PrintWriter pw = new PrintWriter(new FileWriter(segin));
        PrintWriter pwID = new PrintWriter(new FileWriter(seginID));
        
        for(String name : f.list()){
            BufferedReader in = new BufferedReader(new FileReader(f.getAbsolutePath() + "/" + name));
            for(String line = in.readLine(); line!=null; line = in.readLine()){
        	if(line.startsWith("String_ID:")){
        	    pwID.println(name + "---" + line.substring(10));
        	}else if(line.startsWith("Candidate:")){
        	    pw.println(line.substring(10));
        	}
            }
            in.close();
         }
         pw.close();
         pwID.close();

         ChineseSegment(segin, segout);
         BufferedReader in = new BufferedReader(new FileReader(segout));
         BufferedReader inID = new BufferedReader(new FileReader(seginID));

         for(String line = in.readLine(); line!=null; line = in.readLine()){
             table.put(inID.readLine(), line);
         }
         in.close();
         inID.close();
         
         
         for(String name : f.list()){
             BufferedReader inf = new BufferedReader(new FileReader(f.getAbsolutePath() + "/" + name));
             pw = new PrintWriter(new FileWriter(seg.getAbsolutePath() + "/" + name));
             String id = "";
             for(String line = inf.readLine(); line!=null; line = inf.readLine()){
         	if(line.startsWith("String_ID:")){
         	    id = name + "---" + line.substring(10);
         	}
         	
         	if(line.startsWith("Candidate:")){
         	    pw.println(line.substring(0, 10) + table.get(id));
         	}else{
         	    pw.println(line);
         	}
             }
             inf.close();
             pw.close();
          }

    }
    
    public static void ChineseSegment( String fullName, String segName) throws Exception {
        
        System.setOut(new PrintStream(new FileOutputStream(new File(segName))));
        String filename = fullName;
        
        // setting property
        Properties props = new Properties(); 
        props.setProperty("sighanCorporaDict", "/home/sean/projects/ASE_ML/segmenter/data");
        props.setProperty("serDictionary","/home/sean/projects/ASE_ML/segmenter/data/dict-chris6.ser.gz");
        props.setProperty("testFile", filename);
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");
        
        CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
        segmenter.loadClassifierNoExceptions("/home/sean/projects/ASE_ML/segmenter/data/ctb.gz", props);
        segmenter.classifyAndWriteAnswers(filename);
        
    }
}
