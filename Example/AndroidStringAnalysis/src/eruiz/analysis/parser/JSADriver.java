package eruiz.analysis.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.util.Chain;
import dk.brics.automaton.Automaton;
import dk.brics.string.grammar.BinaryProduction;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;
import dk.brics.string.grammar.PairProduction;
import dk.brics.string.grammar.Production;
import dk.brics.string.grammar.UnaryProduction;
import dk.brics.string.grammar.UnitProduction;
import dk.brics.string.grammar.operations.Grammar2MLFA;
import dk.brics.string.mlfa.MLFA;
import dk.brics.string.mlfa.operations.MLFA2Automaton;
import droidblaze.analyses.netsignature.api.NetAPIInvoke;
import droidblaze.thirdparty.jsa.SAStmtVisitor;
import droidblaze.thirdparty.jsa.grammar.GrammarBuilder;

public class JSADriver {
	GrammarBuilder gb;
	HashSet<Nonterminal> Visited;
	HashSet<Nonterminal> VisitedGrammar;
	public JSADriver(Chain<SootClass> scs, Hashtable<SootMethod, List<ReturnStmt>> smRetTable){
		this.gb = new GrammarBuilder();	
		Iterator<SootClass> it = scs.iterator();
		SAStmtVisitor ssv = new SAStmtVisitor(gb, smRetTable);
		
		//TODO including catch blocks
		
		while(it.hasNext()){
			SootClass sc = it.next();
			System.out.println("Analyzing strings in "+ sc.getName()+"...");
			List<SootMethod> sms = sc.getMethods();
			for(SootMethod sm: sms){
				if(sm.isConcrete()){
					Body bd = sm.getActiveBody();
					for(Unit ut:bd.getUnits()){
						Stmt st = (Stmt)ut;
					
						st.apply(ssv);
					}
				}
			}
		}
	}
	public List<Automaton> drive(List<NetAPIInvoke> netArguments, String outputPath ){
        List<Nonterminal> hs_nt = new ArrayList<Nonterminal>();
       

        // Approximate grammar
        
        this.gb.extractNetArgs(netArguments, hs_nt);
        this.gb.simplify(hs_nt);
        int count = 0;
        for(Nonterminal nt:hs_nt){
        	try{
    			String filePath = outputPath + "/grammars";   
    			File myFilePath = new File(filePath);  
    			if (!myFilePath.exists()) {  
    			     myFilePath.mkdirs();  
    			}
    			this.VisitedGrammar = new HashSet<Nonterminal>();
        		PrintWriter pw = new PrintWriter(new FileWriter(outputPath + "/grammars/grammar"+count+".txt"));
        		outputGrammar(nt, pw);
        		pw.close();
        	}catch(IOException e){
        		e.printStackTrace();
        	}
        	count++;
        }
        Grammar r = this.gb.getGrammar();
        
        r.approximateOperationCycles();
        r.approximateNonLinear(hs_nt);
        
        Grammar2MLFA gm = new Grammar2MLFA(r);
        MLFA mlfa = gm.convert();

        MLFA2Automaton mlfa2aut = new MLFA2Automaton(mlfa);
        
        this.Visited = new HashSet<Nonterminal>();
        List<Automaton> auts = new ArrayList<Automaton>();
        for(Nonterminal nt: hs_nt){
        	if(nt!=null){
        		output(nt);
        		
        		Automaton aut = mlfa2aut.extract(gm.getMLFAStatePair(nt));
        		auts.add(aut);
        	}
        }
        return auts;
	}
	private void outputGrammar(Nonterminal nt, PrintWriter pw) {
		// TODO Auto-generated method stub
		if(!this.VisitedGrammar.contains(nt)){
			this.VisitedGrammar.add(nt);
		}else{
			return;
		}
		for(Production p: nt.getProductions()){
			pw.println(nt.toString()+"->"+p.toString());
			if(p instanceof UnitProduction){
				outputGrammar(((UnitProduction) p).getNonterminal(), pw);
			}else if(p instanceof PairProduction){
				outputGrammar(((PairProduction) p).getNonterminal1(), pw);
				outputGrammar(((PairProduction) p).getNonterminal2(), pw);
			}else if(p instanceof UnaryProduction){
				outputGrammar(((UnaryProduction) p).getNonterminal(), pw);
			}else if(p instanceof BinaryProduction){
				outputGrammar(((BinaryProduction) p).getNonterminal1(), pw);
				outputGrammar(((BinaryProduction) p).getNonterminal2(), pw);
			}
		}
	}
	private void output(Nonterminal nt) {
		// TODO Auto-generated method stub
		if(!this.Visited.contains(nt)){
			this.Visited.add(nt);
		}else{
			return;
		}
		for(Production p: nt.getProductions()){
			System.out.println(nt.toString()+"->"+p.toString());
			if(p instanceof UnitProduction){
				output(((UnitProduction) p).getNonterminal());
			}else if(p instanceof PairProduction){
				output(((PairProduction) p).getNonterminal1());
				output(((PairProduction) p).getNonterminal2());
			}else if(p instanceof UnaryProduction){
				output(((UnaryProduction) p).getNonterminal());
			}else if(p instanceof BinaryProduction){
				output(((BinaryProduction) p).getNonterminal1());
				output(((BinaryProduction) p).getNonterminal2());
			}
		}
	}
}
