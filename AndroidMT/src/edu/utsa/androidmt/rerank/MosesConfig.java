package edu.utsa.androidmt.rerank;

import edu.utsa.androidmt.loader.DataLoaderConfig;

public class MosesConfig {

    public String workdir;
    public String mosesdir;
    public String tempdir;
    
    public static MosesConfig defaultConfig() {
	MosesConfig conf = new MosesConfig();
	conf.workdir = DataLoaderConfig.HOME_DIR + "/uitrans/workdir/work_nocontext_es";
	conf.mosesdir = DataLoaderConfig.HOME_DIR;
	conf.tempdir = DataLoaderConfig.HOME_DIR + "/uitrans/temp";
	return conf;
    }

}
