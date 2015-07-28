package edu.utsa.androidmt.loader;

public class DataLoaderConfig {

    private String phraseTablePath;
    private String inputDataPath;
    private String contextsPath;
    private String cmodelPath;
    public static String LAN = "es";
    public static String WORK_NAME = "work_nocontext_" + LAN;
    public static String HOME_DIR = "/home/sean/projects/ASE_ML";
    
    public static DataLoaderConfig defaultTrainConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdir/" + WORK_NAME + "/train/model/phrase-table.txt";
	conf.inputDataPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub1/train";
	conf.contextsPath = HOME_DIR + "/uitrans/context_all";
	conf.cmodelPath = "/home/sean/projects/ASE_ML/uitrans/temp/prop.model";
	return conf;
    }

    public static DataLoaderConfig defaultTestConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdir/" + WORK_NAME + "/train/model/phrase-table.txt";
	conf.inputDataPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub1/test";
	conf.contextsPath = HOME_DIR + "/uitrans/context_all";
	conf.cmodelPath = "/home/sean/projects/ASE_ML/uitrans/temp/prop.model";
	return conf;
    }

    public String getContextModelPath() {
	return this.cmodelPath;
    }

    public String getPhraseTablePath() {
	return this.phraseTablePath;
    }

    public String getInputDataPath() {
	return this.inputDataPath;
    }
    
    public String getContextsPath() {
	return this.contextsPath;
    }
}
