package edu.utsa.cs.latentincomp.util;

import java.io.File;
import java.util.Set;

public class FileManager {
    public static void recursiveDeleteDir(String path){
	File pathFile = new File(path);
	if(!pathFile.exists()){
	    return;
	}
	if(pathFile.isDirectory()){
		for(String subPath : pathFile.list()){
		    recursiveDeleteDir(path + "/"  + subPath);
		}
	    if(pathFile.list().length == 0){
		    pathFile.delete();
	    }
	}else{
	    pathFile.delete();
	}
    }
    public static void recursiveFetchFile(String path, Set<String> files, String postfix) {
	File pathFile = new File(path);
	if(!pathFile.isDirectory()){
	    if(path.endsWith(postfix)){
		files.add(path);
	    }
	}else{
	    for(String subfile : pathFile.list()){
		recursiveFetchFile(path + '/' + subfile, files, postfix);
	    }
	}
    }

}
