/*
 * No license for this file so far
 */
package static_muer.osp;

import java.util.HashMap;
import java.util.Map;
import static_muer.network.Link;

/**
 * Information received at a node
 * @author Venktesh
 */
public class Information {
    Map<Link, LinkState> outgoingLinkInfo;
    double probability; //prob that this information is received

    public Information() {
        this.outgoingLinkInfo = new HashMap<>();
    }

    public Map<Link, LinkState> getOutgoingLinkInfo() {
        return outgoingLinkInfo;
    }

    public double getProbability() {
        return probability;
    }
    
    public void addLinkInfo(Link l, LinkState ls){
        if(!ls.getLink().equals(l)){
            System.out.println("Mismatch of linkState and link. ls="+ls+" and l="+l);
            System.exit(1);
        }
        outgoingLinkInfo.put(l, ls);
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "Information{" + "outgoingLinkInfo=" + outgoingLinkInfo + ", probability=" + probability + '}';
    }
    
    
    
}
