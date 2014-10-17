package droidblaze.analyses.netsignature.api;

import java.util.ArrayList;
import java.util.List;

import soot.Value;

import dk.brics.string.grammar.Nonterminal;
import droidblaze.thirdparty.jsa.grammar.GrammarBuilder;

public class OutputStreamInvoke extends NetAPIInvoke{

	public OutputStreamInvoke(Value arg) {
		this.args = new ArrayList<Value>();
		this.args.add(arg);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateProds(GrammarBuilder gb, List<Nonterminal> hs_nt) {
		// TODO Auto-generated method stub
		
	}

}
