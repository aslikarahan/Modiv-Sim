public class Message {
    public int senderID;
    public int receiverID;
    public int linkBandwidth;
    public int[] distanceVector;

    public Message(int senderID, int receiverID, int linkBandwidth, int[] distanceVector) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.linkBandwidth = linkBandwidth;
        this.distanceVector = distanceVector;
    }
}
