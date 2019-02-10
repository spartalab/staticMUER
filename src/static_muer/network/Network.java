/*
 * No license for this file so far
 */
package static_muer.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import static_muer.osp.LinkState;

/**
 * Implements all network related methods
 * @author Venktesh
 */
public class Network {
    private final boolean networkOrderedAlready = false; //true if inputs are already in topological order
    protected String netName;
    protected Graph graph;
    protected double demandFactor;
    
    protected Map<Double, Double> votProportion; //allVOT class and its proportion
    protected final TripTable tripTable;
    
    protected double relativeGap;
    protected double SPTT; //shortest expected travel cost (includes toll and TT)
    protected double TSTT; //total expected system travel cost (includes toll and TT)
    protected double SORobjective; //total expected system TT (converted to cost units)
    
    
    //since it is Static UER we do not need deltaT and noOfTimeSteps for now
    private int deltaT;  
    private int noOfTimeSteps;
    
    public Network(String name, double dFactor){
        netName=name;
        graph = new Graph();
        votProportion = new HashMap<>();
        tripTable = new TripTable();
        demandFactor = dFactor;
    }
    
    public void readAllInputs(){
        String folderName = "Networks/"+netName+"/";
        readNetwork(folderName+"Links.txt", folderName+"Trips.txt", 
                folderName+"VOT.txt");
    }
    
    public void readNetwork(String linkFile, String tripFile, String VOTfile){
        //Reading link file
        try(Scanner inputFile = new Scanner(new File(linkFile))){
            inputFile.nextLine(); //ignore the first line
            while (inputFile.hasNext()){
                int ID = inputFile.nextInt();
                int origin_id = inputFile.nextInt();
                int dest_id = inputFile.nextInt();

                Node source, dest;

                if (!graph.getNodesByID().containsKey(origin_id))
                    graph.addNode(new Node(origin_id));
                source = graph.getNodesByID().get(origin_id);

                if (!graph.getNodesByID().containsKey(dest_id))
                    graph.addNode(new Node(dest_id));
                dest = graph.getNodesByID().get(dest_id);

                Link l= new Link(ID, source, dest);
                
                int numStates = inputFile.nextInt();
                String allStateInfo = inputFile.next();
                
                //remove trailing {'s
                allStateInfo = allStateInfo.substring(1, allStateInfo.length()-1);
                String eachStateString[] = allStateInfo.split(";");
                int count=0;
                for(String eachState: eachStateString){
                    count++;
                    String info[] = eachState.substring(1,eachState.length()-1).split(",");
                    if(info.length<6){
                        System.out.println("Input file error. Link state for link "
                                +l+" not properly defined");
                        System.exit(1);
                    }
                    double ffTT = Double.parseDouble(info[0]);
                    double cap = Double.parseDouble(info[1]);
                    double alpha = Double.parseDouble(info[2]);
                    double pow = Double.parseDouble(info[3]);
                    double toll = Double.parseDouble(info[4]);
                    double prob = Double.parseDouble(info[5]);
                    
                    int lStateID = l.getID()*100+ count;
                    LinkState lState = new LinkState(lStateID, l, ffTT, cap, alpha, pow, toll, prob);
                    l.addLinkState(lState);
                }
                graph.addLink(l);
            }
            inputFile.close();
            graph.printGraphInfo();
            if(networkOrderedAlready){
                for(Node n: graph.getNodes()){
                    n.setTopologicalID(n.getId());
                }
            }
            else
                graph.getTopologicalOrder();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        //Reading VOT distribution
        try(Scanner inputFile = new Scanner(new File(VOTfile))){
            inputFile.nextLine(); //ignore the first line
            while (inputFile.hasNext()){
                double vot = inputFile.nextDouble();
                double prop = inputFile.nextDouble();
                votProportion.put(vot, prop);
            }
            inputFile.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        //Reading Trips file
        try(Scanner inputFile = new Scanner(new File(tripFile))){
            inputFile.nextLine(); //ignore the first line
            while (inputFile.hasNext()){
                int origID = inputFile.nextInt();
                int destID = inputFile.nextInt();
                double demand = inputFile.nextDouble();
                demand*= demandFactor;
                Node orig = graph.getNodesByID().get(origID);
                Node dest = graph.getNodesByID().get(destID);
                orig.setIsOrigin(true);
                tripTable.addODpair(orig, dest, demand, votProportion);
            }
            inputFile.close();
            tripTable.printThis();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public String getNetName() {
        return netName;
    }

    public Graph getGraph() {
        return graph;
    }

    public Map<Double, Double> getVotProportion() {
        return votProportion;
    }

    public TripTable getTripTable() {
        return tripTable;
    }

    public double getRelativeGap() {
        relativeGap = TSTT/SPTT - 1;
        if(relativeGap<0){
            System.out.println("Error in relative gap");
            System.exit(1);
        }
        return relativeGap;
    }

    public double getSPTT() {
        return SPTT;
    }

    public void setSPTT(double SPTT) {
        this.SPTT = SPTT;
    }

    public double getTSTT() {
        return TSTT;
    }

    public void setTSTT(double TSTT) {
        this.TSTT = TSTT;
    }

    /**
     * Evaluates objective for system optimal with recourse
     * @return the objective
     */
    public double getSORobjective() {
        return SORobjective;
    }

    public void setSORobjective(double SORobjective) {
        this.SORobjective = SORobjective;
    }
    
    

//    public void setRelativeGap(double relativeGap) {
//        //the reason we set it is because we calculate it using a differnt procedure
//        //see MSAOptimizer.java iterate() function for details
//        this.relativeGap = relativeGap;
//    }
    
    public void initializeUERvariables(){
        //set all linkstate flows to zero
        for(Link l: graph.getLinks()){
            for(LinkState ls: l.getStates()){
                Map<Double, Double> temp = new HashMap<>();
                Map<Double, Double> temp2 = new HashMap<>();
                for(Double vot: votProportion.keySet()){
                    temp.put(vot, 0.0);
                    temp2.put(vot, 0.0);
                }
                ls.setSpFlow(temp);
                ls.setFlow(temp2);
            }
        }
        //define probability of all node states and define them
        for (Node n: graph.getNodes()){
            n.initializeInformationVector(null, null);
        }
        //
    }
    
    public void printLinkStateFlows(){
        String epochTime = Integer.toString((int)(System.currentTimeMillis()/1000));
        try(PrintWriter fileIn= new PrintWriter("Networks/"+netName +"/"+"LinkStateFlows_"+epochTime+".txt")){
            fileIn.println("Link\tLinkState\tVOT\tVOTFlow\tTotalCostForThisVOT");
            for(Link l: graph.getLinks()){
                for(LinkState ls: l.getStates()){
                    for(Double vot: votProportion.keySet()){
                        fileIn.println(l+"\t"+ls.getID()+"\t"+vot+"\t"+ ls.getFlow().get(vot)+"\t"+ls.getCost(vot));
                    }
                }
            }
            
            fileIn.close();
            fileIn.flush();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        try(PrintWriter fileIn= new PrintWriter("Networks/"+netName +"/"+"TotalLinkStateFlows_"+epochTime+".txt")){
            fileIn.println("Link\tLinkState\tFlow\tProb");
            for(Link l: graph.getLinks()){
                fileIn.print(l);
                double totalFlow=0.0;
                for(LinkState ls: l.getStates()){
                    double totFlow=0.0;
                    for(Double vot: votProportion.keySet()){
                        totFlow+= ls.getFlow().get(vot);
                    }
//                    fileIn.print("\t"+ls.getID()+"\t"+totFlow+"\t"+ls.getProbOfOccurence());
                    totalFlow+= totFlow*ls.getProbOfOccurence();
                }
                fileIn.print("\t"+totalFlow+"\n");
            }
            
            fileIn.close();
            fileIn.flush();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
    }

    @Override
    public String toString() {
        return getRelativeGap() + "\t" + SPTT + "\t" + TSTT+"\t"+SORobjective;
    }
    
    
    
}
