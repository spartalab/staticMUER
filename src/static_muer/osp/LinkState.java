/*
 * No license for this file so far
 */
package static_muer.osp;
import java.util.HashMap;
import java.util.Map;
import static_muer.network.Link;

/**
 * This class stores information for a link in a certain state
 * @author Venktesh
 */
public class LinkState{
    private int ID; //ID of a link state is linkID*100+x (x=1,2,3,...)
    private final Link link;
    
    //assuming BPR function
    private double fftt;
    private double capacity;
    private double alpha; //coefficient
    private double power;
    
    private double toll;
    private double probOfOccurence;
    
    private Map<Double, Double> flow;
    private Map<Double, Double> spFlow;

    public LinkState(Link link) {
        this.link = link;
    }

    public LinkState(int ID, Link link, double fftt, double capacity, double alpha, double power, double toll, double probOfOccurence) {
        this.ID = ID;
        this.link = link;
        this.fftt = fftt;
        this.capacity = capacity;
        this.alpha = alpha;
        this.power = power;
        this.toll = toll;
        this.probOfOccurence = probOfOccurence;
        
        flow = new HashMap<>();
        spFlow = new HashMap<>();
    }

    public Link getLink() {
        return link;
    }

    public double getFftt() {
        return fftt;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getPower() {
        return power;
    }

    public int getID() {
        return ID;
    }

    public double getToll() {
        return toll;
    }

    public double getProbOfOccurence() {
        return probOfOccurence;
    }
    
    
    
    public double getCost(double vot){
        return toll + (vot)*(getTravelTime());
    }
    
    public double getTravelTime(){
        double totFlow =0.0;
        for(Double vot: flow.keySet())
            totFlow+= flow.get(vot);
        return fftt*(1+ alpha*Math.pow(totFlow/capacity, power));
    }
    
    public double getTTDerivate(){
        double totFlow =0.0;
        for(Double vot: flow.keySet())
            totFlow+= flow.get(vot);
       return fftt*alpha*power*(1/capacity)*Math.pow(totFlow/capacity, power);
    }
    
    public double getSOCost(double VOT){
        double totFlow =0.0;
        for(Double vot: flow.keySet())
            totFlow+= flow.get(vot);
        double newToll = getAvgVOT()*totFlow*getTTDerivate();
        return newToll + VOT*getTravelTime();
    }
    
    public double getSONewToll(){
        double totFlow =0.0;
        for(Double vot: flow.keySet())
            totFlow+= flow.get(vot);
        return getAvgVOT()*totFlow*getTTDerivate();
    }
    
    private double getAvgVOT(){
        double numerator=0.0, den = 0.0;
        for(Double vot: flow.keySet()){
            numerator+= vot*flow.get(vot);
            den+= flow.get(vot);
        }
        if(den>0)
            return numerator/den;
        else
            return 0;
    }

    public void setSpFlow(Map<Double, Double> spFlow) {
        this.spFlow = spFlow;
    }

    public void setFlow(Map<Double, Double> flow) {
        this.flow = flow;
    }

    public Map<Double, Double> getFlow() {
        return flow;
    }

    public Map<Double, Double> getSpFlow() {
        return spFlow;
    }
    
    public void addToSPflow(double vot, double f){
        if(!spFlow.containsKey(vot)){
            System.out.println("VOT "+vot+"not found in spFlow map");
            System.exit(1);
        }
        spFlow.put(vot, spFlow.get(vot)+f);
    }
    
    public void updateSPflow(double vot, double f){
        if(!spFlow.containsKey(vot))
            System.out.println("VOT "+vot+"not found in spFlow map");
        spFlow.put(vot, f);
    }
    
    public void updateflow(double vot, double flowNew){
        if(!flow.containsKey(vot))
            System.out.println("VOT "+vot+"not found in spFlow map");
        flow.put(vot, flowNew);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.ID;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkState other = (LinkState) obj;
        if (this.ID != other.ID) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LinkState{" + "link=" + link + ", prob=" + probOfOccurence + '}';
    }

        
    
}
