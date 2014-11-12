package edu.utsa.cs.latentincomp.util;

import java.util.Set;

public class SetUtil {
	public static boolean intersect(Set<String> set1, Set<String> set2) {
		for(String x : set1){
			if(set2.contains(x)){
				return true;
			}
		}
		return false;
	}

}
