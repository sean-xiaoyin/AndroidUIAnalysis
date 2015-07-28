package edu.utsa.androidmt.findcontext;

import java.io.File;
import java.io.IOException;

import edu.utsa.androidmt.rerank.CommandRunner;
import edu.utsa.androidmt.rerank.CommandRunner.CommandResult;

public class BatchApkTool {
    public static void main(String args[]) throws IOException, InterruptedException{
	String path = "C:/personal/top500";
	File f = new File(path);
	for(String name : f.list()){
	    CommandResult cr = CommandRunner.runCommand("cmd /c apktool d " + path.replace('/', '\\') + "\\" + name, new File("C:/personal/top500_out"));
	    System.out.println(cr.getStdOut() + "\n" + cr.getErrOut());
	}
    }
}
