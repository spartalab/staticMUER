/*
 * No license for this file so far
 */
package static_muer.osp;

import java.util.HashMap;
import java.util.Map;
import static_muer.network.Link;
import static_muer.network.Node;

/**
 * The state at a node (consists of currentNode and information received)
 * Probability of occurrence of this NodeState is same as probability of receiving
 * information "info"
 * @author Venktesh <venktesh at utexas dot edu>
 */
public class NodeState {
    private final Node node;

    private final Information info;
    private double minCostToDestination; //min cost to destination used for solving OSP
    private Link nextLink; //next link to take in this nodeState
    
    private Map<Double, Double> parkedFlow; //flowparked in this node state
    
    public NodeState(Node node, Information info) {
        this.node = node;
        this.info = info;
        
        minCostToDestination = Double.MAX_VALUE;
        nextLink = null;
        parkedFlow = new HashMap<>();
    }

    public Node getNode() {
        return node;
    }

    public Information getInfo() {
        return info;
    }

    public double getMinCostToDestination() {
        return minCostToDestination;
    }

    public Link getNextLink() {
        return nextLink;
    }

    public Map<Double, Double> getParkedFlow() {
        return parkedFlow;
    }
    
    public void addToParkedFlow(double vot, double flow){
        if(!parkedFlow.containsKey(vot))
            System.out.println("No key found in parked flow for nodestate "+this);
        parkedFlow.put(vot, parkedFlow.get(vot)+flow);
    }
    
    public void initializeParkedFlow(double vot, double flow){
        parkedFlow.put(vot, flow);
    }

    public void setMinCostToDestination(double minCostToDestination) {
        this.minCostToDestination = minCostToDestination;
    }

    public void setNextLink(Link nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public String toString() {
        return "NodeState{" + "node=" + node + ", info=" + info + '}';
    }
    
    
}
