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
 * Implements the link-based optimizer that uses method of successive averages algorithm
 * @author Venktesh
 */
public class MSAOptimizer extends LinkBasedOptimizer{

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
    public String toString() {
        return "MSAoptimizer";
    }

    @Override
    public double getStepSizeLambda(int itrNo) {
        if(itrNo>0)
            return 1/(double)itrNo; 
        else
            return 1;
    }
    
}
