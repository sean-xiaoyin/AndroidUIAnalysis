package edu.utsa.androidmt.rerank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import edu.usta.androidmt.model.PropModel;
import edu.usta.androidmt.model.SentencePair;
import edu.utsa.androidmt.loader.DataLoader;
import edu.utsa.androidmt.loader.DataLoaderConfig;
import edu.utsa.androidmt.rerank.CommandRunner.CommandResult;

public class MTDriver {
    public static void main(String args[]) throws ClassNotFoundException, IOException, InterruptedException{
	MTDriver driver = new MTDriver();
	driver.run();
    }
    public void run() throws IOException, ClassNotFoundException, InterruptedException{
	DataLoaderConfig trainConfig = DataLoaderConfig.defaultTrainConfig();
	System.out.println("loading the model...");
	File modelFile = new File(trainConfig.getContextModelPath());
	PropModel model = modelFile.exists() && trainConfig.lazy() ? loadModel(trainConfig.getContextModelPath()) : generateModel(trainConfig);
	System.out.println("reporting interesting words...");
	model.reportTrainingData(trainConfig.getReportPath());
	System.out.println("performing translation...");
	translate(model, DataLoaderConfig.defaultTestConfig(), "");
    }
    
    public PropModel generateModel(DataLoaderConfig config) throws IOException{
	DataLoader loader = new DataLoader();
	loader.loadAll(config, true);
	PropModel model = PropModel.extractModel(loader);
	writeModel(model, config.getContextModelPath());
	return model;
    }
    private void writeModel(PropModel model, String contextModelPath) throws IOException {
	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(contextModelPath));
	oos.writeObject(model);
	oos.close();
    }

    private PropModel loadModel(String cmodelPath) throws IOException, ClassNotFoundException{
	ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cmodelPath));
	PropModel m = (PropModel)ois.readObject();
	ois.close();
	return m;
    }

    public void translate(PropModel model, DataLoaderConfig config, String outputPath) throws IOException, InterruptedException{
//	List<PhrasePair> pairs = model.getPairs("drain");
		
	DataLoader loader = new DataLoader();
	loader.loadAll(config, false);
	MosesConfig mconfig = MosesConfig.defaultConfig();
	
	PropUpdater update = new PropUpdater(model, config.getPhraseTablePath(), loader.getPhraseLineTable(), loader.getPhraseLines());
	
	List<String> ids = new ArrayList<String>();
	ids.addAll(loader.getIdSentenceTable().keySet());
	System.out.println("translating sentences, total " + ids.size());
	int count = 0;

	List<List<String>> batches = fetchBatches(ids, loader);
	CommandRunner.runCommand("rm -rf " + mconfig.tempdir + "/final.ids", true);
	CommandRunner.runCommand("rm -rf " + mconfig.tempdir + "/final.trans", true);
	for(int i = 0; i< batches.size(); i++){
	    List<String> batch = batches.get(i);
	    List<String> froms = new ArrayList<String>();
	    Hashtable<String, List<String>> contexts = new Hashtable<String, List<String>>();
	    for(String id : batch){
		SentencePair sp = loader.getIdSentenceTable().get(id);
		String from = sp.getFrom();
		List<String> context = loader.getIdContextTable().get(id);
		if(context == null){
		    context = new ArrayList<String>();
		}
		contexts.put(from, context);
		froms.add(from);		
	    }	    
	    count = count + 1;
	    System.out.println("translating batch " + count + " of " + batches.size() + ", total sentences " + froms.size());
	    String newPhraseTablePath = update.getPhraseTablePath().substring(0, update.getPhraseTablePath().lastIndexOf('/')) + "/phrase-table.0-0.1.1";
	    update.updateProp(froms, contexts, i == batches.size() - 1);
            runTranslation(mconfig, batch, froms);
            CommandRunner.runCommand("rm -rf " + newPhraseTablePath, true);
	}
	reportBleu(mconfig);
    }

    private void reportBleu(MosesConfig mconfig) throws IOException, InterruptedException {
	List<String> outs = DataLoader.fetchLines(mconfig.tempdir + "/final.trans", 0);
	List<String> ids = DataLoader.fetchLines(mconfig.tempdir + "/final.ids", 0);
	List<String> oriIDs = DataLoader.fetchLines(mconfig.refdir + "/test.ids", 0);
	PrintWriter pw = new PrintWriter(new FileWriter(mconfig.tempdir + "/re-order.trans"));
	
	Hashtable<String, String> idOutTable = new Hashtable<String, String>();
	for(int i = 0; i < outs.size(); i++){
	    idOutTable.put(ids.get(i), outs.get(i));
	}
	for(String ori : oriIDs){
	    pw.println(idOutTable.get(ori));
	}
	pw.close();
	
	
	String lan = DataLoaderConfig.LAN;
	CommandResult cr = CommandRunner.runCommand(mconfig.mosesdir + "/mosesdecoder/scripts/generic/multi-bleu.perl -lc " 
	+ mconfig.refdir + "/test.true." + lan + " < " + mconfig.tempdir + "/re-order.trans", true);
	System.out.println(cr.toString());
	
	reportSeparate(mconfig);
    }
    public void reportSeparate(MosesConfig mconfig) throws IOException, InterruptedException {
	String lan = DataLoaderConfig.LAN;
	List<String> newTrans = DataLoader.fetchLines(mconfig.tempdir + "/re-order.trans", 0);
	List<String> ids = DataLoader.fetchLines(mconfig.refdir + "/test.ids", 0);
	HashSet<String> ids_nocontext = new HashSet<String>();
	ids_nocontext.addAll(DataLoader.fetchLines(mconfig.tempdir + "/temp.ids", 0));

	List<String> oldTrans = DataLoader.fetchLines(mconfig.refdir + "/test.translated." + lan, 0);
	List<String> refs = DataLoader.fetchLines(mconfig.refdir + "/test.true." + lan, 0);
	List<String> oris = DataLoader.fetchLines(mconfig.refdir + "/test.true.en", 0);
	
	List<String> newTransC = new ArrayList<String>();
	List<String> oldTransC = new ArrayList<String>();
	List<String> refsC = new ArrayList<String>();
	
	Hashtable<String, List<String>> contents_old = new Hashtable<String, List<String>>();
	Hashtable<String, List<String>> contents_new = new Hashtable<String, List<String>>();
	Hashtable<String, List<String>> contents_ref = new Hashtable<String, List<String>>();

	if(newTrans.size() == ids.size() && oldTrans.size() == refs.size()
		&& ids.size() == refs.size()){
	    (new File(mconfig.tempdir + "/workingbleu")).mkdirs();
	    CommandRunner.runCommand("rm -rf " + mconfig.tempdir + "/separate.diff", true);
	    
	    List<BleuResult> brs = new ArrayList<BleuResult>();
	    List<Diff> diffs = new ArrayList<Diff>();
	    String oldFile = "";
	    newTrans.add("lastline");
	    for(int i = 0; i < newTrans.size(); i++){
		String file = (i == newTrans.size() - 1) ? "lastline":getFileName(ids.get(i));
		oldFile = i > 0 ? getFileName(ids.get(i - 1)) : file;
		if(!file.equals(oldFile)){
		    DataLoader.writeLines(mconfig.tempdir + "/workingbleu/oldTrans", oldTransC);
		    DataLoader.writeLines(mconfig.tempdir + "/workingbleu/newTrans", newTransC);
		    DataLoader.writeLines(mconfig.tempdir + "/workingbleu/refs", refsC);
//		    System.out.println(oldFile);

		    CommandResult cr = CommandRunner.runCommand(mconfig.mosesdir + "/mosesdecoder/scripts/generic/multi-bleu.perl -lc " 
				+ mconfig.tempdir + "/workingbleu/refs" + " < " + mconfig.tempdir + "/workingbleu/newTrans", true);
		    String newline = cr.getStdOut();
		    CommandResult cr1 = CommandRunner.runCommand(mconfig.mosesdir + "/mosesdecoder/scripts/generic/multi-bleu.perl -lc " 
				+ mconfig.tempdir + "/workingbleu/refs" + " < " + mconfig.tempdir + "/workingbleu/oldTrans", true);
		    String oldline = cr1.getStdOut();
		    CommandRunner.runCommand("echo  \"" + oldFile + "\n\" >> " + mconfig.tempdir + "/separate.diff", true);

		    CommandRunner.runCommand("diff  " + mconfig.tempdir + "/workingbleu/newTrans " + mconfig.tempdir + "/workingbleu/oldTrans >> "
			    + mconfig.tempdir + "/separate.diff", true);
		    
		    BleuResult br = new BleuResult(oldFile, oldline, newline);
		    br.getDiffs().addAll(diffs);
		    diffs.clear();
		    brs.add(br);
		    
		    
		    
		    contents_old.put(oldFile, new ArrayList<String>(oldTransC));
		    contents_new.put(oldFile, new ArrayList<String>(newTransC));
		    contents_ref.put(oldFile, new ArrayList<String>(refsC));
		    
		    oldTransC.clear();
		    newTransC.clear();
		    refsC.clear();
		}
		if(i<ids.size() && !ids_nocontext.contains(ids.get(i))){
		    oldTransC.add(oldTrans.get(i));
		    newTransC.add(newTrans.get(i));
		    refsC.add(refs.get(i));
		    if(!oldTrans.get(i).equals(newTrans.get(i))){
			Diff dif = new Diff(ids.get(i), oris.get(i), oldTrans.get(i), newTrans.get(i), refs.get(i));
			diffs.add(dif);
		    }
		}
	    }


	    Collections.sort(brs);	    
	    oldTrans.clear();
	    newTrans.clear();
	    refs.clear();
	    for(int i = brs.size() - 1; i >= brs.size() - 30; i--){
//		System.out.println(brs.get(i).getName());
		
//		System.out.println(brs.get(i).toString());
		for(Diff dif: brs.get(i).getDiffs()){
		    System.out.print(dif);
		}

		oldTrans.addAll(contents_old.get(brs.get(i).getName()));
		newTrans.addAll(contents_new.get(brs.get(i).getName()));
		refs.addAll(contents_ref.get(brs.get(i).getName()));
	    }
	    
	    DataLoader.writeLines(mconfig.tempdir + "/workingbleu/oldTrans_all", oldTrans);
	    DataLoader.writeLines(mconfig.tempdir + "/workingbleu/newTrans_all", newTrans);
	    DataLoader.writeLines(mconfig.tempdir + "/workingbleu/refs_all", refs);

//	    System.out.println("-------------------------------------------------------");

	    CommandResult cr = CommandRunner.runCommand(mconfig.mosesdir + "/mosesdecoder/scripts/generic/multi-bleu.perl -lc " 
			+ mconfig.tempdir + "/workingbleu/refs_all" + " < " + mconfig.tempdir + "/workingbleu/newTrans_all", true);
	    System.out.println("New:" + cr.getStdOut());
	    CommandResult cr1 = CommandRunner.runCommand(mconfig.mosesdir + "/mosesdecoder/scripts/generic/multi-bleu.perl -lc " 
			+ mconfig.tempdir + "/workingbleu/refs_all" + " < " + mconfig.tempdir + "/workingbleu/oldTrans_all", true);
	    System.out.println("Old:" + cr1.getStdOut());

	}else{
	    System.err.println("allignment errors");
	    System.exit(1);
	}

    }
    private String getFileName(String id) {
	return id.substring(0, id.indexOf(":"));
    }
    private List<List<String>> fetchBatches(List<String> ids, DataLoader loader) {
	List<List<String>> batches = new ArrayList<List<String>>();
	List<Set<String>> phraseSets = new ArrayList<Set<String>>();
	List<String> noContextBatch = new ArrayList<String>();
	for(String id : ids){
	    String from = loader.getIdSentenceTable().get(id).getFrom();
	    Set<String> phrases = SentencePair.getPhrases(from, 1);
	    List<String> context = loader.getIdContextTable().get(id);
	    if(context == null || context.size()==0){
		noContextBatch.add(id);
		continue;
	    }
	    boolean found = false;
	    for(int i = 0; i < phraseSets.size(); i++){
		Set<String> phraseSet = phraseSets.get(i);
		if(!intersect(phrases, phraseSet)){
		    batches.get(i).add(id);
		    phraseSets.get(i).addAll(phrases);
		    found = true;
		    break;
		}
	    }
	    if(!found){
		List<String> newBatch = new ArrayList<String>();
		newBatch.add(id);
		batches.add(newBatch);
		phraseSets.add(phrases);
	    }
	}
	batches.add(noContextBatch);
	return batches;
    }
    private boolean intersect(Set<String> phrases, Set<String> phraseSet) {
	for(String phrase : phrases){
	    if(hasLetter(phrase) && phraseSet.contains(phrase)){
		return true;
	    }
	}
	return false;
    }
    private boolean hasLetter(String phrase) {
	for(int i = 0; i < phrase.length(); i++){
	    if(Character.isLetter(phrase.charAt(i))){
		return true;
	    }
	}
	return false;
    }
    private void runTranslation(MosesConfig mosesConfig, List<String> ids, List<String> froms) throws IOException, InterruptedException {
	PrintWriter pw = new PrintWriter(new FileWriter(mosesConfig.tempdir + "/temp.true"));
	PrintWriter pwID = new PrintWriter(new FileWriter(mosesConfig.tempdir + "/temp.ids"));
	for(int i = 0 ; i < ids.size(); i++){
	    pw.println(froms.get(i));
	    pwID.println(ids.get(i));
	}
	pw.close();
	pwID.close();
	
	System.out.println("tokenizing... ");

//	CommandResult cr = CommandRunner.runCommand(mosesConfig.mosesdir + "/mosesdecoder/scripts/tokenizer/tokenizer.perl -l en < " 
//		+ mosesConfig.tempdir + "/temp.txt > " + mosesConfig.tempdir + "/temp.tok", true);
//	System.out.println(cr.getStdOut());
//	CommandRunner.runCommand(mosesConfig.mosesdir + "/mosesdecoder/scripts/recaser/truecase.perl --model " 
//		+ mosesConfig.mosesdir + "/corpus/truecase-model.en < " + mosesConfig.tempdir + "/temp.tok > " 
//		+ mosesConfig.tempdir + "/temp.true", true);
	System.out.println("translating... ");
	
	CommandRunner.runCommand("nohup nice " + mosesConfig.mosesdir + "/mosesdecoder/bin/moses -f " 
		+ mosesConfig.workdir + "/moses.ini < " + mosesConfig.tempdir + "/temp.true > " + mosesConfig.tempdir
		+ "/temp.trans 2> " + mosesConfig.tempdir + "/test.out", true);
	CommandRunner.runCommand("cat " + mosesConfig.tempdir + "/temp.trans >> " + mosesConfig.tempdir + "/final.trans", true);
	CommandRunner.runCommand("cat " + mosesConfig.tempdir + "/temp.ids >> " + mosesConfig.tempdir + "/final.ids", true);

    }
    
}
