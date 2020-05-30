import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.*;

public class Node extends Thread{
    public int nodeID;
    public Hashtable<Integer, Integer> linkCost; //Neighbor id, link cost
    public Hashtable<Integer, Integer> linkBandwidth; //Neighbor id, link bandwidth
    public ConcurrentHashMap<Integer, Integer[]> distanceTable = new ConcurrentHashMap<>(); //Neighbor id, array of costs to each node
    public Integer[] bottleneckBandwidthTable;
    public final int infinite = 999;
    public static final String ANSI_RESET = "\u001B[30m";
    public Integer[] convergedDistanceVector;
    public int totalNumberOfNodes;
    public boolean converged = false;
    public int roundNumber;
    final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth) {
        this.roundNumber = 0;
        this.nodeID = nodeID;
        this.linkCost = linkCost;
        this.linkBandwidth = linkBandwidth;
        this.totalNumberOfNodes = ModivSim.getInstance().nodeNumber;
        constructDistanceTable();
        bottleneckBandwidthTable = new Integer[totalNumberOfNodes];
        this.convergedDistanceVector = new Integer[totalNumberOfNodes];
        Arrays.fill(convergedDistanceVector, infinite);
    }

    private void constructDistanceTable() {
        for(int neighborID : linkCost.keySet()){
            Integer[] distances = new Integer[totalNumberOfNodes];
            Arrays.fill(distances, infinite);
            distances[neighborID] = linkCost.get(neighborID);
            distances[nodeID] = 0;
            distanceTable.put(neighborID, distances);
        }
    }

    public synchronized void receiveUpdate(Message m){
        int senderID = m.senderID;
        Integer[] newDistanceVector = new Integer[totalNumberOfNodes];
        Arrays.fill(newDistanceVector, infinite);
//        System.out.println(m + " link cost is " + linkCost.get(senderID));
        for(int i = 0; i < newDistanceVector.length; i++){
            if(m.distanceVector[i] != infinite)
                newDistanceVector[i] = m.distanceVector[i] + linkCost.get(senderID);
        }
        newDistanceVector[nodeID] = 0;
        distanceTable.put(senderID, newDistanceVector);
//        System.out.println( Arrays.toString(newDistanceVector));
    }
    public void run(){
        startScheduling();
    }

    public boolean sendUpdate(){
        printDistanceTable();
        roundNumber++;
        Integer[] outgoingDistanceVector = constructDistanceVector();

        if(Arrays.equals(outgoingDistanceVector, convergedDistanceVector)){
            converged = true;
        }else{
            System.arraycopy(outgoingDistanceVector, 0, convergedDistanceVector, 0, totalNumberOfNodes);
        }

        for(int neighborID :linkCost.keySet()){
            Message m = new Message(nodeID, neighborID, linkBandwidth.get(neighborID), outgoingDistanceVector, converged, roundNumber);
            //System.out.println(m.toString());
            ModivSim.forwardMessage(m);
        }
        return true;
    }

    private Integer[] constructDistanceVector() {
        Integer[] distanceVectorInitial = new Integer[totalNumberOfNodes];
        Arrays.fill(distanceVectorInitial, infinite);

        for (Integer[] tableEntry : distanceTable.values()) {
            for(int i = 0; i < totalNumberOfNodes; i++) {
                distanceVectorInitial[i] = Math.min(distanceVectorInitial[i], tableEntry[i]);
            }
        }
        return distanceVectorInitial;
    }

    public Hashtable<String, Integer[]> getForwardingTable(){
        Hashtable<String, Integer[]> hashtable = new Hashtable<>();
        for(int i = 0; i < totalNumberOfNodes; i++) {
            int min1 = infinite;
            int min2 = infinite;
            int neighbor1 = infinite;
            int neighbor2 = infinite;
            if (i == nodeID)
                continue;
            for (Integer neighborID : distanceTable.keySet()) {
                if(distanceTable.get(neighborID)[i] < min1){
                    min2 = min1;
                    neighbor2 = neighbor1;
                    min1 = distanceTable.get(neighborID)[i];
                    neighbor1 = neighborID;
                }else if(distanceTable.get(neighborID)[i] < min2){
                    min2 = distanceTable.get(neighborID)[i];
                    neighbor2 = neighborID;
                }
            }
            Integer[] bestChoices = new Integer[2];
            bestChoices[0] = neighbor1;
            bestChoices[1] = neighbor2;
            hashtable.put(String.valueOf(i), bestChoices);
        }
        return hashtable;
    }

    public void startScheduling(){
        ScheduledFuture scheduledFuture =
                scheduler.scheduleAtFixedRate(new Runnable() {
                                                  public void run(){
                                                      sendUpdate();
                                                  }
                                              },
                        5,10,
                        TimeUnit.SECONDS);
    }


    public synchronized void printDistanceTable() {
        String firstLine = ANSI_RESET + ModivSim.getInstance().getColor(nodeID) + "\nPrinting distance table for node " + nodeID + ": \nDest \t|";

        for(int i = 0; i < totalNumberOfNodes; i++){
            firstLine = firstLine + "\t " + i;
        }
       firstLine = firstLine + "\n----------------------------------------";

        for(Integer neighbor : distanceTable.keySet()){
            firstLine = firstLine + "\nNeighbor " + neighbor.toString() + "\t" + Arrays.toString(distanceTable.get(neighbor)) ;
        }
        firstLine = firstLine + ANSI_RESET;

        System.out.println(firstLine);
    }
}
