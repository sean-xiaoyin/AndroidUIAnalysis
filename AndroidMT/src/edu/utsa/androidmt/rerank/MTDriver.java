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
	PropModel model = modelFile.exists() ? loadModel(trainConfig.getContextModelPath()) : generateModel(trainConfig);
	System.out.println("performing translation...");
	translate(model, DataLoaderConfig.defaultTestConfig(), "");
    }
    
    public PropModel generateModel(DataLoaderConfig config) throws IOException{
	DataLoader loader = new DataLoader();
	loader.loadAll(config);
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
	DataLoader loader = new DataLoader();
	loader.loadAll(config);
	
	PropUpdater update = new PropUpdater(model, config.getPhraseTablePath(), loader.getPhraseLineTable(), loader.getPhraseLines());
	
	List<String> ids = new ArrayList<String>();
	ids.addAll(loader.getIdSentenceTable().keySet());
	System.out.println("translating sentences, total " + ids.size());
	int count = 0;

	List<List<String>> batches = fetchBatches(ids, loader);
	
	for(List<String> batch : batches){
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
	    System.out.println("translating batch " + count + ", total sentences " + froms.size());
	    update.updateProp(froms, contexts);
            runTranslation(MosesConfig.defaultConfig(), froms);
	}
    }

    private List<List<String>> fetchBatches(List<String> ids, DataLoader loader) {
	List<List<String>> batches = new ArrayList<List<String>>();
	List<Set<String>> phraseSets = new ArrayList<Set<String>>();
	for(String id : ids){
	    String from = loader.getIdSentenceTable().get(id).getFrom();
	    Set<String> phrases = SentencePair.getPhrases(from, 1);
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
	return batches;
    }
    private boolean intersect(Set<String> phrases, Set<String> phraseSet) {
	// TODO Auto-generated method stub
	return false;
    }
    private void runTranslation(MosesConfig mosesConfig, List<String> froms) throws IOException, InterruptedException {
	PrintWriter pw = new PrintWriter(new FileWriter(mosesConfig.tempdir + "/temp.txt"));
	for(String from : froms){
	    pw.println(from);
	}
	pw.close();
	System.out.println("tokenizing... ");

	CommandResult cr = CommandRunner.runCommand(mosesConfig.mosesdir + "/mosesdecoder/scripts/tokenizer/tokenizer.perl -l en < " 
		+ mosesConfig.tempdir + "/temp.txt > " + mosesConfig.tempdir + "/temp.tok", true);
	System.out.println(cr.getStdOut());
	CommandRunner.runCommand(mosesConfig.mosesdir + "/mosesdecoder/scripts/recaser/truecase.perl --model " 
		+ mosesConfig.mosesdir + "/corpus/truecase-model.en < " + mosesConfig.tempdir + "/temp.tok > " 
		+ mosesConfig.tempdir + "/temp.true", true);
	System.out.println("translating... ");
	
	CommandRunner.runCommand("nohup nice " + mosesConfig.mosesdir + "/mosesdecoder/bin/moses -f " 
		+ mosesConfig.workdir + "/moses.ini < " + mosesConfig.tempdir + "/temp.true > " + mosesConfig.tempdir
		+ "/temp.trans 2> " + mosesConfig.tempdir + "/test.out", true);
	CommandRunner.runCommand("cat " + mosesConfig.tempdir + "/temp.trans >> " + mosesConfig.tempdir + "/final.trans", true);
    }
    
}
