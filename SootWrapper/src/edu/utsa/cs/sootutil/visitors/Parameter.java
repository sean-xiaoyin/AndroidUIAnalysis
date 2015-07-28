package edu.utsa.cs.sootutil.visitors;

import soot.SootMethod;

public class Parameter
{
    private int index;
    private SootMethod sm;
    public Parameter(SootMethod sm, int index){
	this.sm = sm;
	this.index = index;
    }
    public int getIndex()
    {
	return index;
    }
    public void setIndex(int index)
    {
	this.index = index;
    }
    public SootMethod getSm()
    {
	return sm;
    }
    public void setSm(SootMethod sm)
    {
	this.sm = sm;
    }
    
}
