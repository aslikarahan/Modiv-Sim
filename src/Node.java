import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    public int totalNumberOfNodes;
    final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth) {
        this.nodeID = nodeID;
        this.linkCost = linkCost;
        this.linkBandwidth = linkBandwidth;
        this.totalNumberOfNodes = ModivSim.getInstance().nodeNumber;
        constructDistanceTable();
        bottleneckBandwidthTable = new Integer[totalNumberOfNodes];
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
        Integer[] outgoingDistanceVector = constructDistanceVector();

        for(int neighborID :linkCost.keySet()){
            Message m = new Message(nodeID, neighborID, linkBandwidth.get(neighborID), outgoingDistanceVector);
            System.out.println(m.toString());
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

    public Hashtable<Integer, Integer[]> getForwardingTable(){

        return null;
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



}
