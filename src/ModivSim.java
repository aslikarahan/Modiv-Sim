import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class ModivSim {
    private static ModivSim singletonInstance;
    public static ArrayList<Node> nodes = new ArrayList<>();
    public int nodeNumber;

    private ModivSim() {
    }
    public static ModivSim getInstance() {
        if(singletonInstance == null) {
            synchronized (ModivSim.class) {
                if(singletonInstance == null) {
                    singletonInstance = new ModivSim();
                }
            }
        }
        return singletonInstance;
    }

    public static void forwardMessage(Message m) {
        int targetNodeID = m.receiverID;
        nodes.get(targetNodeID).receiveUpdate(m);
    }

    public void readInputFile(String directory){
        File dir = new File(directory);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".txt");
            }
        });
        nodeNumber = files.length;
        for(File f : files){
            try {
                FileInputStream fis=new FileInputStream(directory +"/" +f.getName());
                Scanner sc=new Scanner(fis);
                while(sc.hasNextLine()) {
                    String[] temp = sc.nextLine().split("[\\(||\\)||,]");
                    ArrayList<Integer> parameters = new ArrayList<Integer>();
                    for(String s : temp){
                        if (s.length()!=0){
                            parameters.add(Integer.parseInt(s));
                        }
                    }
                    int numberOfNodes = parameters.size() / 3;
                    int nodeID = parameters.get(0);
                    Hashtable<Integer, Integer> linkCost = new Hashtable<Integer, Integer>();
                    Hashtable<Integer, Integer> linkBandwidth = new Hashtable<Integer, Integer>();
                    for(int i = 0; i < numberOfNodes; i ++){
                        int neighborID = parameters.get(3 * i + 1);
                        int neighborLinkCost = parameters.get(3 * i + 2);
                        int neighborBandwidth = parameters.get(3 * i + 3);
                        linkCost.put(neighborID, neighborLinkCost);
                        linkBandwidth.put(neighborID, neighborBandwidth);
                    }
                    nodes.add(new Node(nodeID, linkCost, linkBandwidth));
                    }
                sc.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void startNodeThreads(){
        for(Node n : this.nodes){
            n.start();
        }
    }

}
