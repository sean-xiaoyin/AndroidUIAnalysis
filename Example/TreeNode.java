package org.apache.commons.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode {
	String key;
	String value;
	String parent;
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	List<TreeNode> arrayChild = new ArrayList<TreeNode>();
	
	Map<String,TreeNode> childs = new HashMap<String, TreeNode>();
	
	boolean arrayElement;
	boolean primitiveElement;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Map<String, TreeNode> getChilds() {
		return childs;
	}
	public void setChilds(Map<String, TreeNode> childs) {
		this.childs = childs;
	}
	public boolean isArrayElement() {
		return arrayElement;
	}
	public void setArrayElement(boolean arrayElement) {
		this.arrayElement = arrayElement;
	}
	public boolean isPrimitiveElement() {
		return primitiveElement;
	}
	public void setPrimitiveElement(boolean primitiveElement) {
		this.primitiveElement = primitiveElement;
	}
}
