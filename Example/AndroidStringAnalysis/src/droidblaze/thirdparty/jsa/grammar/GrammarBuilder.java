package droidblaze.thirdparty.jsa.grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.StringConstant;
import soot.jimple.VirtualInvokeExpr;
import dk.brics.automaton.Automaton;
import dk.brics.string.grammar.AutomatonProduction;
import dk.brics.string.grammar.BinaryProduction;
import dk.brics.string.grammar.EpsilonProduction;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;
import dk.brics.string.grammar.PairProduction;
import dk.brics.string.grammar.Production;
import dk.brics.string.grammar.UnaryProduction;
import dk.brics.string.grammar.UnitProduction;
import dk.brics.string.stringoperations.Postfix;
import dk.brics.string.stringoperations.Replace1;
import dk.brics.string.stringoperations.Replace2;
import dk.brics.string.stringoperations.Replace3;
import dk.brics.string.stringoperations.Replace4;
import dk.brics.string.stringoperations.Replace6;
import dk.brics.string.stringoperations.Substring;
import droidblaze.analyses.netsignature.api.NetAPIInvoke;
import droidblaze.thirdparth.jsa.operations.AddParas;

public class GrammarBuilder {
	Grammar r;

	private Hashtable<Object, Nonterminal> valueTable = new Hashtable<Object, Nonterminal>();
	
	Hashtable<Nonterminal, Nonterminal> simplifyTable = new Hashtable<Nonterminal, Nonterminal>();
	public List<Value> writeStreams = new ArrayList<Value>();
	public List<Value> getStreams = new ArrayList<Value>();
	public List<Value> Requests = new ArrayList<Value>();
	

	public GrammarBuilder(){
		this.r = new Grammar();
	}
	public Grammar getGrammar() {
		// TODO Auto-generated method stub
		return this.r;
	}
	private Nonterminal getNonTerminal(Object value) {
		Nonterminal left;
		if(valueTable.containsKey(value)){
			left = valueTable.get(value);
		}else{
			r.addNonterminal();
			left = r.getNonterminals().get(r.getNonterminals().size()-1);
			valueTable.put(value, left);
		}
		return left;
	}
	public void addUnitProduction(Object leftOp, Object rightOp) {
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(leftOp);
		
		right = getNonTerminal(rightOp);
		r.addUnitProduction(left, right);
	}
	public void addInitProduction(Value arg0, Automaton automaton) {
		// TODO Auto-genserated method stub
		Nonterminal left;
		left = getNonTerminal(arg0);

		r.addAutomatonProduction(left, automaton);
	}
	public void addPairProduction(Object ret, Object arg1,
			Object arg2) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right1;
		Nonterminal right2;
		left = getNonTerminal(ret);
				
		right1 = getNonTerminal(arg1);
				
		right2 = getNonTerminal(arg2);
				
		r.addPairProduction(left, right1, right2);
	}
	
	private Nonterminal getNewNonTerminal(Grammar g, Nonterminal old) {
		Nonterminal nt;
		if(this.simplifyTable.containsKey(old)){
			nt = simplifyTable.get(old);
		}else{
			g.addNonterminal();
			nt = g.getNonterminals().get(g.getNumberOfNonterminals()-1);
			simplifyTable.put(old, nt);
		}
		return nt;
	}
	
	public void extractNetArgs(List<NetAPIInvoke> netInvokes, List<Nonterminal> hs_nt) {
		// TODO Auto-generated method stub
		for(NetAPIInvoke vb:netInvokes){
			vb.generateProds(this, hs_nt);
		}
	}
	
	public void simplify(List<Nonterminal> hs_nt) {
		// TODO Auto-generated method stub
		Grammar result = new Grammar();
		
		removeIrrelaventProds(result, hs_nt);
		this.r = result;
		
		Grammar result1 = new Grammar();		
		removeIdentityProds(result1, hs_nt);
		
		handleEndNonterminals(result1);
		this.r = result1;
	}
	
	private void removeIrrelaventProds(Grammar result, List<Nonterminal> hs_nt) {
		// TODO Auto-generated method stub
		Hashtable<Nonterminal, List<Nonterminal>> deduceTable = new Hashtable<Nonterminal, List<Nonterminal>>();
		Hashtable<Nonterminal, List<Nonterminal>> deduceBackTable = new Hashtable<Nonterminal, List<Nonterminal>>();
		Hashtable<Nonterminal, Nonterminal> mapTable = new Hashtable<Nonterminal, Nonterminal>();
		HashSet<Nonterminal> reqNts = new HashSet<Nonterminal>();
		for(Value v: this.Requests){
			reqNts.add(this.getNonTerminal(v));
		}
		
		for(Nonterminal nt: this.r.getNonterminals()){
			for(Production p:nt.getProductions()){
				if(p instanceof UnitProduction){
					UnitProduction up = (UnitProduction)p;
					addListTable(deduceBackTable, up.getNonterminal(), nt);
					addListTable(deduceTable, nt, up.getNonterminal());
				}else if(p instanceof PairProduction){
					PairProduction up = (PairProduction)p;
					addListTable(deduceBackTable, up.getNonterminal1(), nt);
					addListTable(deduceBackTable, up.getNonterminal2(), nt);
					addListTable(deduceTable, nt, up.getNonterminal1());
					addListTable(deduceTable, nt, up.getNonterminal2());
				}else if(p instanceof UnaryProduction){
					UnaryProduction up = (UnaryProduction)p;
					addListTable(deduceBackTable, up.getNonterminal(), nt);
					addListTable(deduceTable, nt, up.getNonterminal());
				}else if(p instanceof BinaryProduction){
					BinaryProduction up = (BinaryProduction)p;
					addListTable(deduceBackTable, up.getNonterminal1(), nt);
					addListTable(deduceBackTable, up.getNonterminal2(), nt);
					addListTable(deduceTable, nt, up.getNonterminal1());
					addListTable(deduceTable, nt, up.getNonterminal2());
				}
			}
		}
		for(Nonterminal h_nt: hs_nt){
			Queue<Nonterminal> workQueue = new LinkedBlockingQueue<Nonterminal>();
			workQueue.add(h_nt);
			Nonterminal n_n = result.addNonterminal();
			mapTable.put(h_nt, n_n);
		
		while(!workQueue.isEmpty()){
			Nonterminal nt = workQueue.poll();
			Nonterminal nt_new = mapTable.get(nt);
			List<Nonterminal> nts = deduceTable.get(nt);
			List<Nonterminal> bnts = deduceBackTable.get(nt);
			if(nts!=null){
				for(Nonterminal nt1:nts){
					if(!mapTable.containsKey(nt1)){
						workQueue.add(nt1);
						Nonterminal n = result.addNonterminal();
						mapTable.put(nt1, n);
					}
				}
			}
			if(bnts!=null){
				for(Nonterminal nt1:bnts){
					if(!mapTable.containsKey(nt1)){
						workQueue.add(nt1);
						Nonterminal n = result.addNonterminal();
						mapTable.put(nt1, n);
					}
				}
			}
			for(Production p:nt.getProductions()){
				if(p instanceof UnitProduction){
					UnitProduction up = (UnitProduction)p;
					Nonterminal right = mapTable.get(up.getNonterminal());
					if(noUnitProds(nt_new, up, right)){
						result.addUnitProduction(nt_new, right);
					}
					if(reqNts.contains(right)){
						reqNts.remove(right);
						reqNts.add(nt);
					}
				}else if(p instanceof PairProduction){
					PairProduction up = (PairProduction)p;
					Nonterminal right1 = mapTable.get(up.getNonterminal1());
					Nonterminal right2 = mapTable.get(up.getNonterminal2());
					if(noPairProds(nt_new, up, right1, right2)){
						result.addPairProduction(nt_new, right1, right2);
					}
				}else if(p instanceof UnaryProduction){
					UnaryProduction up = (UnaryProduction)p;
					Nonterminal right = mapTable.get(up.getNonterminal());
					if(noUnaryProds(nt_new, up, right)){	
						result.addUnaryProduction(nt_new, up.getOperation(), right);
					}
				}else if(p instanceof BinaryProduction){
					BinaryProduction up = (BinaryProduction)p;
					Nonterminal right1 = mapTable.get(up.getNonterminal1());
					Nonterminal right2 = mapTable.get(up.getNonterminal2());
					if(noBinaryProds(nt_new, up, right1, right2)){						
						result.addBinaryProduction(nt_new, up.getOperation(), right1, right2);
					}
				}else if(p instanceof EpsilonProduction){
					if(noProds(nt_new, p)){
						result.addEpsilonProduction(nt_new);
					}
				}else if(p instanceof AutomatonProduction){
					AutomatonProduction up = (AutomatonProduction)p;
					if(noProds(nt_new, p)){
						result.addAutomatonProduction(nt_new, up.getAutomaton());
					}
				}
			}
		}}
		for(int i = 0; i< hs_nt.size(); i++){
			hs_nt.set(i, mapTable.get(hs_nt.get(i)));
		}
		for(Value v: this.writeStreams){
			if(mapTable.get(v)!=null){
				hs_nt.add(mapTable.get(v));
			}
		}
		Iterator<Nonterminal> it = reqNts.iterator();
		while(it.hasNext()){
			Nonterminal req = it.next();
			Nonterminal new_req = mapTable.get(req);
			if(new_req!=null){
				hs_nt.add(new_req);
			}
		}
	}
	private boolean noBinaryProds(Nonterminal nt_new, BinaryProduction up, Nonterminal right1, Nonterminal right2) {
		// TODO Auto-generated method stub
		for(Production p1:nt_new.getProductions()){
			if(p1 instanceof BinaryProduction){
				BinaryProduction bp = (BinaryProduction)p1;
				if(bp.getOperation().toString().equals(up.getOperation().toString())&&bp.getNonterminal1()==right1&&bp.getNonterminal2()==right2){
					return false;
				}
			}
		}
		return true;
	}
	private boolean noUnaryProds(Nonterminal nt_new, UnaryProduction up, Nonterminal right) {
		// TODO Auto-generated method stub
		for(Production p1:nt_new.getProductions()){
			if(p1 instanceof UnaryProduction){
				UnaryProduction bp = (UnaryProduction)p1;
				if(bp.getOperation().toString().equals(up.getOperation().toString())&&bp.getNonterminal()==right){
					return false;
				}
			}
		}
		return true;
	}
	private boolean noPairProds(Nonterminal nt_new, PairProduction up, Nonterminal right1, Nonterminal right2) {
		// TODO Auto-generated method stub
		for(Production p1:nt_new.getProductions()){
			if(p1 instanceof PairProduction){
				PairProduction bp = (PairProduction)p1;
				if(bp.getNonterminal1()==right1&&bp.getNonterminal2()==right2){
					return false;
				}
			}
		}
		return true;
		
	}
	private boolean noUnitProds(Nonterminal nt_new, UnitProduction up, Nonterminal right) {
		// TODO Auto-generated method stub
		for(Production p1:nt_new.getProductions()){
			if(p1 instanceof UnitProduction){
				UnitProduction bp = (UnitProduction)p1;
				if(bp.getNonterminal()==right){
					return false;
				}
			}
		}
		return true;
	}
	private boolean noProds(Nonterminal nt_new, Production p) {
		// TODO Auto-generated method stub
		for(Production p1:nt_new.getProductions()){
			if(p1.toString().equals(p.toString())){
				return false;
			}
		}
		return true;
	}
	private void handleEndNonterminals(Grammar result) {
		// TODO Auto-generated method stub
		for(Nonterminal nt: result.getNonterminals()){
			if(nt.getProductions().size()==0){
				result.addAutomatonProduction(nt, Automaton.makeAnyString());
			}
		}
	}
	
	private void removeIdentityProds(Grammar result, List<Nonterminal> hs_nt) {
		Hashtable<Nonterminal, List<Nonterminal>> deduceTable = new Hashtable<Nonterminal, List<Nonterminal>>();
		
		for(Nonterminal nt: this.r.getNonterminals()){
			for(Production p:nt.getProductions()){
				if(p instanceof UnitProduction){
					UnitProduction up = (UnitProduction)p;
					addListTable(deduceTable, up.getNonterminal(), nt);
				}else if(p instanceof PairProduction){
					PairProduction up = (PairProduction)p;
					addListTable(deduceTable, up.getNonterminal1(), nt);
					addListTable(deduceTable, up.getNonterminal2(), nt);
				}else if(p instanceof UnaryProduction){
					UnaryProduction up = (UnaryProduction)p;
					addListTable(deduceTable, up.getNonterminal(), nt);
				}else if(p instanceof BinaryProduction){
					BinaryProduction up = (BinaryProduction)p;
					addListTable(deduceTable, up.getNonterminal1(), nt);
					addListTable(deduceTable, up.getNonterminal2(), nt);
				}
			}
		}
		
		for(Nonterminal nt: this.r.getNonterminals()){
			if(nt.getProductions().size()==1){
				Production p = nt.getProductions().get(0);
				if(p instanceof UnitProduction){
					UnitProduction up = (UnitProduction)p;
					if(deduceTable.get(up.getNonterminal()).size()==1){
						merge(up.getNonterminal(), nt, result);
					}
				}
			}
		}
		
		for(Nonterminal nt: this.r.getNonterminals()){
			Nonterminal nt_new = this.getNewNonTerminal(result, nt);
			for(Production p:nt.getProductions()){
				if(p instanceof UnitProduction){
					UnitProduction up = (UnitProduction)p;
					Nonterminal right = this.getNewNonTerminal(result, up.getNonterminal());
					if(nt_new!=right){
						result.addUnitProduction(nt_new, right);
					}
				}else if(p instanceof PairProduction){
					PairProduction up = (PairProduction)p;
					Nonterminal right1 = this.getNewNonTerminal(result, up.getNonterminal1());
					Nonterminal right2 = this.getNewNonTerminal(result, up.getNonterminal2());
					result.addPairProduction(nt_new, right1, right2);
				}else if(p instanceof UnaryProduction){
					UnaryProduction up = (UnaryProduction)p;
					Nonterminal right = this.getNewNonTerminal(result, up.getNonterminal());
					result.addUnaryProduction(nt_new, up.getOperation(), right);
				}else if(p instanceof BinaryProduction){
					BinaryProduction up = (BinaryProduction)p;
					Nonterminal right1 = this.getNewNonTerminal(result, up.getNonterminal1());
					Nonterminal right2 = this.getNewNonTerminal(result, up.getNonterminal2());
					result.addBinaryProduction(nt_new, up.getOperation(), right1, right2);
				}else if(p instanceof EpsilonProduction){
					result.addEpsilonProduction(nt_new);
				}else if(p instanceof AutomatonProduction){
					AutomatonProduction up = (AutomatonProduction)p;
					result.addAutomatonProduction(nt_new, up.getAutomaton());
				}
			}
		}
		for(int i = 0; i< hs_nt.size(); i++){
			hs_nt.set(i, this.simplifyTable.get(hs_nt.get(i)));
		}
	}
	private void merge(Nonterminal nt1, Nonterminal nt2, Grammar result) {
		// TODO Auto-generated method stub
		Nonterminal nt1_n = this.simplifyTable.get(nt1);
		Nonterminal nt2_n = this.simplifyTable.get(nt2);
		
		if(nt1_n==null&&nt2_n==null){
			result.addNonterminal();
			Nonterminal nt_n = result.getNonterminals().get(result.getNumberOfNonterminals()-1);
			this.simplifyTable.put(nt1, nt_n);
			this.simplifyTable.put(nt2, nt_n);
		}else if(nt1_n!=null){
			this.simplifyTable.put(nt1, nt1_n);
			this.simplifyTable.put(nt2, nt1_n);
		}else if(nt2_n!=null){
			this.simplifyTable.put(nt1, nt2_n);
			this.simplifyTable.put(nt2, nt2_n);
		}
		
		
	}
	private void addListTable(
			Hashtable<Nonterminal, List<Nonterminal>> deduceTable,
			Nonterminal rightNt, Nonterminal leftNt) {
		// TODO Auto-generated method stub
		List<Nonterminal> nts = deduceTable.get(rightNt);
		if(nts!=null){
			nts.add(leftNt);
		}else{
			nts = new ArrayList<Nonterminal>();
			nts.add(leftNt);
			deduceTable.put(rightNt, nts);
		}
	}
	public void addReplace1Production(VirtualInvokeExpr arg0, Value base,
			Value arg1, Value arg2) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(arg0);				
		right = getNonTerminal(base);
		
		char c1 = (char)((IntConstant)arg1).value;
		char c2 = (char)((IntConstant)arg2).value;
								
		r.addUnaryProduction(left, new Replace1(c1, c2), right);
	}
	public void addReplace2Production(VirtualInvokeExpr arg0, Value base,
			Value arg) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(arg0);				
		right = getNonTerminal(base);
		
		char c1 = (char)((IntConstant)arg).value;
								
		r.addUnaryProduction(left, new Replace2(c1), right);
	}
	public void addReplace3Production(VirtualInvokeExpr arg0, Value base,
			Value arg) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(arg0);				
		right = getNonTerminal(base);
		
		char c2 = (char)((IntConstant)arg).value;
								
		r.addUnaryProduction(left, new Replace3(c2), right);
	}
	public void addReplace4Production(VirtualInvokeExpr arg0, Value base) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(arg0);				
		right = getNonTerminal(base);
								
		r.addUnaryProduction(left, new Replace4(), right);
	}
	public void addReplace6Production(VirtualInvokeExpr arg0, Value base,
			Value arg1, Value arg2) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(arg0);				
		right = getNonTerminal(base);
		
		String s1 = ((StringConstant)arg1).value;
		String s2 = ((StringConstant)arg2).value;
								
		r.addUnaryProduction(left, new Replace6(s1, s2), right);
	}
	public void addSubStringProduction(VirtualInvokeExpr arg0, Value base) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(arg0);				
		right = getNonTerminal(base);
								
		r.addUnaryProduction(left, new Substring(), right);
	}
	public void addPostfixProduction(VirtualInvokeExpr arg0, Value base) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;
		left = getNonTerminal(arg0);				
		right = getNonTerminal(base);
								
		r.addUnaryProduction(left, new Postfix(), right);
	}
	public Hashtable<Object, Nonterminal> getValueTable() {
		return valueTable;
	}
	public void addEpsilonProduction(Object leftPos) {
		// TODO Auto-generated method stub
		Nonterminal left;
		left = getNonTerminal(leftPos);				
								
		r.addEpsilonProduction(left);
	}
	public void addSpecialProduction(VirtualInvokeExpr arg0, Value arg1,
			Value arg2) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right1;
		Nonterminal right2;
		left = getNonTerminal(arg0);
				
		right1 = getNonTerminal(arg1);
				
		right2 = getNonTerminal(arg2);
				
		r.addNonterminal();
		Nonterminal colon = r.getNonterminals().get(r.getNonterminals().size()-1);
		r.addNonterminal();
		Nonterminal mid = r.getNonterminals().get(r.getNonterminals().size()-1);

		r.addPairProduction(left, right1, mid);
		r.addPairProduction(mid, colon, right2);
	}
	public void addParamProduction(Value base, Value base2,
			VirtualInvokeExpr arg0) {
		// TODO Auto-generated method stub
		Nonterminal left;
		Nonterminal right;

		left = getNonTerminal(base);				
		right = getNonTerminal(arg0);
		
								
		r.addUnaryProduction(left, new AddParas(), right);
	}
	
}
