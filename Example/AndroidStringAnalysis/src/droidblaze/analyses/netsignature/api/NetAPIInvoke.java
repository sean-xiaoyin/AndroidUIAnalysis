package droidblaze.analyses.netsignature.api;

import java.util.List;

import dk.brics.string.grammar.Nonterminal;
import droidblaze.thirdparty.jsa.grammar.GrammarBuilder;

import soot.Value;

public abstract class NetAPIInvoke {
	List<Value> args;
	public NetAPIInvoke(){
	}
	public abstract void generateProds(GrammarBuilder gb, List<Nonterminal> hs_nt);
	
}
