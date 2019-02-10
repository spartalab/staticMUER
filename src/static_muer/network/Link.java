/*
 * No license for this file so far
 */
package static_muer.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static_muer.osp.LinkState;

/**
 * All link related variables. The LinkState methods are located in LinkState class under OSP package
 * @author Samar
 */
public class Link {
    private final Node fromNode, toNode;
    private final int ID;
    
    private List<LinkState> states; //states link can be in
    
//    public Map<LinkState, Map<Double, Double>> flow; //actual flow
//    public Map<LinkState, Map<Double, Double>> spFlow; //flow when all travelrs on shortest path
    
    //other variables commented for static implementation
    private double length; //km
    //double capacity;//veh/hr
    //double jamDensity; //veh/km
    private double FFS; //km/hr //stores the freeflowspeed
    //double backWaveSpeed; //km/hr
    String linkClass; //GP or ML
    private boolean isTolled; //true means the link is tolled
//    int noOfCells;
//    double initialVehicles; //stores no. of initial vehicles
    
    private double fftt; //freeFlow travel time in seconds
    
    public Link(int id, Node fromNode, Node toNode){
        this.ID = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
        fromNode.addLink(this);
        toNode.addLink(this);
        states = new ArrayList<>();
        
    }

    public Link(Node fromNode, Node toNode, int ID, double length, double FFS, boolean isTolled, String linkClass) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.ID = ID;
        this.length = length;
        this.FFS = FFS;
        this.isTolled = isTolled;
        this.linkClass = linkClass;
        
        this.fftt = this.length*3600/this.FFS; //units in seconds
        fromNode.addLink(this);
        toNode.addLink(this);
    }

    public int getID(){
        return ID;
    }
    
    public Node getFromNode(){
        return fromNode;
    }

    public Node getToNode(){
        return toNode;
    }
    
    
    public double getFFS(){
        return FFS;
    }
    
    public int hashCode(){
        return ID;
    }
    
    public boolean equals(Object o){
        Link rhs = (Link)o;
        return rhs.ID == ID;
    }
    
    public String toString(){
        return ""+ID;
    }

    public double getLength(){
        return length;
    }

    public boolean isIsTolled(){
        return isTolled;
    }

    public double getFftt(){
        return fftt;
    }

    public String getLinkClass() {
        return linkClass;
    }
    
    public void addLinkState(LinkState ls){
        if(!states.contains(ls))
            states.add(ls);
    }

    public List<LinkState> getStates() {
        return states;
    }
    
}
