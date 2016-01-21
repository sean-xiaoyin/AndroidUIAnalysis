package edu.utsa.androidmt.loader;

public class DataLoaderConfig {

    private String phraseTablePath;
    private String contextsPath;
    private String idPath;
    private String oriPath;
    private String transPath;
    private String cmodelPath;
    private String reportPath;
    private boolean lazy;

    public static String LAN = "zh";
//    public static String WORK_NAME = "filter-context-" + LAN;
    public static String HOME_DIR = "/home/sean/projects/ASE_ML";
    public static int PREFIX_ES = 173950;
    public static int PREFIX_CH = 201757;
    
    public static DataLoaderConfig defaultTrainConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdirs/filter-context-" + DataLoaderConfig.LAN + "-new/phrase-table.0-0.1.1.back";
	conf.idPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/train.ids";
	conf.oriPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/train.true.en";
	conf.transPath = HOME_DIR + "/uitrans/allign-" + LAN + "-final_cross/sub_1/train.true." + LAN;
	conf.contextsPath = HOME_DIR + "/uitrans/context_all";
	conf.cmodelPath = "/home/sean/projects/ASE_ML/uitrans/temp/prop.model";
	conf.reportPath = "/home/sean/projects/ASE_ML/uitrans/temp/train.report";
	conf.lazy = false;
	return conf;
    }

    public static DataLoaderConfig defaultTestConfig(){
	DataLoaderConfig conf = new DataLoaderConfig();
	conf.phraseTablePath = HOME_DIR + "/uitrans/workdirs/filter-context-" + DataLoaderConfig.LAN + "-new/phrase-table.0-0.1.1.back";
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

    public boolean lazy() {
	return this.lazy;
    }

    public String getReportPath() {
	return reportPath;
    }

    public void setReportPath(String reportPath) {
	this.reportPath = reportPath;
    }
}
