package edu.utsa.cs.sootutil.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlowCheck {
	
    public String className;
    public String methodName;
    public List<String> paraList;
    public String operations;
    
    public FlowCheck(){
	this.className = new String();
        this.methodName = new String();
        this.paraList = new ArrayList<String>();
        this.operations = new String();
    }
    
    public FlowCheck(String className, String methodName, String returnType, List<String> paraList, String operations ){
        this.className = className;
        this.methodName = methodName;
        this.paraList = paraList;
        this.operations = operations;
    }

    public void setclassName(String line) {
        this.className = line;
    }

    public void setmethodName(String line) {
        this.methodName = line;
    }

    public void setparaList(String line) {
        //line to paralist
        this.paraList = Arrays.asList(line.split(","));
    }

    public void setoperations(String line) {
        //line to operations
        this.operations = line.replaceAll(",", "");
    }
    
}
