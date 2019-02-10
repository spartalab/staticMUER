/*
 * No license for this file so far
 */
package static_muer.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static_muer.osp.Information;
import static_muer.osp.LinkState;
import static_muer.osp.NodeState;

/**
 * All node related variables
 * @author Venktesh
 */
public class Node {
    private final List<Link> incoming;
    private final List<Link> outgoing;

    private final int id; //Node ID
    private int topologicalID; //topological order ID
    
    private Set<NodeState> nodeStates;
    private boolean isOrigin;
    
//    private Set<Information> informationSet; //ideally a function of time; but assumed time-independent for now
    
//    public double label; // used for shortest path
//    public Link prev; //used for shortest path
    
    //Backward recursion variables
    public Map<Integer, Double> valueAtTimeStep;
    public Map<Integer, Map<Information, Double>> valueOfInfoAtTimeStep;
    public Map<Integer, Map<Information, Link>> nextLinkGivenInfoAtTimeStep;
    
    public Node(int id){
        incoming = new ArrayList<>();
        outgoing = new ArrayList<>();
        //transitionFlows = new HashMap<>();
        //originFlow=0;
        this.id = id;
        this.topologicalID = -1;
        valueAtTimeStep= new HashMap<>();
        valueOfInfoAtTimeStep = new HashMap<>();
        nextLinkGivenInfoAtTimeStep = new HashMap<>();
        
        isOrigin=false;
    }


    @Override
    public boolean equals(Object o){
        Node rhs = (Node)o;
        return rhs.id == id;
    }

    public void addLink(Link l){
        if(l.getFromNode() == this){
            outgoing.add(l);
        }
        else if(l.getToNode() == this){
            incoming.add(l);
        }
    }

    public List<Link> getIncoming(){
        return incoming;
    }

    public List<Link> getOutgoing(){
        return outgoing;
    }

    // return true if the source node is upstream of the node object
    public Link getIncomingLink(Node source){
        for(Link l : incoming){
            if(l.getFromNode() == source){
                return l;
            }
        }
        return null;
    }

    public Link getOutgoingLink(Node dest){
        for(Link l : outgoing){
            if(l.getToNode() == dest){
                return l;
            }
        }
        return null;
    }
    
    public int getId(){
        return id;
    }

    public int getTopologicalID() {
        return topologicalID;
    }

    public void setTopologicalID(int topologicalID) {
        this.topologicalID = topologicalID;
    }
    
    

    public Set<NodeState> getNodeStates() {
        return nodeStates;
    }

    public boolean isIsOrigin() {
        return isOrigin;
    }

    public void setIsOrigin(boolean isOrigin) {
        this.isOrigin = isOrigin;
    }
    
    

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public String toString()
    {
        return ""+id;
    }
    
    
    public void initializeInformationVector(List<Double> tollSet, List<Double> ttMultiplier){
        
        nodeStates = new HashSet<>();
        
        List<List<LinkState>> arraysToEnumerateFrom = new ArrayList<>();
        for(Link l: getOutgoing()){
            List<LinkState> temp = new ArrayList<>();
            for(LinkState ls: l.getStates())
                temp.add(ls);
            arraysToEnumerateFrom.add(temp);
        }
        /**
         * product holds the numbers which will be used to divide or take a modulus by
         * Say we have 4*3*2*2 elements to be generated (4 elements in list 1, ...)
         * Then, product will hold 12, 4, 2, 1 (the reason for which is in the concept of generating the enumerations)
         */
        List<Integer> product = new ArrayList<>();
        for(int i=0;i<arraysToEnumerateFrom.size();i++)
            product.add(0);
        int numberOfCombinations = 1;
            
        for(int i=arraysToEnumerateFrom.size()-1;i>=0;i--){
            if(i==arraysToEnumerateFrom.size()-1)
                product.set(i, 1);
            else{
                product.set(i, (int)arraysToEnumerateFrom.get(i+1).size()*product.get(i+1));
            }
            numberOfCombinations*= arraysToEnumerateFrom.get(i).size();
        }
        /**
         * If index=43 for 48 combinations of 4*3*2*2 elements, then proceed as following:
         * ID to pick from array 1= (43/12)%4 = 3%4 = 3 (all integer based calculations)
         * ID to pick from array 2= (43/4)%3 = 10%3= 1
         * ID to pick from array 3= (43/2)%2 = 21%2 = 1
         * ID to pick from array 4= (43/1)%2 = 43%2 = 1
         * So if index=0, pick first element of each array for the combination
         * if index=1, pick first element from all except the last array from which pick the second element...
         */
        for(int index=0; index< numberOfCombinations; index++){
            List<Integer> idsToPick = new ArrayList<>(); //ids in each array list that will be picked for this index of combination
            for(int i=0;i< product.size();i++){
                idsToPick.add(i,(index/product.get(i))%arraysToEnumerateFrom.get(i).size());
            }
            Information theta= new Information();
            double totalProb = 1.0;
            for(int i=0;i< arraysToEnumerateFrom.size(); i++){
                LinkState ls= arraysToEnumerateFrom.get(i).get(idsToPick.get(i));
                theta.addLinkInfo(ls.getLink(), ls);
                totalProb *= ls.getProbOfOccurence();
            }
            theta.setProbability(totalProb);
            NodeState nS = new NodeState(this, theta);
            nodeStates.add(nS);
        }
        printInformationSet();
    }
    
    public void printInformationSet(){
        System.out.println("Node "+this+" has total "+ nodeStates.size()+" node states, which are:");
        for(NodeState ns: nodeStates){
            System.out.println("+++"+ns);
        }
    }
    
}
