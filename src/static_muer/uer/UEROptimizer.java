/*
 * No license for this file so far
 */
package static_muer.uer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import static_muer.network.Link;
import static_muer.network.Network;
import static_muer.osp.LinkState;

/**
 * Abstract class having all methods for an optimizer
 * Borrowing the idea from William's Algo B code
 * @author Venktesh
 */
public abstract class UEROptimizer {
    private Integer iteration = 0;
    protected final Integer maxIterations;
    protected final Integer relativeGapExp; //exponent of relative gap -4 for 10^(-4)
    protected final Network network;
	
    public UEROptimizer(Network network) {
            this(network, 1000);
    }
	
    public UEROptimizer(Network network, Integer maxIters) {
            this(network, maxIters, -5);
    }

    public UEROptimizer(Network network, Integer maxIters, Integer exp) {
            this.network = network;
            this.maxIterations = maxIters;
            this.relativeGapExp = exp;
    }
	
    public Network getNetwork() {
            return network;
    }

    public abstract void iterate(int i);

    @Override
    public abstract String toString();

    /**
     * Optimizes the network by solving an appropriate MUER algorithm
     */
    public void optimize() throws FileNotFoundException{
        System.out.println();
        System.out.println("Iter. #\tRelGap\t\t\tSPTT\t\t\tTSTT\tSystem optimal objective \tRuntime");
        System.out.println("-----------------------------------------------------");

        Long start = System.currentTimeMillis();
        Long end; Double runtime;
        
        String epochTime = Integer.toString((int)(System.currentTimeMillis()/1000)); 
        PrintWriter fileIn= null;
        
        if(static_muer.Static_MUER.printFlows){
            fileIn=   new PrintWriter("Networks/"+network.getNetName() +"/Outputs/"+"GapRate_"+epochTime+".txt");
            fileIn.println("Iter. #\tRelGap\tSPTT\tTSTT\tSORobjective\tRuntime");
        }
        
        
        do {
//            network.clearCache();
            System.out.print(iteration);
            if(static_muer.Static_MUER.printFlows)
                fileIn.print(iteration);
            
            iterate(iteration);
            
            System.out.print("\t"+network.toString());
            if(static_muer.Static_MUER.printFlows)
                fileIn.print("\t"+network.toString());

            end = System.currentTimeMillis();
            runtime = (end - start)/1000.0;
            System.out.println("\t"+String.format("%4.3f", runtime)+" s");
            if(static_muer.Static_MUER.printFlows)
                fileIn.println("\t"+String.format("%4.3f", runtime)+" s");

//            if (wrap.printFlows) try {
//                    network.printFlows(new PrintStream("flows.txt"));
//            } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//            }
            iteration++;
            start = System.currentTimeMillis();
        } while (!converged());
        
        if(static_muer.Static_MUER.printFlows){
            fileIn.close();
            fileIn.flush();
        }
        
        if(static_muer.Static_MUER.printFlows){
            network.printLinkStateFlows();
        }
        
        //uncomment the following if want to print the tolls for SO
//        for(Link l: network.getGraph().getLinks()){
//            for(LinkState ls: l.getStates()){
//                System.out.println("Toll for "+ls+" is "+ls.getSONewToll());
//            }
//        }

    }

    private Boolean converged() {
        try {
            return iteration > maxIterations || network.getRelativeGap() < Math.pow(10, relativeGapExp);
        } catch (Exception e) {
            e.printStackTrace();
            return iteration > maxIterations;
        }
    }
}
