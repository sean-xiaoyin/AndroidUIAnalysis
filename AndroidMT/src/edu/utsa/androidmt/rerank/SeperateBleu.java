package edu.utsa.androidmt.rerank;

import java.io.IOException;

public class SeperateBleu {
    public static void main(String args[]) throws IOException, InterruptedException{
	MosesConfig conf = MosesConfig.defaultConfig();
	MTDriver d = new MTDriver();
	d.reportSeparate(conf);
    }
}
