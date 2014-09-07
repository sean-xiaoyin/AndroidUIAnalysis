package droidblaze.analyses.netsignature.api;

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import droidblaze.thirdparty.jsa.grammar.GrammarBuilder;
import dk.brics.string.grammar.Nonterminal;

public class NewURLInvoke extends NetAPIInvoke{

	public NewURLInvoke(Value arg) {
		this.args = new ArrayList<Value>();
		args.add(arg);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateProds(GrammarBuilder gb, List<Nonterminal> hs_nt) {
		// TODO Auto-generated method stub
		hs_nt.add(gb.getValueTable().get(this.args.get(0)));
	}

}
