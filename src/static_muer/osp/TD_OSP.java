/*
 * No license for this file so far
 */
package static_muer.osp;

import static_muer.network.Link;
import static_muer.network.Network;
import static_muer.network.Node;

/**
 * Implements online shortest path (later, we seek to make it time dependent)
 * Additionally see if you can extend this class and function to incorporate the 
 * method proposed in Waller and Ziliaskopoulos (2002)
 * @author Venktesh
 */
public class TD_OSP {
    public void OSP(Network net, Node dest, Double vot, boolean solveMSOR){
        
        //Initialize
        for(Node n: net.getGraph().getNodes()){
            double cost= Double.MAX_VALUE;
            if(n.equals(dest))
                cost=0.0;
            for(NodeState ns: n.getNodeStates()){
                ns.setMinCostToDestination(cost);
                ns.setNextLink(null);
            }
        }
        
        //Go in reverse topological order of nodes
        //(currently assuming that the nodes are automatically topologically ordered
        //in increasing value by +1)
        for(int nodeID= dest.getTopologicalID()-1; nodeID>0; nodeID--){
            Node currNode = net.getGraph().getNodesByTopologicalID().get(nodeID);
            for(NodeState nS: currNode.getNodeStates()){
                double tempJ = Double.MAX_VALUE;
                Link bestLink = null;
                
                for(Link l: nS.getInfo().getOutgoingLinkInfo().keySet()){
                    double linkTT = 0.0;
                    if(solveMSOR)
                        linkTT = nS.getInfo().getOutgoingLinkInfo().get(l).getSOCost(vot);
                    else
                        linkTT = nS.getInfo().getOutgoingLinkInfo().get(l).getCost(vot);
                    Node nextNode = l.getToNode();
                    double nodeCost= 0.0;
                    for(NodeState ns2: nextNode.getNodeStates()){
                        nodeCost+= ns2.getMinCostToDestination()*ns2.getInfo().getProbability();
                    }
                    
                    if(linkTT+nodeCost < tempJ){
                        tempJ = linkTT+nodeCost;
                        bestLink = l;
                    }
                }
                nS.setMinCostToDestination(tempJ);
                nS.setNextLink(bestLink);
            }
        }
        
        //printing costs (for debugging)
//        System.out.println("Shortest hyperpath for vot="+vot+" for destination="+dest+
//                "is:\nNodestate\tMinCostToDest\tNextLink");
//        for(Node n: net.getGraph().getNodes()){
//            for(NodeState ns: n.getNodeStates()){
//                System.out.println(ns+"\t"+ns.getMinCostToDestination()+"\t"+ns.getNextLink());
//            }
//        }
    }
}
