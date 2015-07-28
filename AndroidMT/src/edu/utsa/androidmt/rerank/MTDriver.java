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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import edu.usta.androidmt.model.PropModel;
import edu.usta.androidmt.model.SentencePair;
import edu.utsa.androidmt.loader.DataLoader;
import edu.utsa.androidmt.loader.DataLoaderConfig;

public class MTDriver {
    public void run() throws IOException, ClassNotFoundException, InterruptedException{
	DataLoaderConfig trainConfig = DataLoaderConfig.defaultTrainConfig();
	File modelFile = new File(trainConfig.getContextModelPath());
	PropModel model = modelFile.exists() ? loadModel(trainConfig.getContextModelPath()) : generateModel(trainConfig);
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
	List<String> translated = new ArrayList<String>();
	
	PropUpdater update = new PropUpdater(model, config.getPhraseTablePath(), loader.getPhraseLineTable(), loader.getPhraseLines());
	
	List<String> froms = new ArrayList<String>();
	Hashtable<String, List<String>> contexts = new Hashtable<String, List<String>>();
	HashSet<String> existed = new HashSet<String>();
	List<String> ids = new ArrayList<String>();
	ids.addAll(loader.getIdSentenceTable().keySet());
	for(int i = 0; i < ids.size(); i++){
	    String id = ids.get(i);
	    SentencePair sp = loader.getIdSentenceTable().get(id);
	    List<String> context = loader.getIdContextTable().get(id);
	    froms.add(sp.getFrom());
	    contexts.put(sp.getFrom(), context);
	    if(isBatch(froms, existed)){		
		update.updateProp(froms, contexts);
		runTranslation(MosesConfig.defaultConfig(), froms);
		froms.clear();
		contexts.clear();
		existed.clear();
		i = i - 1;
	    }
	}
	
		
	PrintWriter pw = new PrintWriter(new FileWriter(outputPath));
	for(String line : translated){
	    pw.println(line);
	}
	pw.close();
	
    }

    private boolean isBatch(List<String> froms, HashSet<String> existed) {
	for(String from : froms){
	    Set<String> phrases = SentencePair.getPhrases(from);
	    for(String phrase : phrases){
		if(existed.contains(phrase)){
		    return true;
		}
	    }
	}
	return false;
    }

    private void runTranslation(MosesConfig mosesConfig, List<String> froms) throws IOException, InterruptedException {
	PrintWriter pw = new PrintWriter(new FileWriter(mosesConfig.tempdir + "/temp.txt"));
	for(String from : froms){
	    pw.println(from);
	}
	pw.close();
	
	CommandRunner.runCommand(mosesConfig.mosesdir + "/mosesdecoder/scripts/tokenizer/tokenizer.perl -l en < " 
		+ mosesConfig.tempdir + "/temp.txt > " + mosesConfig.tempdir + "/temp.tok");
	CommandRunner.runCommand(mosesConfig.mosesdir + "/mosesdecoder/scripts/recaser/truecase.perl --model " 
		+ mosesConfig.mosesdir + "/corpus/truecase-model.en < " + mosesConfig.tempdir + "/temp.tok > " 
		+ mosesConfig.tempdir + "/temp.true");
	
	CommandRunner.runCommand("nohup nice " + mosesConfig.mosesdir + "/mosesdecoder/bin/moses -f " 
		+ mosesConfig.workdir + "/mert-work/moses.ini < " + mosesConfig.tempdir + "/temp.true > " + mosesConfig.tempdir
		+ "/temp.trans 2> " + mosesConfig.tempdir + "/test.out");
	CommandRunner.runCommand("cat " + mosesConfig.tempdir + "/temp.trans >> " + mosesConfig.tempdir + "/final.trans");
    }
    
}
