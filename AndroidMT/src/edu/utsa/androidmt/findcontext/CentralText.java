/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utsa.androidmt.findcontext;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Xue
 */
public class CentralText {
    
    private String text_ID;
    private String text_strings;
    private Set<String> Context = new LinkedHashSet<>();
    private boolean found = false;
    
    public CentralText() {
        text_ID = " N/A ";
        text_strings = " N/A ";
    }
    
    public void setFound(){
	this.found = true;
    }
    public boolean isFound(){
	return this.found;
    }
    
    public void SetCentralText( CentralText t) {
        text_ID = t.text_ID;
        text_strings = t.text_strings;
        Context = t.Context;
    }
    
    
    public String SetTextID ( String text_id ) {
        text_ID = text_id;
        return text_ID;
    }
    
    public String SetTextString (String text_str) {
        text_strings = text_str;
        return text_strings;
    }
    
    public String GetTextID () {
        
        return text_ID;
    }
    
    public String GetTextString () {
        
        return text_strings;
    }
    
    public Set<String> GetContext () {
        
        return Context;
    }
    
    public void AddContext(String string) {
	this.Context.add(string);
    }

    public Set<String> AddContext ( List<String> fileStringList, int index) {
        
        Set<String> str = new LinkedHashSet<>();
        for (int i = 0; i < index; i ++){
            str.add(fileStringList.get(i));
        }
        for (int i = index + 1; i < fileStringList.size(); i ++){
            str.add(fileStringList.get(i));
        }
        
        Context = str;
        return Context;
        
    }
    
    public Set<String> AddContextNotFound () {
        
        Set<String> str = new LinkedHashSet<>();
        str.add("Not Found");
        
        Context = str;
        return Context;    
    }
    
    public Set<String> AddContextNoNeighbor () {
        
        Set<String> str = new LinkedHashSet<>();
        str.add("No Neighbor");

        Context = str;
        return Context;    
    }

    
    
}