/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bleutranslator;

import java.util.*;
import edu.stanford.nlp.mt.metrics.BLEUMetric;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Xue
 */
public class TransSet {
    
    String String_ID;
    String English;
    String Reference;
    String Candidate;
    double BLEUscore;
    
    // constructor
    TransSet() {
        String_ID = " N/A ";
        English = " N/A ";
        Reference = " N/A ";
        Candidate = " N/A ";
        BLEUscore = 0;
    }
    
    String setStringID ( String id ) {
        
        String_ID = id.substring(10);
        return String_ID;
        
    }
    
    String setEnglish ( String eng ) {
        
        English = RegexMatches(eng.substring(8));
        return English;
        
    }
    
    String setReference ( String ref, boolean Chinese ) {
        
        if ( Chinese == false ){
            Reference = RegexMatches(ref.substring(8).toLowerCase());
        }
        else {
            Reference = ref.substring(8);
        }
        
        return Reference;
        
    }
    
    String setCandidate ( String candi, boolean Chinese ) {
        
        if ( Chinese == false) {
            Candidate = RegexMatches(HTMLFilter(candi.substring(11).toLowerCase()));
        }
        else {
            Candidate = HTMLFilter(candi.substring(11));
        }
        
        return Candidate;
        
    }
    
    double BlEUClaculator (){
        
        return BLEUMetric.computeLocalSmoothScore(Candidate, Arrays.asList(Reference), 4);
        
    }
    
    String HTMLFilter ( String str) {
        
        str = str.replaceAll("&nbsp;", " ");
        str = str.replaceAll("&quot;", "\"");
        str = str.replaceAll("&apos;", "\"");
        str = str.replaceAll("&gt;", ">");
        str = str.replaceAll("&lt;", "<");
        str = str.replaceAll("&amp;", "&");
        
        return str;
        
    }
    
    String RegexMatches ( String str) {
        
        String[] pattern = {"(%\\s\\d\\s[$][\\ss]*)" , "(%\\d[$][s]*)" };

        for (String regex : pattern) {            
            str = str.replaceAll( regex , " algo ");
            
        }
        
        return str;
        
    }
    
}
