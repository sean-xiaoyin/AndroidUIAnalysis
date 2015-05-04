/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlnodetree;



import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
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
public class XMLNodeTree extends JFrame {

    /**
     * @param args the command line arguments
     */
    
    public static Set fetchAllNodes ( Node node, ArrayList<String> t, Hashtable idtext_table) {
            
        Set<String> s = new LinkedHashSet<>();  // attributes for one node
        
        if ( node.hasChildNodes() == false )  { // has not childnode = leaf
            if (node.getNodeType() == Node.ELEMENT_NODE) {   
                NamedNodeMap al = node.getAttributes();
                for (int j = 0; j < al.getLength(); j++) {  // attribute loop
                    Node attr = al.item(j);
                    if (attr.getNodeType() == Node.ATTRIBUTE_NODE && attr.getTextContent().contains("@string") ) {
                        String str = idtext_table.get(attr.getTextContent().substring(8)).toString();
                        s.add(str);
                    }
                }              
            }    
        }
        else { // has childnode
            NodeList childlist = node.getChildNodes();
            for ( int i = 0; i < childlist.getLength(); i++) {
                Node c = childlist.item(i);
                if (c.getNodeType() == Node.ELEMENT_NODE) {
                    Set<String> stmp = fetchAllNodes(c, t, idtext_table);
                    s.addAll(stmp);
                    System.out.println(c.getNodeName()+ "~"+ stmp.toString());
                    t.add(c.getNodeName()+ "~"+ stmp.toString());
    
                }
                
            }
            
        }
        
    return s;    
    
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
    
    

    public static void main(String[] args) throws Exception {
        
        File filet = new File("C:\\Xue\\ml\\strings.xml");
        DocumentBuilderFactory dbft = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbt = dbft.newDocumentBuilder();
        Document doct = dbt.parse(filet);
        Hashtable TextID_table = HashTextID(doct);
        
        
        File file = new File("C:\\Xue\\ml\\layouttest.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        Node elem = (Node)doc.getDocumentElement();
        ArrayList<String> t = new ArrayList<String>();
        fetchAllNodes(elem, t, TextID_table);
        System.out.println(t);

    }
    
}
