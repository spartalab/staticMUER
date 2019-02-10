/*
 * No license for this file so far
 */
package static_muer;

import java.io.FileNotFoundException;
import static_muer.network.Network;
import static_muer.uer.MSAOptimizer;

/**
 * Code that implements the multiclass (static) user-equilibrium with recourse
 * The code is implemented for acyclic managed lane networks, but will be extended
 * later to include cyclic networks
 * Current version is based on a discrete VOT distribution
 * @author venktesh
 */
public class Static_MUER{

    //if true, print the flow in each linkstate and the gap rate in separate files
    public static boolean printFlows = true;
    
    /**
     * @param args[0] is the name of the network with files under the folder
     * "/Networks/____/"
     * args[1] is the demand factor that gets multiplied to the demand 
     * between each OD pair for modeling varying demand levels
     */
    public static void main(String[] args){

        try{
            String netName = args[0];
            double demandFactor=Double.parseDouble(args[1]);
            Network network = new Network(netName, demandFactor);
            network.readAllInputs();

            network.initializeUERvariables();

            //run max 1000 iterations or solve to a gap of 10^-4
            //if solving system optimal with recourse, toggle the variable below to true
            boolean solveMSOR = false; 
            MSAOptimizer optimizer = new MSAOptimizer(network, 5000, -4, solveMSOR);
            optimizer.optimize();
            
        } catch(FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        
    }
    
}
