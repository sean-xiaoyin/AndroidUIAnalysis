/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package findcontext;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Xue
 */
public class FindContext {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        File file = new File("C:\\Xue\\ml\\strings.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        
        List<CentralText> sList = CentralTextGen(doc);
        
        // generate hash table 
        Hashtable TextID_table = HashTextID(doc); 
        
        String outName = "C:\\Xue\\ml\\output.txt";
        File filew = new File(outName);
        FileWriter fileWriter = new FileWriter(filew,true);
        
        File f = new File("C:/Xue/ml/Testrun");
        
        for ( int i = 0; i < sList.size(); i++) {
             
            CentralText t = ContextForOneStr(sList, i, f, TextID_table);
            sList.get(i).SetCentralText(t);
             
            //fileWriter.write(sList.get(i).GetTextID() + "~ " + sList.get(i).GetTextString().replaceAll("[\n\r]", "") + "~ " + sList.get(i).GetContext().toString().replaceAll("[\n\r]", "") + "\n");
            WriteText(sList.get(i), fileWriter);
        }
        
        fileWriter.flush();
	fileWriter.close();
        
    }
    
    public static void WriteText (CentralText st, FileWriter fileWriter) throws Exception {
        fileWriter.write(st.GetTextID() + "~ " + st.GetTextString().replaceAll("[\n\r]", ""));
        
        for (String s : st.GetContext()) {
            fileWriter.write("~ " + s.replaceAll("[\n\r]", ""));
        }
        fileWriter.write("\n");
    }
        
    
    public static Hashtable HashTextID ( Document doc ) { //Hashtable for text and id
        
        Hashtable textId_table = new Hashtable();
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
     
    
    public static List<CentralText> CentralTextGen ( Document doc ) throws Exception {
        
        
        Element elem = doc.getDocumentElement();
        NodeList cl = elem.getChildNodes();
        List<CentralText> sList = new ArrayList<CentralText>();
        
        int i;
        for (i = 0; i < cl.getLength(); i++) {
            Node node = cl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
            
                Element elment = (Element) node;

                // create object centraltext for every strings in strings.xml
                CentralText tmp = new CentralText();
                tmp.SetTextID(elment.getAttribute("name"));
                tmp.SetTextString(node.getTextContent());

                sList.add(tmp);
            }
        }
        
        return sList;
    }
    
    public static List<String> ContextFromOneFile (Document doc, Hashtable idtext_table) { // return strings pack for one file
        
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
                        String str = idtext_table.get(attr.getTextContent().substring(8)).toString();
                        sList.add(str);
                    }
                }              
            }
        }
        
        return sList;
    }
    
    public static CentralText ContextForOneStr (List<CentralText> sList, int n, File f, Hashtable TextID_table ) throws Exception {
        
        String tstr = sList.get(n).GetTextString(); //target string
        boolean Neighbor = false;  // false: no neighbor, true: have neighbor
        boolean Found = false;  // false: not found, true: found
        
        for(String name : f.list()){
            
            String fullName = f.getAbsolutePath() + "/" + name;
            File file = new File(fullName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            
            // Collect string context from every file
            List<String> fileStringList = ContextFromOneFile(doc, TextID_table);
            
            if (fileStringList.contains(tstr)) {  // meet target string
                
                Found = true;
                if (fileStringList.size() != 1) {   // have neighbors
                    Neighbor = true;
                    int index = fileStringList.indexOf(tstr);               
                    sList.get(n).AddContext(fileStringList, index);
                }
                else {
                    // no neighbor
                }                  
            }
            else { 
                // does not find the target string    
            }
                
        }
        
        if ( Found == false ) {
            sList.get(n).AddContextNotFound();
        }
        else if ( Neighbor == false ) {
            sList.get(n).AddContextNoNeighbor();
        }

        return sList.get(n);
    }
    
    
}

