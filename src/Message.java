import java.util.Arrays;

public class Message {
    public int senderID;
    public int receiverID;
    public int linkBandwidth;
    public Integer[] distanceVector;
    public boolean convergence;

    @Override
    public String toString() {
        return "Message{" +
                "senderID=" + senderID +
                ", receiverID=" + receiverID +
                ", linkBandwidth=" + linkBandwidth +
                ", distanceVector=" + Arrays.toString(distanceVector) +
                '}';
    }

    public Message(int senderID, int receiverID, int linkBandwidth, Integer[] distanceVector, boolean converged) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.linkBandwidth = linkBandwidth;
        this.distanceVector = distanceVector;
        this.convergence = converged;
    }
}
