package edu.utsa.androidmt.loader;

public class DataLoaderConfig {

    private String phraseTablePath;
    private String contextsPath;
    private String idPath;
    private String oriPath;
    private String transPath;
    private String cmodelPath;

    public static String LAN = "es";
    public static String WORK_NAME = "filter-context-" + LAN;
    public static String HOME_DIR = "/home/sean/projects/ASE_ML";
    public static int PREFIX_ES = 174729;
    public static int PREFIX_CH = 201757;
    
    public static DataLoaderConfig defaultTrainConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdirs/" + WORK_NAME + "/phrase-table.0-0.1.1";
	conf.idPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/train.ids";
	conf.oriPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/train.clean.en";
	conf.transPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/train.clean" + LAN;
	conf.contextsPath = HOME_DIR + "/uitrans/context_all";
	conf.cmodelPath = "/home/sean/projects/ASE_ML/uitrans/temp/prop.model";
	return conf;
    }

    public static DataLoaderConfig defaultTestConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdirs/" + WORK_NAME + "/phrase-table.0-0.1.1";
	conf.idPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/test.ids";
	conf.oriPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/test.true.en";
	conf.transPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/test.true." + LAN;
	
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
    
    public String getContextsPath() {
	return this.contextsPath;
    }

    public String getOriPath() {
	return this.oriPath;
    }

    public String getIDPath() {
	return this.idPath;
    }

    public String getTransPath() {
	return this.transPath;
    }
}
