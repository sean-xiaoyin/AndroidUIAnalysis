package edu.utsa.androidmt.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.usta.androidmt.model.PhrasePair;
import edu.usta.androidmt.model.SentencePair;

public class DataLoader {
    private Hashtable<String, SentencePair> idSentenceTable = new Hashtable<String, SentencePair>();
    private Hashtable<String, List<PhrasePair>> phraseTable = new Hashtable<String, List<PhrasePair>>();
    private Hashtable<String, List<String>> idContextTable = new Hashtable<String, List<String>>();
    private Hashtable<PhrasePair, Integer> phraseLineTable = new Hashtable<PhrasePair, Integer>();
    private List<String> phraseLines = new ArrayList<String>();
    
    public void loadAll(DataLoaderConfig conf) throws IOException{
	this.loadPhraseTable(conf.getPhraseTablePath());
	this.loadTrainingData(conf.getInputDataPath());
	this.loadContexts(conf.getContextsPath());
    }

    private void loadPhraseTable(String path) throws IOException{
	BufferedReader in = new BufferedReader(new FileReader(path));
	int lineNum = 0;
	for(String line = in.readLine(); line!=null; line = in.readLine()){
	    this.phraseLines.add(line);
	    String[] parts = line.split("|||");
	    String from = parts[0];
	    String to = parts[1];
	    String scores = parts[2];
	    double prop = Double.parseDouble(scores.split(" ")[0].trim());
	    PhrasePair pp = new PhrasePair(from, to, prop);
	    this.phraseLineTable.put(pp, lineNum);
	    List<PhrasePair> pairs = phraseTable.get(pp);
	    if(pairs == null){
		pairs = new ArrayList<PhrasePair>();
		phraseTable.put(from, pairs);
	    }
	    pairs.add(pp);
	    lineNum = lineNum + 1;
	}
	in.close();
    }
    private void loadTrainingData(String path) throws IOException{
	BufferedReader in = new BufferedReader(new FileReader(path));
	for(String line = in.readLine(); line!=null; line = in.readLine()){
	    if(line.startsWith("English:")){
		String id = line.substring(9);
		String from = in.readLine().substring(8);
		String to = in.readLine().substring(10);
		SentencePair sp = new SentencePair(from, to, id);
		this.idSentenceTable.put(id, sp);
	    }
	}
	in.close();
    }

    private void loadContexts(String path) throws IOException{
	File f = new File(path);
	for(String txt : f.list()){
	    BufferedReader in = new BufferedReader(new FileReader(path));
	    for(String line = in.readLine(); line!=null; line = in.readLine()){
		String[] items = line.split("~");
		String id = txt + "_" + items[0].trim();
		List<String> contextWords = new ArrayList<String>();
		this.idContextTable.put(id, contextWords);
		String[] contexts = items[2].substring(1, items[2].length() - 1).split(",");
		for(String context : contexts){
		    String[] words = context.split(" ");
		    for(String word : words){
			contextWords.add(word.trim());
		    }
		}
	    
	    }		
	    in.close();
	}
    }

    public Hashtable<String, SentencePair> getIdSentenceTable() {
	return this.idSentenceTable;
    }

    public Hashtable<String, List<String>> getIdContextTable() {
	return this.idContextTable;
    }

    public Hashtable<String, List<PhrasePair>> getPhraseTable() {
	return this.phraseTable;
    }

    public Hashtable<PhrasePair, Integer> getPhraseLineTable() {
	return this.phraseLineTable;
    }

    public List<String> getPhraseLines() {
	return this.phraseLines;
    }
}
