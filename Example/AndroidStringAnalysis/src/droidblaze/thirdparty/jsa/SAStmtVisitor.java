package droidblaze.thirdparty.jsa;

import java.util.Hashtable;
import java.util.List;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.ValueBox;

import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import dk.brics.automaton.Automaton;
import droidblaze.analyses.netsignature.visitors.StmtVisitor;
import droidblaze.thirdparty.jsa.grammar.GrammarBuilder;
import droidblaze.thirdparty.jsa.grammar.ValuePos;

public class SAStmtVisitor extends StmtVisitor {

	private GrammarBuilder gb;
	private Hashtable<SootMethod, List<ReturnStmt>> smRetTable;
	private Hashtable<Value, ValuePos> currentPosTable;
	public SAStmtVisitor(GrammarBuilder gb, Hashtable<SootMethod, List<ReturnStmt>> smRetTable) {
		// TODO Auto-generated constructor stub
		this.gb = gb;
		this.smRetTable = smRetTable;
		this.currentPosTable = new Hashtable<Value, ValuePos>();
	}
	
	@Override
	public void caseAssignStmt(AssignStmt arg0) {
		// TODO Auto-generated method stub
		if(arg0.getRightOp().getType().toString().equals("java.lang.StringBuffer")){
			if(!(arg0.getRightOp() instanceof NewExpr)){
				Value leftOp = arg0.getLeftOp();
				Value rightOp = arg0.getRightOp();
				ValuePos leftPos = new ValuePos(leftOp, rightOp);
				ValuePos rightPos = this.currentPosTable.get(rightOp);
				if(rightPos!=null){
					this.currentPosTable.put(leftOp, leftPos);
					this.gb.addUnitProduction(leftPos, rightPos);
					this.currentPosTable.put(leftOp, leftPos);
				}else{
					this.gb.addEpsilonProduction(leftPos);
					this.currentPosTable.put(leftOp, leftPos);
				}
			}else{
				Value leftOp = arg0.getLeftOp();
				ValuePos leftPos = new ValuePos(leftOp, arg0.getRightOp());
				this.gb.addEpsilonProduction(leftPos);
				this.currentPosTable.put(leftOp, leftPos);
			}
		}else if(!isPrimitive(arg0.getLeftOp().getType())&&!(arg0.getRightOp() instanceof NewExpr)){
			if(arg0.getLeftOp() instanceof Local){
				Value leftOp = arg0.getLeftOp();
				Value rightOp = arg0.getRightOp();
				this.gb.addUnitProduction(leftOp, rightOp);
			}else if(arg0.getLeftOp() instanceof FieldRef){
				FieldRef fRef = (FieldRef)arg0.getLeftOp();
				SootField sf = fRef.getField();
				this.gb.addUnitProduction(sf, arg0.getRightOp());
				if(fRef instanceof InstanceFieldRef){
					InstanceFieldRef ifr = (InstanceFieldRef)fRef;
					ifr.getBase().apply(this);
				}
			}else if(arg0.getLeftOp() instanceof ArrayRef){
			/*	ArrayRef aref = (ArrayRef)arg0;
				Value base = aref.getBase();
				if(base instanceof FieldRef){
					
				}else{
					
				}*/
			}else{
				arg0.getLeftOp().apply(this);
			}
		}else{
			
		}
		arg0.getRightOp().apply(this);
	}

	private boolean isPrimitive(Type type) {
		// TODO Auto-generated method stub
		String typeStr = type.toString();
		return typeStr.equals("int")||typeStr.equals("boolean")||typeStr.equals("char")||typeStr.equals("double")||typeStr.equals("byte")||typeStr.equals("float");
	}

	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
		
		SootMethod sm = arg0.getMethod();
		if(sm.hasActiveBody()){
			for(int i = 0; i<arg0.getArgCount(); i++){
				ValueBox argBox = arg0.getArgBox(i);
				Value para = sm.getActiveBody().getParameterLocal(i);
				if(!isPrimitive(para.getType())){
					this.gb.addUnitProduction(para, argBox.getValue());
				}
			}
			List<ReturnStmt> retStmts = this.smRetTable.get(sm);
			for(ReturnStmt ret: retStmts){
				if(!isPrimitive(ret.getOp().getType())){	
					this.gb.addUnitProduction(arg0, ret.getOp());
				}
			}
			
		}else{
/*			if(arg0.getArgCount()==1){
				this.gb.addUnitProduction(arg0.getBase(), arg0.getArg(0));
			}else{
				this.gb.addEpsilonProduction(arg0.getBase());
			}*/
		}
		
	}
	
	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
		//handle string operations
		handleInvoke(arg0);
	}

	private void handleInvoke(VirtualInvokeExpr arg0) {
		SootMethod sm = arg0.getMethod();
		if(sm.hasActiveBody()){
			for(int i = 0; i<arg0.getArgCount(); i++){
				ValueBox argBox = arg0.getArgBox(i);
				Value para = sm.getActiveBody().getParameterLocal(i);
				if(!isPrimitive(para.getType())){	
					this.gb.addUnitProduction(para, argBox.getValue());
				}
			}
				List<ReturnStmt> retStmts = this.smRetTable.get(sm);
				for(ReturnStmt ret: retStmts){
					if(!isPrimitive(ret.getOp().getType())){	
						
						this.gb.addUnitProduction(arg0, ret.getOp());
					}
				}
		}else{
			if(sm.toString().equals("<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>")){
				this.gb.addPairProduction(arg0, arg0.getBase(), arg0.getArg(0));
			}else if(sm.toString().equals("<java.lang.StringBuilder: java.lang.StringBuilder append(int)>")){
				this.gb.addPairProduction(arg0, arg0.getBase(), arg0.getArg(0));
				this.gb.addInitProduction(arg0.getArg(0), Automaton.makeCharSet("0123456789"));
			}else if(sm.toString().equals("<java.lang.String: java.lang.String replace(char,char)>")){
				Value arg1 = arg0.getArg(0);
				Value arg2 = arg0.getArg(1);
				if(arg1 instanceof IntConstant && arg2 instanceof IntConstant){
					this.gb.addReplace1Production(arg0, arg0.getBase(), arg0.getArg(0), arg0.getArg(1));
				}else if(arg1 instanceof IntConstant){
					this.gb.addReplace2Production(arg0, arg0.getBase(), arg0.getArg(0));
				}else if(arg2 instanceof IntConstant){
					this.gb.addReplace3Production(arg0, arg0.getBase(), arg0.getArg(1));
				}else{
					this.gb.addReplace4Production(arg0, arg0.getBase());
				}
			}else if(sm.toString().equals("<java.lang.String: java.lang.String replace(String,String)>")){
				Value arg1 = arg0.getArg(0);
				Value arg2 = arg0.getArg(1);
				if(arg1 instanceof StringConstant && arg2 instanceof StringConstant){
					this.gb.addReplace6Production(arg0, arg0.getBase(), arg0.getArg(0), arg0.getArg(1));
				}else{	
					this.gb.addReplace4Production(arg0, arg0.getBase());
				}
			}else if(sm.toString().equals("<java.lang.String: java.lang.String substring(int,int)>")){
				this.gb.addSubStringProduction(arg0, arg0.getBase());
			}else if(sm.toString().equals("<java.lang.String: java.lang.String substring(int)>")){
				this.gb.addPostfixProduction(arg0, arg0.getBase());
			}else if(sm.getName().equals("toString")&&sm.getDeclaringClass().getName().equals("java.lang.StringBuilder")){
				this.gb.addUnitProduction(arg0, arg0.getBase());
			}else if(sm.toString().equals("<java.lang.StringBuffer: void <init>(java.lang.String)>")){
				this.currentPosTable.put(arg0.getBase(), new ValuePos(arg0.getBase(), arg0));
				this.gb.addUnitProduction(this.currentPosTable.get(arg0.getBase()), arg0.getArg(0));
			}else if(sm.toString().equals("<java.lang.StringBuffer: java.lang.String toString()>")){
				this.gb.addUnitProduction(arg0, this.currentPosTable.get(arg0.getBase()));
			}else if(sm.toString().equals("<java.lang.StringBuffer: java.lang.StringBuffer append()>")){
				ValuePos vp = new ValuePos(arg0.getBase(), arg0);
				this.gb.addPairProduction(vp, this.currentPosTable.get(arg0.getBase()), arg0.getArg(0));
				this.currentPosTable.put(arg0.getBase(), vp);
			}else if(sm.toString().equals("<java.lang.String: byte[] getBytes()>")){
				this.gb.addUnitProduction(arg0, arg0.getBase());
			}else if(sm.toString().equals("<java.io.OutputStream: void writeBytes(byte[])>")){
				this.gb.addUnitProduction(arg0.getBase(), arg0.getArg(0));
				this.gb.writeStreams.add(arg0.getArg(0));
			}else if(sm.toString().equals("<java.net.URLConnection: java.io.OutputStream getOutputStream()")){
				this.gb.addEpsilonProduction(arg0);
				this.gb.getStreams.add(arg0);
			}else if(sm.toString().equals("<java.net.HttpURLConnection: java.io.OutputStream getOutputStream()")){
				this.gb.addEpsilonProduction(arg0);			
				this.gb.getStreams.add(arg0);
			}else if(sm.toString().equals("<java.net.Socket: java.io.OutputStream getOutputStream()")){
				this.gb.addEpsilonProduction(arg0);
				this.gb.getStreams.add(arg0);
			}else if(sm.toString().equals("<org.apache.http.HttpMessage: void addHeader(java.lang.String, java.lang.String)")){
				this.gb.addPairProduction(arg0.getBase(), arg0.getBase(), arg0);
				this.gb.addSpecialProduction(arg0, arg0.getArg(0), arg0.getArg(1));					
			}else if(sm.toString().equals("<org.apache.http.HttpMessage: void setHeader(java.lang.String, java.lang.String)")){
				this.gb.addPairProduction(arg0.getBase(), arg0.getBase(), arg0);
				this.gb.addSpecialProduction(arg0, arg0.getArg(0), arg0.getArg(1));
			}else if(sm.toString().equals("<org.apache.http.client.methods.HttpGet: void <init>(java.lang.String)")){
				this.gb.addUnitProduction(arg0.getBase(), arg0.getArg(0));
				this.gb.Requests.add(arg0.getBase());
			}else if(sm.toString().equals("<org.apache.http.client.methods.HttpPost: void <init>(java.lang.String)")){
				this.gb.addUnitProduction(arg0.getBase(), arg0.getArg(0));
				this.gb.Requests.add(arg0.getBase());
			}else if(sm.toString().equals("<org.apache.http.client.methods.HttpGet: void <init>(java.net.Uri)")){
				this.gb.addUnitProduction(arg0.getBase(), arg0.getArg(0));
				this.gb.Requests.add(arg0.getBase());
			}else if(sm.toString().equals("<org.apache.http.client.methods.HttpPost: void <init>(java.net.Uri)")){
				this.gb.addUnitProduction(arg0.getBase(), arg0.getArg(0));
				this.gb.Requests.add(arg0.getBase());
			}else if(sm.toString().equals("<java.net.Uri: void <init>(java.lang.String)")){
				this.gb.addUnitProduction(arg0.getBase(), arg0.getArg(0));
			}
		}
	}
	
	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr arg0) {
		// TODO Auto-generated method stub
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}		
		SootMethod sm = arg0.getMethod();
		if(sm.hasActiveBody()){
			for(int i = 0; i<arg0.getArgCount(); i++){
				ValueBox argBox = arg0.getArgBox(i);
					Value para = sm.getActiveBody().getParameterLocal(i);
					if(!isPrimitive(para.getType())){	
						
						this.gb.addUnitProduction(para, argBox.getValue());
					}
			}
				List<ReturnStmt> retStmts = this.smRetTable.get(sm);
				for(ReturnStmt ret: retStmts){
					if(!isPrimitive(ret.getOp().getType())){		
						this.gb.addUnitProduction(arg0, ret.getOp());
					}
				}
		}else{
			if(sm.toString().equals("<java.lang.String: java.lang.String valueOf(java.lang.Object)>")){
				this.gb.addUnitProduction(arg0, arg0.getArg(0));
			}
		}
	}
	
	@Override
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr arg0) {
		// TODO Auto-generated method stub
		arg0.getBase().apply(this);
		for(int i = 0; i<arg0.getArgCount(); i++){
			Value ut = arg0.getArg(i);
			ut.apply(this);
		}
		SootMethod sm = arg0.getMethod();
		if(sm.toString().equals("<org.apache.http.HttpMessage: void addHeader(java.lang.String, java.lang.String)")){
			
		}else if(sm.toString().equals("<org.apache.http.HttpMessage: void setHeader(java.lang.String, java.lang.String)")){
			
		}
	}
	
	@Override
	public void caseStringConstant(StringConstant arg0) {
		// TODO Auto-generated method stub
		this.gb.addInitProduction(arg0, Automaton.makeString(arg0.value));
	}
	public void caseInstanceFieldRef(InstanceFieldRef arg0) {
		// TODO Auto-generated method stub
			this.gb.addUnitProduction(arg0, arg0.getField());
		arg0.getBase().apply(this);
	}
}
