package edu.utsa.androidmt.rerank;

import edu.utsa.androidmt.loader.DataLoaderConfig;

public class MosesConfig {

    public String workdir;
    public String mosesdir;
    public String tempdir;
    public String refdir;
    
    public static MosesConfig defaultConfig() {
	MosesConfig conf = new MosesConfig();
	conf.workdir = DataLoaderConfig.HOME_DIR + "/uitrans/workdirs/filter-context-" + DataLoaderConfig.LAN + "-new";
	conf.mosesdir = DataLoaderConfig.HOME_DIR;
	conf.tempdir = DataLoaderConfig.HOME_DIR + "/uitrans/temp";
	conf.refdir = DataLoaderConfig.HOME_DIR + "/uitrans/allign-" + DataLoaderConfig.LAN + "-final_cross/sub_1";
	return conf;
    }

}
