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
 * Implements the link-based optimizer that uses Frank-Wolfe algorithm
 * @author Venktesh
 */
public class FWOptimizer extends LinkBasedOptimizer{

    boolean solveMSOR;
    
    public FWOptimizer(Network network) {
        super(network);
        solveMSOR = false;
    }

    public FWOptimizer(Network network, Integer maxIters) {
        super(network, maxIters);
        solveMSOR = false;
    }

    public FWOptimizer(Network network, Integer maxIters, Integer exp) {
        super(network, maxIters, exp);
        solveMSOR = false;
    }
    
    public FWOptimizer(Network network, Integer maxIters, Integer exp, boolean temp) {
        super(network, maxIters, exp);
        solveMSOR = temp;
    }

    @Override
    public String toString() {
        return "FWoptimizer";
    }

    @Override
    public double getStepSizeLambda(int itrNo) {
        if(itrNo>0){
            double lambda=0.5;
            boolean converged = false;
            while(!converged){
                double VIFunc = variationInequalityFunction(lambda);
                double VIFuncDerivative = variationInequalityFunctionDerivative(lambda);
                double l = lambda - VIFunc/VIFuncDerivative;
                if(Math.abs(VIFunc)<1E-4){
                    converged = true;
                }
//                System.out.println("VIFunc="+VIFunc+", VIFunc derivative="+VIFuncDerivative+", old lambda="+lambda); //+" and new lambda="+lambda);
                lambda = l;
                lambda = (lambda>1.0)?1.0:((lambda<0.0)?0.0:lambda);
//                System.out.println("--and new lambda="+lambda);
//                System.out.println("VIFunc="+VIFunc+" and lambda="+lambda);
                
            }
            lambda = (lambda>1.0)?1.0:((lambda<0.0)?0.0:lambda);
//            System.out.println("===Lambda in itr "+itrNo+" = "+lambda);
            return lambda;
        }
        else
            return 1.0;
    }
    
    private double variationInequalityFunction(double lambda){
        double functionVal = 0.0;
        for(Link l: network.getGraph().getLinks()){
            for(LinkState ls: l.getStates()){
                Map<Double, Double> scaledFlow = new HashMap<>();
                double spFlow = 0.0;
                double flow = 0.0;
                for(Double vot: network.getVotProportion().keySet()){
                    scaledFlow.put(vot, lambda*ls.getSpFlow().get(vot) + (1-lambda)*ls.getFlow().get(vot));
                }
                for(Double vot: network.getVotProportion().keySet()){
                    spFlow = ls.getSpFlow().get(vot);
                    flow = ls.getFlow().get(vot);
//                    functionVal += (ls.getToll()/vot + ls.getTravelTime(scaledFlow))*(spFlow-flow);
                    functionVal += ls.getCost(vot, scaledFlow)*(spFlow-flow);
                }
            }
        }
        return functionVal;
    }
    
    //derivative wrt lambda
    //@todo: assuming toll to not be a function of flow so it is not included as part of the derivative
    private double variationInequalityFunctionDerivative(double lambda){
        double functionVal = 0.0;
        for(Link l: network.getGraph().getLinks()){
            for(LinkState ls: l.getStates()){
                Map<Double, Double> scaledFlow = new HashMap<>();
                double spFlow = 0.0;
                double flow = 0.0;
                double totalSPFlow = 0.0;
                double totalFlow= 0.0;
                for(Double vot: network.getVotProportion().keySet()){
                    scaledFlow.put(vot, lambda*ls.getSpFlow().get(vot) + (1-lambda)*ls.getFlow().get(vot));
                    totalSPFlow += ls.getSpFlow().get(vot);
                    totalFlow += ls.getFlow().get(vot);
                }
                for(Double vot: network.getVotProportion().keySet()){
                    spFlow = ls.getSpFlow().get(vot);
                    flow = ls.getFlow().get(vot);
                    functionVal += ls.getCostDerivative(vot,scaledFlow)*(spFlow-flow)*(totalSPFlow-totalFlow);
                    //functionVal += ls.getCostDerivative(vot,scaledFlow)*Math.pow(spFlow-flow,2);
                }
                
            }
        }
        return functionVal;
    }
    
}
