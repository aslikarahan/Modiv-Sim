public class Main {
    public static void main(String[] args){
        System.out.println("selamlar");
        ModivSim.getInstance().readInputFile("input");
        ModivSim.getInstance().startNodeThreads();

        System.out.println("Heyos");
    }
}
