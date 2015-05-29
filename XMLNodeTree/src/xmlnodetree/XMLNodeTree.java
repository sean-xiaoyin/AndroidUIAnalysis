/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlnodetree;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Xue
 */
public class XMLNodeTree implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * @param args the command line arguments
     */
    List<Document> docs;
    Hashtable<Node, Set<Node>> childTable = new Hashtable<>();
    Hashtable<String, Set<Node>> idNodeTable = new Hashtable<>();
    Hashtable<String, String> HashIDText = new Hashtable<>();
    
    public XMLNodeTree(List<Document> docs, Hashtable<String, String> HashIDText) throws Exception {
        this.docs = docs;
        this.HashIDText = HashIDText;
        fetchAll();
    }
    
//    public Hashtable<String, Set<Node>> setidNodeTable(String id, String apkPath, int level) throws Exception { 
//        docs = resdocument(apkPath);
//        for (Document doc : docs) {
//            List<Context> nodelist = getSiblings(id,level);
//            if ( nodelist.size() == 1) {
//                idNodeTable.put(id, nodelist.get(0).Getnodeset());
//            }  
//        }
//        return idNodeTable;
//    }
    
    public void fetchAll() throws Exception {
        for (Document doc : docs) {
            Node elem = doc.getDocumentElement();
            fetchAllChildNodes(elem);
        }
    }
    
    public Set<Node> fetchAllChildNodes (Node node) throws Exception {
            
        Set<Node> childset = new HashSet<>();  // child nodeset for one node
        
        if ( node.hasChildNodes() == false )  { // has no childnode
            if (node.getNodeType() == Node.ELEMENT_NODE) {   
                childset.add(node);
                fetchIdNode(node);
            }   
        }
        else {
            childset.add(node);
            fetchIdNode(node); //
            NodeList childlist = node.getChildNodes();    
            for ( int i = 0; i < childlist.getLength(); i++) {
                Node c = childlist.item(i);
                if (c.getNodeType() == Node.ELEMENT_NODE) {
                    Set<Node> set_tmp = fetchAllChildNodes(c); /////////////////////
                    childset.addAll(set_tmp);
                }
            }   
        }
	    childTable.put(node, childset);
        return childset;
    }
    
    public Set<Node> getAllChildNode(Node node) { //////////////
        return this.childTable.get(node);
    }



     public static List<Node> getNodes ( Document doc, String id ) {
        
        List<Node> nodeset = new ArrayList<>(); // nodes list contains id in attribute
        NodeList idNode = doc.getElementsByTagName("*");
        
        for (int i = 0; i < idNode.getLength(); i++) { //All child element nodes loop
            Node node = idNode.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {   
                NamedNodeMap al = node.getAttributes();
                for (int j = 0; j < al.getLength(); j++) {  // attribute loop
                    Node attr = al.item(j);
                    if (attr.getNodeType() == Node.ATTRIBUTE_NODE && attr.getTextContent().contains("@string") ) {
                        String str = attr.getTextContent().substring(8);
                        if (str.equals(id)) {
                            nodeset.add(node);
                        }
                    }
                }              
            }
        }
    return nodeset;   
    }
    
    public void fetchIdNode(Node node){
        NamedNodeMap al = node.getAttributes();
        for (int j = 0; j < al.getLength(); j++) {  // attribute loop
            Node attr = al.item(j);
            if (attr.getNodeType() == Node.ATTRIBUTE_NODE && attr.getTextContent().contains("@string") ) {
                String str = attr.getTextContent().substring(8);
                if(idNodeTable.get(str)==null){
                    idNodeTable.put(str, new HashSet<Node>()); //////////////////////
                }
                idNodeTable.get(str).add(node);
            }                    
        }
    }

//    public static Set<String> fetchAllNodes ( Node node, Hashtable<Node, Set<String>> t) {
//            
//        Set<String> s = new HashSet<String>();  // attributes for one node
//        
//        if ( node.hasChildNodes() == false )  { // has no childnode
//            if (node.getNodeType() == Node.ELEMENT_NODE) {   
//                s.addAll(fetchAttributes(node));
//                t.put(node, s);
//            }    
//        }
//        else {
//            if (node.getNodeType() == Node.ELEMENT_NODE) { 
//                s.addAll(fetchAttributes(node));  
//                // has childnode
//                NodeList childlist = node.getChildNodes();
//                for ( int i = 0; i < childlist.getLength(); i++) {
//                    Node c = childlist.item(i);
//                    if (c.getNodeType() == Node.ELEMENT_NODE) {
//                        Set<String> stmp = fetchAllNodes(c, t);
//                        s.addAll(stmp);
//                    }
//                }
//                t.put(node, s);
//            }
//        }
//        return s;
//    }
    
    public List<Context> getSiblings(String id, int level){
        Set<Node> nodes = idNodeTable.get(id);
        Set<Node> childset = new HashSet<>();
        Set<Node> parentset = new HashSet<>();
        Set<Node> siblingset = new HashSet<>();
        List<Context> nodeset = new ArrayList<>();

        for (Node node : nodes) {
            Context c = new Context();
            c.Setnode(node);  // set node
            Node parentnode = node;
            Node childnode = node;
            if ( level == -1 ){ //iterate to root node
                while (parentnode.getParentNode()!=null) {
                    childnode = parentnode;
                    parentnode = parentnode.getParentNode();
                }
            }
            else { //iterate to specific level
                for ( int i = 0; i < level; i++){
                    while (parentnode.getParentNode()!=null) {
                        childnode = parentnode;
                        parentnode = parentnode.getParentNode();
                    }
                    
                }
            }                
            childset = getAllChildNode(childnode);
            parentset = getAllChildNode(parentnode);
            siblingset = parentset;
            siblingset.removeAll(childset);
            siblingset.remove(parentnode);
            c.Setnodeset(siblingset);
            nodeset.add(c);
        }
        return nodeset;
    }
    
    public static Hashtable<String, String> HashTextID ( Document doc ) { //Hashtable for text and id
        
        Hashtable<String, String> textId_table = new Hashtable<String, String>();
        Element elem = doc.getDocumentElement();
        NodeList cl = elem.getChildNodes();
        
        int i;
        for (i = 0; i < cl.getLength(); i++) {
            Node node = cl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elment = (Element) node;

                // add to hash table
                String id_key = elment.getAttribute("name");
                String text_value = node.getTextContent();
                textId_table.put(id_key, text_value);
            }
        }
        
        return textId_table;
        
    }
    
    public static Set<String> fetchAttributes (Node node){
        Set<String> ret = new HashSet<String>();
        NamedNodeMap al = node.getAttributes();
        for (int j = 0; j < al.getLength(); j++) {  // attribute loop
            Node attr = al.item(j);
            if (attr.getNodeType() == Node.ATTRIBUTE_NODE && attr.getTextContent().contains("@string") ) {
                String str = attr.getTextContent().substring(8);
                ret.add(str);
            }
        }
        return ret;
    }
    

    
    public static List<Document> resdocument ( String apkPath ) throws ParserConfigurationException, SAXException, IOException  { 
        List<Document> docList = new ArrayList<>();
        String respath = apkPath + "/res";
        File f = new File(respath);
        for(String name : f.list()){    
            
            File rf = new File(f.getAbsolutePath() + "/" + name);
            for (String rname : rf.list()) {                
                String fullname = rf.getAbsolutePath() + "/" + rname;
                
                if ( rname.contains(".xml") ) {
                    File file = new File(fullname);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(file);
                    docList.add(doc);
                }
            }
        }
        return docList;    
    }
    
    public static Document idTextDocument ( String apkPath ) throws Exception {
        String idTextpath = apkPath + "/res/values/strings.xml";
        File file = new File(idTextpath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        return doc;
    } 
   
    public static XMLNodeTree handleApk (String apkPath) throws Exception {
        
        XMLNodeTree xnt = new XMLNodeTree(XMLNodeTree.resdocument(apkPath), XMLNodeTree.HashTextID(XMLNodeTree.idTextDocument(apkPath)) );
        //xnt.docs = XMLNodeTree.resdocument(apkPath);
        //xnt.HashIDText = XMLNodeTree.HashTextID(XMLNodeTree.idTextDocument(apkPath));

        //xnt.childTable = setchildTable(apkPath);
        //xnt.idNodeTable = setidNodeTable(id, apkPath, level);
        
        return xnt;
    }
    
    public static void main(String[] args) throws Exception {
       //iterate over all apks
        String apkFolder = "/home/sean/AndroidUIAnalysis/SampleApks/TopApk";
        File f = new File(apkFolder);
        for(String name : f.list()) {
            System.out.println(name);
            String apkPath = apkFolder + "/" + name;
            String outpath = "/home/sean/AndroidUIAnalysis/SampleApks/" + name + ".ser";
            File out = new File(outpath);
            if(!out.exists()) {
                out.createNewFile();
            }
            
            XMLNodeTree xnt = handleApk(apkPath);
            FileOutputStream outxnt = new FileOutputStream(outpath, true);
            ObjectOutputStream oos = new ObjectOutputStream(outxnt);
            System.out.println(xnt.childTable.keySet().size());
            for(Node key : xnt.childTable.keySet()){
            	System.out.println(key.getNodeName());
            	System.out.println(key.getNamespaceURI());
                oos.writeObject(xnt.childTable.get(key));            	
            }
            oos.close();
            outxnt.close();
        }

    }
    
}