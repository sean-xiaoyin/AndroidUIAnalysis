/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlnodetree;

import java.util.LinkedHashSet;
import java.util.Set;
import org.w3c.dom.Node;

/**
 * @author Xue
 * @version 1.0
 * @since 2015-06-02
 */
public class Context implements java.io.Serializable {
    
    /**
     * This class is used to store one node and its corresponding node set.
     */
    private static final long serialVersionUID = 1L;
    Node node;
    Set<Node> nodeset = new LinkedHashSet<>();
    
    public Node Setnode ( Node n) {
        node = n;
        return node;
    }
    public Set<Node> Setnodeset ( Set<Node> s ){
        nodeset.addAll(s);
        return nodeset;
    }
    public Node Getnode () {
        return node;
    }
    public Set<Node> Getnodeset() {
        return nodeset;
    }
    
}

