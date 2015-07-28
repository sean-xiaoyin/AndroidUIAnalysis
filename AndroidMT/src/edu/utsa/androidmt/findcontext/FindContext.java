/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.utsa.androidmt.findcontext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
public class FindContext {

    public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException{
	String appsPath = "C:/personal/top500_out";
	String outputDir = "C:/personal/top500_out_context";
	File appsDir = new File(appsPath);
	for(String name : appsDir.list()){
	    runIndividual(appsPath + "/" + name, outputDir);
	}
    }
    
    /**
     * @param args the command line arguments
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws Exception 
     */
    public static void runIndividual(String path, String outputPath) throws ParserConfigurationException, SAXException, IOException {
        
	String name = path.substring(path.lastIndexOf('/'));
        File file = new File(path + "/res/values/strings.xml");
        if(!file.exists()){
            file = new File(path + "/res/values-hdpi/strings.xml");
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        
        List<String> idList = new ArrayList<String>();
        Hashtable<String, CentralText> sTable = CentralTextGen(doc, idList);
        
        
        // generate hash table 
        Hashtable<String, String> TextID_table = HashTextID(doc); 
        
        String outName = outputPath + "/" + name + ".txt";
        File filew = new File(outName);
        FileWriter fileWriter = new FileWriter(filew,true);
        
        File f = new File(path + "/res");
        ContextForOneStr(sTable, f, TextID_table);
        
        for ( int i = 0; i < idList.size(); i++) {             
            CentralText ct = sTable.get(idList.get(i));
            if (!ct.isFound()) {
                ct.AddContextNotFound();
            }else if (ct.GetContext().size()==0) {
                ct.AddContextNoNeighbor();
            }

//          sList.get(i).SetCentralText(t);
//          fileWriter.write(sList.get(i).GetTextID() + "~ " + sList.get(i).GetTextString().replaceAll("[\n\r]", "") + "~ " + sList.get(i).GetContext().toString().replaceAll("[\n\r]", "") + "\n");
            WriteText(ct, fileWriter);
        }
        
        fileWriter.flush();
	fileWriter.close();
        
    }
    
    public static void WriteText (CentralText st, FileWriter fileWriter) throws IOException {
        fileWriter.write(st.GetTextID() + "~ " + st.GetTextString().replaceAll("[\n\r]", ""));
        
        for (String s : st.GetContext()) {
            fileWriter.write("~ " + s.replaceAll("[\n\r]", ""));
        }
        fileWriter.write("\n");
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
     
    
    public static Hashtable<String, CentralText> CentralTextGen ( Document doc, List<String> idList ) {
        
        
        Element elem = doc.getDocumentElement();
        NodeList cl = elem.getChildNodes();
        Hashtable<String, CentralText> sTable = new Hashtable<String, CentralText>();
        
        int i;
        for (i = 0; i < cl.getLength(); i++) {
            Node node = cl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
            
                Element elment = (Element) node;

                // create object centraltext for every strings in strings.xml
                CentralText tmp = new CentralText();
                String id = elment.getAttribute("name");
                String text = node.getTextContent();
                tmp.SetTextID(id);
                tmp.SetTextString(text);
                idList.add(id);
                sTable.put(id, tmp);
            }
        }
        
        return sTable;
    }
    
    public static List<String> ContextFromOneFile (Document doc, Hashtable<String, String> textID_table) { // return strings pack for one file
        
        NodeList cl = doc.getElementsByTagName("*");
        List<String> sList = new ArrayList<String>();
        
        int i,j;
        for (i = 0; i < cl.getLength(); i++) { //All child element nodes loop
            Node node = cl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {   
                NamedNodeMap al = node.getAttributes();
                for (j = 0; j < al.getLength(); j++) {  // attribute loop
                    Node attr = al.item(j);
                    if (attr.getNodeType() == Node.ATTRIBUTE_NODE && attr.getTextContent().contains("@string") ) {
                	String id = attr.getTextContent().substring(8);
                	if(textID_table.get(id)!=null){
                	    sList.add(id);
                	}
                    }
                }              
            }
        }
        
        return sList;
    }
    
    public static void ContextForOneStr (Hashtable<String, CentralText> sTable, File f, Hashtable<String, String> TextID_table ) throws IOException, ParserConfigurationException 
    {
        List<String> xmlpaths = fetchXmls(f.getAbsolutePath());        
        for(String name : xmlpaths){
	    File file = new File(name);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            try{
        	Document doc = db.parse(file);
            // Collect string context from every file
            List<String> fileIdList = ContextFromOneFile(doc, TextID_table);
            
            for (String id : fileIdList) {  // meet target string
        	CentralText ct = sTable.get(id);
        	ct.setFound();
        	for(String id2 : fileIdList){
        	    if(!id2.equals(id)){
        		ct.AddContext(TextID_table.get(id2));
        	    }
        	}
            }
            }catch(SAXException e){
        	System.err.println(name + " cannot be parsed");
            }catch(IOException e){
        	System.err.println(name);
            }

        }
    }

    private static List<String> fetchXmls(String path) {
	List<String> xmls = new ArrayList<String>();
	File dir = new File(path);
	for(String name : dir.list()){
	    File f = new File(path + "/" + name);
	    if(!f.isDirectory() && name.endsWith(".xml")){
		xmls.add(path + "/" + name);		
	    }else if(f.isDirectory()){
		if(!name.startsWith("values")){
		    xmls.addAll(fetchXmls(path + "/" + name));
		}
	    }
	}
	return xmls;
    }
    
    
}