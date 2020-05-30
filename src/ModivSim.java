import java.io.*;
import java.util.*;

public class ModivSim {
    private static ModivSim singletonInstance;
    public static ArrayList<Node> nodes = new ArrayList<>();
    public static int nodeNumber;
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public List<String> colorlist = new ArrayList<>();
    public static boolean[] convergenceArray;
    public static boolean globalConvergence = false;


    private ModivSim() {
//        colorlist.add(ANSI_BLACK_BACKGROUND);
        colorlist.add(ANSI_RED_BACKGROUND);
        colorlist.add(ANSI_GREEN_BACKGROUND);
//        colorlist.add(ANSI_YELLOW_BACKGROUND);
        colorlist.add(ANSI_BLUE_BACKGROUND);
        colorlist.add(ANSI_PURPLE_BACKGROUND);
        colorlist.add(ANSI_CYAN_BACKGROUND);
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

    public synchronized static void forwardMessage(Message m) {
        int targetNodeID = m.receiverID;
        int senderID = m.senderID;
        convergenceArray[senderID] = m.convergence;
        if(!globalConvergence) {
            int counter = 0;
            for (boolean t : convergenceArray) {
                if (t)
                    counter++;
            }
            if (counter == nodeNumber) {
                globalConvergence = true;
                System.out.println("All nodes converged!");
                for (Node node : nodes){
                    Hashtable<String, Integer[]> forwardingTable = node.getForwardingTable();
                    printForwardingTable(forwardingTable, node.nodeID);
                }
            }
        }
        nodes.get(targetNodeID).receiveUpdate(m);
    }

    private static void printForwardingTable(Hashtable<String, Integer[]> forwardingTable, int nodeID) {

    }

    public void readInputFile(String directory){
        File dir = new File(directory);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".txt");
            }
        });
        nodeNumber = files.length;
        convergenceArray = new boolean[nodeNumber];
        Arrays.fill(convergenceArray, false);
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
    public String getColor(int i){
        return colorlist.get(i);
    }


}
