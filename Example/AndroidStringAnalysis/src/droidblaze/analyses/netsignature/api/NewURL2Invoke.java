package droidblaze.analyses.netsignature.api;

import java.util.List;

import soot.Value;
import droidblaze.thirdparty.jsa.grammar.GrammarBuilder;
import dk.brics.string.grammar.Nonterminal;

public class NewURL2Invoke extends NetAPIInvoke{

	public NewURL2Invoke(List<Value> args) {
		this.args = args;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateProds(GrammarBuilder gb, List<Nonterminal> hs_nt) {
		// TODO Auto-generated method stub
		
	}

}
