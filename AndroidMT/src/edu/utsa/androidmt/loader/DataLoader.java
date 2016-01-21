package edu.utsa.androidmt.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import edu.usta.androidmt.model.PhrasePair;
import edu.usta.androidmt.model.SentencePair;

public class DataLoader {
    private Hashtable<String, SentencePair> idSentenceTable = new Hashtable<String, SentencePair>();
    private Hashtable<String, List<PhrasePair>> phraseTable = new Hashtable<String, List<PhrasePair>>();
    private Hashtable<String, List<String>> idContextTable = new Hashtable<String, List<String>>();
    private Hashtable<PhrasePair, Integer> phraseLineTable = new Hashtable<PhrasePair, Integer>();
    private List<String> phraseLines = new ArrayList<String>();
    
    public void loadAll(DataLoaderConfig conf, boolean training) throws IOException{
	this.loadPhraseTable(conf.getPhraseTablePath());
	this.loadInputData(conf.getIDPath(), conf.getOriPath(), conf.getTransPath(), training);
	this.loadContexts(conf.getContextsPath());
    }

    private void loadPhraseTable(String path) throws IOException{
	BufferedReader in = new BufferedReader(new FileReader(path));
	int lineNum = 0;
	for(String line = in.readLine(); line!=null; line = in.readLine()){
	    this.phraseLines.add(line);
	    String[] parts = line.split(Pattern.quote("|||"));
	    String from = parts[0].trim();
	    String to = parts[1].trim();
	    String scores = parts[2].trim();

	    double prop = Double.parseDouble(scores.split(" ")[2].trim());
	    PhrasePair pp = new PhrasePair(from, to, prop);
	    this.phraseLineTable.put(pp, lineNum);
	    List<PhrasePair> pairs = phraseTable.get(from);
	    if(pairs == null){
		pairs = new ArrayList<PhrasePair>();
		phraseTable.put(from, pairs);
	    }
	    pairs.add(pp);
	    lineNum = lineNum + 1;
	}
	in.close();
    }
    private void loadInputData(String idPath, String oriPath, String transPath, boolean training) throws IOException{
	int prefix = 0;
	if(training){
	    prefix  = DataLoaderConfig.LAN.equals("es")?DataLoaderConfig.PREFIX_ES : DataLoaderConfig.PREFIX_CH;
	}
	List<String> ids = fetchLines(idPath, 0);
	List<String> froms = fetchLines(oriPath, prefix);
	List<String> tos = fetchLines(transPath, prefix);
	if(ids.size()==froms.size() && froms.size() == tos.size()){
	    for(int i = 0; i < froms.size(); i++){
		String id = ids.get(i);
		String from = froms.get(i);
		String to = tos.get(i);
		SentencePair sp = new SentencePair(from, to, id);
		this.idSentenceTable.put(id, sp);
	    }
	}
    }

    public static List<String> fetchLines(String path, int start) throws IOException {
	BufferedReader in = new BufferedReader(new FileReader(path));
	List<String> lines = new ArrayList<String>();
	int counter = 0;
	for(String line = in.readLine(); line!=null; line = in.readLine()){
	    counter = counter + 1;
	    if(counter > start){
		lines.add(line);
	    }
	}
	in.close();
	return lines;
    }

    private void loadContexts(String path) throws IOException{
	File f = new File(path);
	for(String txt : f.list()){
	    BufferedReader in = new BufferedReader(new FileReader(path + "/" + txt));
	    for(String line = in.readLine(); line!=null; line = in.readLine()){
		String[] items = line.split("~");
		String id = txt + ":" + items[0].trim();
		List<String> contextWords = new ArrayList<String>();
		this.idContextTable.put(id, contextWords);
		
		for(int i = 2; i< items.length; i++){
		    String context = items[i].trim();
		    if(!context.contains("smali") && !context.contains("Not Found") && context.length() > 0){
			String[] conSents = context.split(" ,");
			for(String sen : conSents){
			    String[] words = sen.split(" ");
			    for(String word:words){
				if(word.trim().length() > 0 && !StopWords.isStopword(word)){
				    contextWords.add(Stemmer.stemWord(word.trim().toLowerCase()));
				}
			    }
			}
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

    public static void writeLines(String path, List<String> lines) throws IOException {
	PrintWriter pw = new PrintWriter(new FileWriter(path));
	for(String line : lines){
	    pw.println(line.replaceAll("&quot;", ""));
	}
	pw.close();
    }
}
