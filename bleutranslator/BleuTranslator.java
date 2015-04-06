/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bleutranslator;


import java.util.*;
import java.io.*;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 *
 * @author Xue
 */
public class BleuTranslator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws Exception{
        
        //boolean Chinese = true;
        //File f = new File("C:/Users/Xin/Documents/opt/allign-zh-trans");
        //File out = new File("C:/Users/Xin/Documents/opt/out-zh");
        boolean Chinese = false;
        File f = new File("C:/Users/Xin/Documents/optest/allign-es-trans");
        File out = new File("C:/Users/Xin/Documents/optest/out-es");
        File seg = new File("C:/Users/Xin/Documents/optest/segmented-zh");
        
        out.mkdirs();
        seg.mkdirs();

        for(String name : f.list()){

            String fullName = f.getAbsolutePath() + "/" + name;
            String outName = out.getAbsolutePath() + "/" + name;
            
            
            if ( Chinese == true ){
                
                String segName = seg.getAbsolutePath() + "/" + name;               
                ChineseSegment(fullName, segName);
                BlEUClaculatorOnefile(segName, outName, Chinese);
            }
            else {
                
                BlEUClaculatorOnefile(fullName, outName, Chinese);
                
            }
            
            
            
         }
    }
    
    public static void BlEUClaculatorOnefile( String fullName, String outName, boolean Chinese ) throws Exception {        
        
        File filer = new File(fullName);
        File filew = new File(outName);
	FileReader fileReader = new FileReader(filer);
        FileWriter fileWriter = new FileWriter(filew,true);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
	String line, ID, Eng, Ref, Can, Score;
        TransSet set = new TransSet();
        int n  = 0;
	while ((line = bufferedReader.readLine()) != null) {
                            
            n = n + 1;
            if ( n % 5 == 1){
                set.setStringID(line);
            }
            else if ( n % 5 == 2){
                set.setEnglish(line);
            }
            else if ( n % 5 == 3){
                set.setReference(line, Chinese);
            }
            else if ( n % 5 == 4){
                set.setCandidate(line, Chinese);
            }
            else if ( n % 5 == 0){
                                
                fileWriter.write("String_ID:" + set.String_ID + '\n');
                fileWriter.write("English:" + set.English + '\n');
                fileWriter.write("Reference:" + set.Reference.toString() + '\n');
                fileWriter.write("Candidate:" + set.Candidate + '\n');
                fileWriter.write("BlEUscore:" + String.valueOf(set.BlEUClaculator()) + '\n');
                fileWriter.write(line + '\n');
                                
            }
			
	}
        fileWriter.flush();
	fileWriter.close();
	fileReader.close();
        
    }
        
    public static void ChineseSegment( String fullName, String segName) throws Exception {
    
        
        System.setOut(new PrintStream(new FileOutputStream(new File(segName))));
        String filename = fullName;
        
        // setting property
        Properties props = new Properties(); 
        props.setProperty("sighanCorporaDict", "C:/Users/Xin/Documents/NetBeansProjects/BleuTranslator/data");
        props.setProperty("serDictionary","C:/Users/Xin/Documents/NetBeansProjects/BleuTranslator/data/dict-chris6.ser.gz");
        props.setProperty("testFile", filename);
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");
        
        CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
        segmenter.loadClassifierNoExceptions("C:/Users/Xin/Documents/NetBeansProjects/BleuTranslator/data/ctb.gz", props);
        segmenter.classifyAndWriteAnswers(filename);
        
    }
    
}
