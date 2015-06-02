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
 * @author Xue
 * @version 1.0
 * @since 2015-06-02
 */
public class XMLNodeTree implements java.io.Serializable{


    private static final long serialVersionUID = 1L;

    /*
     * docs:            A list of documents for all files in one application. 
     * childTable:      A table of nodes and their child nodes.  
     * idNodeTable:     A table of strings and their corresponding nodes.
     * HashIDText:      A hash table of id and text for one application.
     */
    List<Document> docs;
    Hashtable<Node, Set<Node>> childTable = new Hashtable<>();
    Hashtable<String, Set<Node>> idNodeTable = new Hashtable<>();
    Hashtable<String, String> HashIDText = new Hashtable<>();
    
    /**
     * This method is a constructor for XMLNodeTree object.
     * In this method, the argument docs and HashIDText are constructed. And it also 
     * called the fetchAll() method.
     * 
     * @param docs          A list of documents for all files in one application.
     * @param HashIDText    A hash table of id and text for one application.
     * @throws Exception 
     */
    public XMLNodeTree(List<Document> docs, Hashtable<String, String> HashIDText) throws Exception {
        this.docs = docs;
        this.HashIDText = HashIDText;
        fetchAll();
    }
    
    /**
     * This method is used to call fetchAllChildNodes() method for every given documents.
     * 
     * @throws Exception 
     */
    public void fetchAll() throws Exception {
        for (Document doc : docs) {
            Node elem = doc.getDocumentElement();
            fetchAllChildNodes(elem);
        }
    }
    
    /**
     * This method is used to get a node's corresponding node set in childTable.
     * Given a node, find its corresponding node set in childTable.
     * 
     * @param node       The key node to find its node set value pair.
     * @return           The corresponding node set.
     */
    public Set<Node> getAllChildNode(Node node) { 
        return this.childTable.get(node);
    }
    
    /**
     * returns a set of node with recursion to generate childTables under a given root node. 
     * Only the node that contains the strings in attribute will be saved in childTable.
     * 
     * @param node the root node for the recursion
     * @return a set of nodes 
     * @throws Exception 
     */
    public Set<Node> fetchAllChildNodes (Node node) throws Exception {
            
        Set<Node> childset = new HashSet<>();  // child nodeset for one node 
        if ( node.hasChildNodes() == false )  { // has no child node
            if (node.getNodeType() == Node.ELEMENT_NODE) {   
                if ( hasStrings(node) ) {
                    childset.add(node);
                    fetchIdNode(node);
                }               
            }   
        }
        else {
            if ( hasStrings(node) ) {
                    childset.add(node);
                    fetchIdNode(node);
                }
            NodeList childlist = node.getChildNodes();    
            for ( int i = 0; i < childlist.getLength(); i++) {
                Node c = childlist.item(i);
                if (c.getNodeType() == Node.ELEMENT_NODE) {
                    Set<Node> set_tmp = fetchAllChildNodes(c); 
                    childset.addAll(set_tmp);
                }
            }   
        }
        if (childset.isEmpty() == false) {
            childTable.put(node, childset);
        }
        return childset;
    }
    
    /**
     * This method is used to detect whether the given node has a string or not.
     * As long as the node has at least one string parameter in its attributes, 
     * we consider this node has strings. And return true.
     * 
     * @param node     the node need to be test whether it contains strings or not.
     * @return         the boolean parameter shows whether it has strings or not.
     */
    public static boolean hasStrings (Node node) {
        boolean flag = false;
        NamedNodeMap al = node.getAttributes();
            for (int j = 0; j < al.getLength(); j++) {  // attribute loop
                Node attr = al.item(j);
                if (attr.getNodeType() == Node.ATTRIBUTE_NODE && attr.getTextContent().contains("@string") ) {
                    flag = true;
                }
            }
        return flag;
    }
    
    /**
     * This method is used to generate a string and its corresponding nodes.
     * In this method, there is a loop from the given root node. It loops every node in
     * the layouts and record the node once it has attribute "string". And put the records
     * in idNodeTable.
     * 
     * @param node      the given beginning root node
     */
    public void fetchIdNode(Node node){
        NamedNodeMap al = node.getAttributes();
        for (int j = 0; j < al.getLength(); j++) {  // attribute loop
            Node attr = al.item(j);
            if (attr.getNodeType() == Node.ATTRIBUTE_NODE && attr.getTextContent().contains("@string") ) {
                String str = attr.getTextContent().substring(8);
                if(idNodeTable.get(str)==null){
                    idNodeTable.put(str, new HashSet<Node>()); 
                }
                idNodeTable.get(str).add(node);
            }                    
        }
    }

    /**
     * This method is used to get Siblings for a given string.
     * In this method, first it find out all the nodes that contain the given string. 
     * Then according to the parameter level to get different siblings.
     * When level is a positive integer i or zero, it search siblings upward for i layouts.
     * When level is -1, it search siblings upward till root node
     * .
     * @param id        The given string used to search.
     * @param level     Then searching parameter.
     * @return          A list of Context class contains the nodes witch have string id and their siblings.
     */
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
            else if ( level > -1){ //iterate to specific level
                for ( int i = 0; i < level; i++){
                    while (parentnode.getParentNode()!=null) {
                        childnode = parentnode;
                        parentnode = parentnode.getParentNode();
                    }
                    
                }
            }
            else {
                System.out.println("The level should be at least -1.");
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
    
    /**
     * This method is used to generate a Text and ID table for one Application.
     * Given the document of value directory. It generate a table for all the IDs that used
     * in this application and their corresponding text in English.
     * 
     * @param doc       The document for one specific directory.
     * @return          A hash table for id and text.
     */
    public static Hashtable<String, String> HashTextID ( Document doc ) {  // Hashtable for text and id
        
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
    
    /**
     * This Method is used to generate a list of documents for all the file in one Application.
     * Given the application path, it generate a list of documents for all the file in this path.
     * The files exclude value folder and all the not XML files.
     * 
     * @param apkPath       Application path.
     * @return              A list of documents for all the files in application path.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public static List<Document> resdocument ( String apkPath ) throws ParserConfigurationException, SAXException, IOException  { 
        List<Document> docList = new ArrayList<>();
        String respath = apkPath + "/res";
        File f = new File(respath);
        for(String name : f.list()){    
            if ( name.contains("values") == false ) {
                File rf = new File(f.getAbsolutePath() + "/" + name);
                for (String rname : rf.list()) {  
                    //System.out.println(rname);
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
        }
        return docList;    
    }
    
    /**
     * This Method is used to generate a document for one Application's strings file.
     * 
     * @param apkPath       Application path.
     * @return              A document for one Application's strings file.
     * @throws Exception 
     */
    public static Document idTextDocument ( String apkPath ) throws Exception {
        String idTextpath = apkPath + "/res/values/strings.xml";
        File file = new File(idTextpath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        return doc;
    } 
    
    /**
     * This method is used to generate a XMLNodeTree object given one application path.
     * In this method, it calls resdocument and HashTextID methods to generate a XMLNodeTree object.
     * And then return it.
     * @param apkPath       Application path.
     * @return
     * @throws Exception 
     */
    public static XMLNodeTree handleApk (String apkPath) throws Exception {
        
        XMLNodeTree xnt = new XMLNodeTree(XMLNodeTree.resdocument(apkPath), XMLNodeTree.HashTextID(XMLNodeTree.idTextDocument(apkPath)) );
        return xnt;
    }
    
    /**
     * The main method is used to generate a XMLNodeTree object for every application and write it into a file.
     * @param args         not used.
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
       //iterate over all apks
        String apkFolder = "C:/Xue/ml/SampleApks/TopApk";
        File f = new File(apkFolder);
        for(String name : f.list()) {
            System.out.println(name);
            String apkPath = apkFolder + "/" + name;
            String outpath = "C:/Xue/ml/SampleApks/output/" + name + ".ser";
            File out = new File(outpath);
            if(!out.exists()) {
                out.createNewFile();
            }
            
            XMLNodeTree xnt = handleApk(apkPath);
            FileOutputStream outxnt = new FileOutputStream(outpath, true);
            ObjectOutputStream oos = new ObjectOutputStream(outxnt);
            //System.out.println(xnt.childTable.keySet().size());
            for(Node key : xnt.childTable.keySet()){
            	//System.out.println(key.getNodeName());
            	//System.out.println(key.getNamespaceURI());
                oos.writeObject(xnt.childTable.get(key));            	
            }
            oos.close();
            outxnt.close();
        }

    }
    
}