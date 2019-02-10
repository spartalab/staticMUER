/*
 * No license for this file so far
 */
package static_muer.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static_muer.osp.LinkState;

/**
 * A class that stores the nodes and links and their connection
 * @author Venktesh
 */
public class Graph {
    private final Set<Link> links;
    private final List<Node> nodes; //we will access nodes in reverse topological order so order is imp for now
    private final Map<Integer, Node> nodesByID;
    private final Map<Integer, Node> nodesByTopologicalID;
    private final Map<Integer, Link> linksByID;

    public Graph() {
        links=new HashSet<>();
        nodes=new ArrayList<>();
        nodesByID= new HashMap<>();
        linksByID= new HashMap<>();
        nodesByTopologicalID = new HashMap<>();
    }
    
    public void addLink(Link l){
        links.add(l);
        if(linksByID.containsKey(l.getID())){
            linksByID.put(l.getID(), l);
        }
    }
    
    public void addNode(Node n){
        if(!nodes.contains(n)){
            nodes.add(n);
            nodesByID.put(n.getId(), n);
        }
    }

    public Set<Link> getLinks() {
        return links;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Map<Integer, Node> getNodesByID() {
        return nodesByID;
    }

    public Map<Integer, Link> getLinksByID() {
        return linksByID;
    }

    public Map<Integer, Node> getNodesByTopologicalID() {
        return nodesByTopologicalID;
    }
    
    
    
    public void printGraphInfo(){
        System.out.println("No. of nodes:"+nodes.size());
        System.out.println("\t Nodes are:"+nodes);
        System.out.println("No. of links:"+links.size());
        System.out.println("\tLinks are:"+links);
        
        System.out.println("All link states:");
        for(Link l: links){
            for(LinkState ls: l.getStates())
                System.out.print(ls+"\t");
            System.out.print("\n");
        }
    }
    
    /**
     * Arranges nodes in topological order
     */
    public void getTopologicalOrder(){
        int index=1;
        List<Link> remainingLinks= new ArrayList<>();
        for(Link l:links)
            remainingLinks.add(l);
        
        List<Node> nodesWithNoIncoming = new ArrayList<>();
        for(Node n: nodes){
            if(n.getIncoming().size()==0){
                nodesWithNoIncoming.add(n);
//                n.setTopologicalID(index);
//                System.out.println("Node "+n+" new ID is "+index);
//                nodesByTopologicalID.put(index, n);
//                index++;
            }
        }
        
        while(!nodesWithNoIncoming.isEmpty()){
            Node n = nodesWithNoIncoming.get(0);
            nodesWithNoIncoming.remove(0);
            
            n.setTopologicalID(index);
            System.out.println("Node "+n+" new ID is "+index);
            nodesByTopologicalID.put(index, n);
            index++;
            
            for(Link l: n.getOutgoing()){
                remainingLinks.remove(l);
                boolean n2HasNoMoreIncoming = true;
                for(Link l2: l.getToNode().getIncoming()){
                    if(remainingLinks.contains(l2)){
                        n2HasNoMoreIncoming = false;
                        break;
                    }
                }
                if(n2HasNoMoreIncoming)
                    nodesWithNoIncoming.add(l.getToNode());
            }
        }
        
    }
    
}
