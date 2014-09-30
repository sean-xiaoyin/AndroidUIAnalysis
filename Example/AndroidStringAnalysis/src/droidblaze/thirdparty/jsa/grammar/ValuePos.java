package droidblaze.thirdparty.jsa.grammar;

import soot.Value;

public class ValuePos {
	private Value v;
	private Value encloser;
	public ValuePos(Value v, Value encloser){
		this.v = v;
		this.encloser = encloser;
	}
	public Value getEncloser() {
		return encloser;
	}
	public Value getValue() {
		return v;
	}
	
}
