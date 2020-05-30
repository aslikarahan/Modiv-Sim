import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Node extends Thread{
    public int nodeID;
    public Hashtable<Integer, Integer> linkCost; //Neighbor id, link cost
    public Hashtable<Integer, Integer> linkBandwidth; //Neighbor id, link bandwidth
    public HashMap<Integer, Integer[]> distanceTable = new HashMap<>(); //Neighbor id, array of costs to each node
    public Integer[] bottleneckBandwidthTable;
    public final int infinite = 999;
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

    public void receiveUpdate(Message m){
        int senderID = m.senderID;
        int[] newDistanceVector = m.distanceVector;

        for(int i = 0; i < newDistanceVector.length; i++){

        }
    }
    public void run(){
        startScheduling();
    }

    public boolean sendUpdate(){
        int[] distanceVector = constructDistanceVector();

        for(int neighborID :linkCost.keySet()){
            Message m = new Message(nodeID, neighborID, linkBandwidth.get(neighborID), distanceVector);
            ModivSim.forwardMessage(m);
        }
        return true;
    }

    private int[] constructDistanceVector() {
        int[] distanceVector = new int[totalNumberOfNodes];
        Arrays.fill(distanceVector, infinite);

        for (Integer[] tableEntry : distanceTable.values()) {
            for(int i = 0; i < totalNumberOfNodes; i++) {
                distanceVector[i] = Math.min(distanceVector[i], tableEntry[i]);
            }
        }

        return distanceVector;
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
                        5,30,
                        TimeUnit.SECONDS);
    }


}
