import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        //ClassicsTrader classicsTrader = new ClassicsTrader(100);
        CSVWorker worker = new CSVWorker();
        ResultArray resultArray = new ResultArray();


/*        try {
            classicsTrader.saveToCSV(true);
        } catch (Exception e) {
            System.out.println("Error saving to CSV");
            System.out.println(e.getMessage());
        }*/

        try{
            String [][] array = worker.CSVToArray(Path.of("src/ClassicsTrader.csv"));
            //prints the resulting difference beteween the mean and median of the compared result "z" variable
            for (Double[] doubles : resultArray.results(array)) {
                for (Double aDouble : doubles) {
                    System.out.print(aDouble + " ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Error reading from CSV");
            System.out.println(e.getMessage());
        }
    }
}