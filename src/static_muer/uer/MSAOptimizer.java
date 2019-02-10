/*
 * No license for this file so far
 */
package static_muer.uer;

import java.util.HashMap;
import java.util.Map;
import static_muer.network.Link;
import static_muer.network.Network;
import static_muer.network.Node;
import static_muer.osp.LinkState;
import static_muer.osp.NodeState;
import static_muer.osp.TD_OSP;

/**
 * Implements the link-based optimizer that uses methods of successive averages
 * @author Venktesh
 */
public class MSAOptimizer extends UEROptimizer{

    boolean solveMSOR;
    
    public MSAOptimizer(Network network) {
        super(network);
        solveMSOR = false;
    }

    public MSAOptimizer(Network network, Integer maxIters) {
        super(network, maxIters);
        solveMSOR = false;
    }

    public MSAOptimizer(Network network, Integer maxIters, Integer exp) {
        super(network, maxIters, exp);
        solveMSOR = false;
    }
    
    public MSAOptimizer(Network network, Integer maxIters, Integer exp, boolean temp) {
        super(network, maxIters, exp);
        solveMSOR = temp;
    }

    
    @Override
    public void iterate(int itrNo) {
        //reinitialize SP flow and parked flows on nodes
        for(Link l: network.getGraph().getLinks()){
            for(LinkState ls: l.getStates()){
                Map<Double, Double> temp= new HashMap<>();
                for(Double vot: network.getVotProportion().keySet()){
                    temp.put(vot, 0.0);
                }
                ls.setSpFlow(temp);
            }
        }
        network.setSPTT(0.0);
        for(Node n: network.getGraph().getNodes()){
            for(NodeState ns: n.getNodeStates()){
                for(Double vot: network.getVotProportion().keySet())
                    ns.initializeParkedFlow(vot, 0.0);
            }
        }
        
        //@todo: fix the code below to not solve TD-OSP for every OD pair
        for(Node dest: network.getTripTable().getDests()){
            for(Double vot: network.getVotProportion().keySet()){
                TD_OSP temp = new TD_OSP();
                temp.OSP(network, dest, vot, solveMSOR);
                updateSPTT(dest, vot);
                loadDemandToSP(dest,vot);
            }
        }
                
        //calculate relative gap
        double rG=100;
        if(itrNo==1)
            network.setTSTT(Double.MAX_VALUE);
        else{
            double TSTC=0.0; //total system travel cost
            double SORobjective =0.0; //only includes TT
            for(Link l: network.getGraph().getLinks()){
                for(LinkState ls: l.getStates()){
                    for(Double vot: network.getVotProportion().keySet()){
                        double oldFlow = ls.getFlow().get(vot);
                        if(solveMSOR)
                            TSTC+= oldFlow * ls.getSOCost(vot);
                        else
                            TSTC+= oldFlow * ls.getCost(vot);
                        SORobjective += oldFlow* vot* ls.getTravelTime();
                    }
                }
            }
            network.setTSTT(TSTC);
            network.setSORobjective(SORobjective);
        }
        
        //find lambda
        double lambda = 1/(double)itrNo;
        
        //Shift flows using a linear combination
        for(Link l: network.getGraph().getLinks()){
            for(LinkState ls: l.getStates()){
//                System.out.println(ls);
                for(Double vot: network.getVotProportion().keySet()){
                    double newFlow = ls.getSpFlow().get(vot);
                    double oldFlow = ls.getFlow().get(vot);
//                    System.out.println("++Vot "+vot+" has spFlow"+newFlow+" and old flow="+
//                            oldFlow+" and cost="+(ls.getFlow().get(vot)*ls.getCost(vot)));
                    ls.updateflow(vot, lambda*newFlow+(1-lambda)*oldFlow);
                }
            }
        }
    }
    
    private void updateSPTT(Node dest, Double vot){
        double sptt= network.getSPTT();
        
        for(Node orig: network.getTripTable().getTrips().get(dest).keySet()){
            double shortestCost=0.0;
            for(NodeState ns: orig.getNodeStates()){
                shortestCost+= ns.getMinCostToDestination()*ns.getInfo().getProbability();
//                System.out.println("cost from origin "+orig+" to dest="+
//                        ns.getMinCostToDestination()*ns.getInfo().getProbability());
            }
            sptt+= shortestCost* network.getTripTable().getTrips().get(dest).get(orig).get(vot);
        }
        
        network.setSPTT(sptt);
    }
    
    private void loadDemandToSP(Node dest, Double vot){
        //we need nodestate flow labels as well
        
        //go in topological order of nodes
        for(int i=1;i< dest.getTopologicalID();i++){
            Node currNode = network.getGraph().getNodesByTopologicalID().get(i);
            
            //First node flow
            double nodeFlow = 0.0;
            if(network.getTripTable().getTrips().get(dest).containsKey(currNode))
                nodeFlow= network.getTripTable().getTrips().get(dest).get(currNode).get(vot);
            for(Link l: currNode.getIncoming()){
                for(LinkState ls: l.getStates())
                    nodeFlow+= ls.getSpFlow().get(vot);
            }
            
            //Then node flow to node-state flow
            for(NodeState ns: currNode.getNodeStates()){
                ns.addToParkedFlow(vot, nodeFlow*ns.getInfo().getProbability());
            }
            
            //Then node-state flow to link-state flow
            for(NodeState ns: currNode.getNodeStates()){
                LinkState ls= ns.getInfo().getOutgoingLinkInfo().get(ns.getNextLink());
                ls.addToSPflow(vot, ns.getParkedFlow().get(vot));
            }
        }
    }

    @Override
    public String toString() {
        return "MSAoptimizer";
    }
    
}
