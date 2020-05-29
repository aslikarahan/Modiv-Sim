import java.util.Hashtable;

public class Node {
    public int nodeID;
    public Hashtable<Integer, Integer> linkCost; //Neighbor id, link cost
    public Hashtable<Integer, Integer> linkBandwidth; //Neighbor id, link bandwidth
    public int[][] distanceTable;
    public int[] bottleneckBandwidthTable;
    public final int infinite = 999;


    public Node(int nodeID, Hashtable<Integer, Integer> linkCost, Hashtable<Integer, Integer> linkBandwidth) {
        this.nodeID = nodeID;
        this.linkCost = linkCost;
        this.linkBandwidth = linkBandwidth;
    }

}
