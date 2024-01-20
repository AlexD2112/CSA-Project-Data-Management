public class Main {
    public static void main(String[] args) {
        ClassicsTrader classicsTrader = new ClassicsTrader(1000);

        try {
            classicsTrader.saveToCSV(true);
        } catch (Exception e) {
            System.out.println("Error saving to CSV");
            System.out.println(e.getMessage());
        }
    }
}