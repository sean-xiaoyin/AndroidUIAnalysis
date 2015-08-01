package edu.utsa.androidmt.loader;

public class DataLoaderConfig {

    private String phraseTablePath;
    private String inputDataPath;
    private String contextsPath;
    private String cmodelPath;
    public static String LAN = "es";
    public static String WORK_NAME = "filter-context-" + LAN;
    public static String HOME_DIR = "/home/sean/projects/ASE_ML";
    
    public static DataLoaderConfig defaultTrainConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdirs/" + WORK_NAME + "/phrase-table.0-0.1.1";
	conf.inputDataPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/train";
	conf.contextsPath = HOME_DIR + "/uitrans/context_all";
	conf.cmodelPath = "/home/sean/projects/ASE_ML/uitrans/temp/prop.model";
	return conf;
    }

    public static DataLoaderConfig defaultTestConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdirs/" + WORK_NAME + "/phrase-table.0-0.1.1";
	conf.inputDataPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/test";
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
