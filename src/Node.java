import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Node extends Thread{
    public int nodeID;
    public Hashtable<Integer, Integer> linkCost; //Neighbor id, link cost
    public Hashtable<Integer, Integer> linkBandwidth; //Neighbor id, link bandwidth
    public int[][] distanceTable;
    public int[] bottleneckBandwidthTable;
    public final int infinite = 999;
    final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth) {
        this.nodeID = nodeID;
        this.linkCost = linkCost;
        this.linkBandwidth = linkBandwidth;
    }
    public void receiveUpdate(Message m){

    }
    public void run(){
        startScheduling();
    }

    public boolean sendUpdate(){

        return true;
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
