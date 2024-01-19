import org.jsoup.Jsoup;

import java.io.IOException;

public class ClassicsTrader {
    private static final String CT_SEARCH_URL = "https://www.classic-trader.com/uk/cars/search";
    private static final String CSV_Path = "src/ClassicsTrader.csv";
    /**
     * Create new ClassicsTrader class grabbing 100 cars
     *
     * This class is responsible for grabbing data from ClassicsTrader.com
     * and storing it in a CSV file.
     */
    public ClassicsTrader() {
        this(3);
    }
    /**
     * Create new ClassicsTrader class grabbing numCars cars
     *
     * This class is responsible for grabbing data from ClassicsTrader.com
     * and storing it in a CSV file.
     *
     * @param numCars The number of cars to grab
     */
    public ClassicsTrader(int numCars) {
        Object[][] doubleArray = grabAllData(numCars);
    }
    private Object[][] grabAllData(int numCars) {
        Object[][] doubleArray = new Object[numCars][];
        String HTML;

        try {
            HTML = Jsoup.connect(CT_SEARCH_URL).get().html();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Get links to numCars cars from the search page
        String[] links = new String[numCars];
        //Find fifth astro-island element on screen, and make a new subelement that is just that element
        String CarsHTML = HTML.substring(HTML.indexOf("astro-island") + 13);
        CarsHTML = CarsHTML.substring(CarsHTML.indexOf("astro-island") + 13);
        CarsHTML = CarsHTML.substring(CarsHTML.indexOf("astro-island") + 13);
        CarsHTML = CarsHTML.substring(CarsHTML.indexOf("astro-island") + 13);
        CarsHTML = CarsHTML.substring(CarsHTML.indexOf("astro-island") + 13);
        CarsHTML = CarsHTML.substring(0, CarsHTML.indexOf("astro-island") + 13);

        int carsLeft = numCars;
        System.out.println(CarsHTML);
        while (carsLeft > 0) {
            //The CarsHTML array should contain several mx-auto class elements with a data-href attribute
            //Check if one exists, and than take the value of it to link. If none exist, do something else
            if (CarsHTML.contains("data-href")) {
                //Get the link
                CarsHTML = CarsHTML.substring(CarsHTML.indexOf("data-href") + 11);
                String link = CarsHTML.substring(0, CarsHTML.indexOf("\""));
                links[numCars - carsLeft] = link;
                carsLeft--;
            } else {
                System.out.println("Nonexistent");
                carsLeft = 0;
            }


            numCars--;
        }

        for (int i = 0; i < links.length; i++) {
            System.out.println(links[i]);
        }

        return doubleArray;
    }
    //Grab data for a single car from ClassicsTrader
    private Object[] grabCarData() {
        Object[] singleArray = new Object[1];
        return singleArray;
    }
    /**
     * Get the path to the CSV file
     *
     * @return The path to the CSV file
     */
    public String getCSVPath() {
        return CSV_Path;
    }
}
