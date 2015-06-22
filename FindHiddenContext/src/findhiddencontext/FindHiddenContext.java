/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package findhiddencontext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
 * @author Xin
 */
public class FindHiddenContext {
    
    File smalidir;
    Document pubIdDoc;
    List<String> smalidocs = new ArrayList<String>();
    List<Document> resdocs = new ArrayList<Document>();
    List<CentralText> sList = new ArrayList<CentralText>();
    Hashtable<String, String> HashIDText = new Hashtable<>();
    
    public FindHiddenContext(Document pubIdDoc, File smalidir, List<CentralText> sList, Hashtable<String, String> HashIDText, List<Document> resdocs) throws Exception {
        this.pubIdDoc = pubIdDoc;
        this.smalidir = smalidir;
        this.sList = sList;
        this.HashIDText = HashIDText;
        this.resdocs = resdocs;
        runAll();    
    }
    
    public void runAll() throws Exception {
        
        smaliDocs(smalidir); // generate smalidocs
        for ( int i = 0; i < sList.size(); i++) { // for every string id in list. 
            CentralText t = findNeighbors(sList.get(i), resdocs, HashIDText);
            sList.get(i).SetCentralText(t);
            if ( sList.get(i).GetContext().toString().contains("Not Found")){
                String pubId = pulicId( pubIdDoc, sList.get(i).GetTextID()); // the according public id
                findHiddenNeighbor(pubId, sList.get(i).GetTextID(), i);
            }
            
        }
    }
    
    /**
     * This method is used to generate all the smali file paths.
     * 
     * @param smalifolder
     * @throws IOException 
     */
    public void smaliDocs( final File smalifolder ) throws IOException {
        for (final File fileEntry : smalifolder.listFiles()) {
            if (fileEntry.isDirectory()) {
                smaliDocs(fileEntry);
            } else {
                smalidocs.add(fileEntry.getAbsoluteFile().toString());
            }
        }    
    }
    
    /**
     * 
     * @param pubId
     * @param strId
     * @param n
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void findHiddenNeighbor(String pubId, String Id, int n) throws FileNotFoundException, IOException {
        
        String strId = "\""+Id+"\"";
        for(String dirname : smalidocs) {
            File file = new File(dirname);
            BufferedReader br = new BufferedReader(new FileReader(file));
            CentralText ct = sList.get(n);
            
            String line = null;
            int linenum = 1;
            while ((line = br.readLine()) != null) {
		if( line.contains(pubId) || line.contains(strId) ) {
                    if (ct.GetContext().contains("Not Found")) {
                        ct.EmptyContext();
                        //System.out.println(strId + dirname + "," + linenum);
                        ct.AddContext(file.getName() + "," + linenum);
                        sList.set(n, ct);
                    }
                    else {
                        //System.out.println(strId + dirname + "," + linenum);
                        ct.AddContext(file.getName() + "," + linenum);
                        sList.set(n, ct);
                    }   
                }
                linenum++;
            }
            br.close();
        }
    }
    
    
    
    
    /**
     * This method is used to find public id number for specific string id
     * 
     * @param apkPath
     * @param strId
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException 
     */
    public static String pulicId ( Document doc, String strId ) throws IOException, SAXException, ParserConfigurationException{
        int i ;
        String id;
        NodeList cl = doc.getElementsByTagName("*");
        for (i = 1; i < cl.getLength(); i++) { //All child element nodes loop
            if ( cl.item(i).getAttributes().item(1).getTextContent().contains(strId) ){
                break;
            }
        }
        return cl.item(i).getAttributes().item(0).getTextContent();    
    }
            
    
    /**
     * This method is used to return strings pack for every xml file.
     * 
     * @param doc
     * @param idtext_table
     * @return 
     */
    public static List<String> findingStrings (Document doc, Hashtable idtext_table) { 
        
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
    
    /**
     * This method is used to find neighbors for one string.
     * 
     * @param str
     * @param resdocs
     * @param TextID_table
     * @return
     * @throws Exception 
     */
    public static CentralText findNeighbors (CentralText str, List<Document> resdocs , Hashtable TextID_table ) throws Exception {
        
        String tstr = str.GetTextString(); //target string
        boolean Neighbor = false;  // false: no neighbor, true: have neighbor
        boolean Found = false;  // false: not found, true: found
        
        for(Document doc : resdocs){
            // Collect string packs from every file
            List<String> fileStringList = findingStrings(doc, TextID_table);
            if (fileStringList.contains(tstr)) {  // meet target string
                Found = true;
                if (fileStringList.size() != 1) {   // have neighbors
                    Neighbor = true;
                    int index = fileStringList.indexOf(tstr);               
                    str.AddContext(fileStringList, index);
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
            str.AddContextNotFound();
        }
        else if ( Neighbor == false ) {
            str.AddContextNoNeighbor();
        }
        return str;
    }
    
    
    /**
     * This method is used to generate document of public id file.
     * 
     * @param apkPath
     * @return
     * @throws Exception 
     */
    public static Document pubIdDocument ( String apkPath ) throws Exception {
        String idpath = apkPath + "/res/values/public.xml"; 
        File file = new File(idpath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        return doc;
    }
    
    /**
     * This method is used to get smali file path.
     * 
     * @param apkPath
     * @return 
     */
    public static File smaliDir ( String apkPath ) { 
        String idpath = apkPath + "/smali"; 
        File file = new File(idpath);
        return file;
    }
    
    /**
     * This method is to create object CentralText for every strings in strings.xml
     * 
     * @param doc
     * @return
     * @throws Exception 
     */
    public static List<CentralText> CentralTextGen ( Document doc ) throws Exception {
        Element elem = doc.getDocumentElement();
        NodeList cl = elem.getChildNodes();
        List<CentralText> sList = new ArrayList<CentralText>();
        
        int i;
        for (i = 0; i < cl.getLength(); i++) {
            Node node = cl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {            
                Element elment = (Element) node;
                CentralText tmp = new CentralText();
                tmp.SetTextID(elment.getAttribute("name"));
                tmp.SetTextString(node.getTextContent());

                sList.add(tmp);
            }
        }
        return sList;
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
     * This Method is used to generate a list of documents for all the files in one Application.
     * Given the application path, it generate a list of documents for all the file in this path.
     * The files exclude value folder and all the not XML files.
     * 
     * @param apkPath       Application path.
     * @return              A list of documents for all the files in application path.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public static List<Document> resDocList ( String apkPath ) throws ParserConfigurationException, SAXException, IOException  { 
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
     * 
     * @param st
     * @param fileWriter
     * @throws Exception 
     */
    public static void WriteText (CentralText st, FileWriter fileWriter) throws Exception {
        fileWriter.write(st.GetTextID() + "~ " + st.GetTextString().replaceAll("[\n\r]", ""));
        
        for (String s : st.GetContext()) {
            fileWriter.write("~ " + s.replaceAll("[\n\r]", ""));
        }
        fileWriter.write("\n");
    }
    
    public static FindHiddenContext handleContext (String apkPath) throws Exception {
        
        FindHiddenContext fhc = new FindHiddenContext(pubIdDocument(apkPath), smaliDir(apkPath), CentralTextGen(idTextDocument(apkPath)), HashTextID(idTextDocument(apkPath)), resDocList(apkPath));
        return fhc;
    }


    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, Exception {
        // TODO code application logic here
        
        String apkPath = "C:/Xue/ml/SingleApks/1000_com.slacker.radio";
        String outName = "C:/Xue/ml/output2.txt";
        File filew = new File(outName);
        FileWriter fileWriter = new FileWriter(filew,true);
        
        FindHiddenContext fhc = handleContext(apkPath);
        
        for ( int i = 0; i < fhc.sList.size(); i++) {
            WriteText(fhc.sList.get(i), fileWriter);
        }
        
        fileWriter.flush();
	fileWriter.close();
         
                
        
    }
    
}
