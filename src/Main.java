public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        ClassicsTrader classicsTrader = new ClassicsTrader(100);

        try {
            classicsTrader.saveToCSV(true);
        } catch (Exception e) {
            System.out.println("Error saving to CSV");
            System.out.println(e.getMessage());
        }
    }
}