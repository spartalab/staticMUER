package static_muer.network;

import java.util.*;

public class TripTable{
    private final Map<Node, Map<Node, Map<Double, Double>>> trips; //dest --> orig --> VOT --> trips

    private final Set<Node> origins;
    private final Set<Node> dests;

    public TripTable(){
        trips = new HashMap<>();
        origins = new HashSet<>();
        dests = new HashSet<>();

    }

    public Map<Double, Double> getODPair(Node origin, Node dest)
    {
        if(!trips.containsKey(dest))
        {
            System.out.println("Triptable: origin was nor found\t"+origin);
            System.exit(1);
        } else if (!trips.get(dest).containsKey(origin))
        {
            System.out.println("Triptable: dest was nor found\t"+origin);
            System.exit(1);
        }
        return trips.get(dest).get(origin);
    }


    public void addODpair(Node o, Node d, double demand, Map<Double, Double> VOTdist){
        Map<Double, Double> temp = new HashMap<>();
        for(Double vot: VOTdist.keySet()){
            temp.put(vot, demand*(VOTdist.get(vot)));
        }
        
        Map<Node, Map<Double, Double>> temp2;

        if(trips.containsKey(d)){
            temp2 = trips.get(d);
        }
        else{
            trips.put(d, temp2 = new HashMap<>());
        }
        temp2.put(o, temp);

        if (!origins.contains(o)){
            origins.add(o);
        }

        if (!dests.contains(d)){
            dests.add(d);
        }
    }

    public Set<Node> getOrigins(){
        return origins;
    }

    public Set<Node> getDests(){
        return dests;
    }

    public Map<Node, Map<Node, Map<Double, Double>>> getTrips(){
        return trips;
    }
    
    public void printThis(){
        System.out.println("Origin \t Dest \t VOT \t Demand");
        for(Node dest: trips.keySet()){
            for(Node orig: trips.get(dest).keySet()){
                for(Double vot: trips.get(dest).get(orig).keySet()){
                    System.out.println(orig+"\t"+dest+"\t"+vot+"\t"
                            +trips.get(dest).get(orig).get(vot));
                }
            }
        }
    }


//    public Iterable<ODPair> byOrigin(Node origin)
//    {
//        return new MapValueIterable<ODPair>(trips.get(origin));
//    }
//
//    public Iterator<ODPair> iterator()
//    {
//        return new TripTableIterator();
//    }
//
//    class TripTableIterator implements Iterator<ODPair>
//    {
//        private Iterator<ODPair> inner;  // iterates over destinations of each origin and returns the associated ODpair object
//        private Iterator<Node> outer;  // iterates over origins
//
//        public TripTableIterator()
//        {
//            outer = trips.keySet().iterator();
//            inner = new MapValueIterator<ODPair>(trips.get(outer.next()));
//        }
//        public boolean hasNext()
//        {
//            return inner.hasNext() || outer.hasNext();
//        }
//
//        public ODPair next()
//        {
//            if(inner.hasNext())
//            {
//                return inner.next();
//            }
//            else if(outer.hasNext())
//            {
//                inner = new MapValueIterator<ODPair>(trips.get(outer.next()));
//                return inner.next();
//            }
//            else
//            {
//                return null;
//            }
//        }
//
//        public void remove()
//        {
//            throw new RuntimeException("Unsupported");
//        }
//
//    }
}