import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Arrays;
import java.util.Arrays.*;
import java.util.HashMap;
import java.util.Map;

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

        //Get links from the search page
        String[] links = getLinksOnPage(HTML);
        int page = 2;
        while (links.length < numCars) {
            String newHTML;
            try {
                newHTML = Jsoup.connect(CT_SEARCH_URL + "?page=" + page).get().html();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] newLinks = getLinksOnPage(newHTML);
            String[] combinedLinks = new String[links.length + newLinks.length];
            System.arraycopy(links, 0, combinedLinks, 0, links.length);
            System.arraycopy(newLinks, 0, combinedLinks, links.length, newLinks.length);
            links = combinedLinks;
        }

        if (links.length > numCars) {
            links = Arrays.copyOfRange(links, 0, numCars);
        }

        //Grab Car Data and put it in the array
        int j = 0;
        for (String link : links) {
            doubleArray[j] = grabCarData();
            j++;
        }

        return doubleArray;
    }
    //Grab data for a single car from ClassicsTrader

    private String[] getLinksOnPage(String HTML) {
        //Create a new map of strings to arrays of ints
        Map<String, int[]> map = new HashMap<>();
        //Find every instance of the string "https://www.classic-trader.com/uk/cars/listing/" in the HTML. Then grab the link that goes from here until &quot or ". This is the link. If this is not yet in the hashmap, add it, otherwise add the index where it was found.

        int index = 0;
        while (HTML.indexOf("https://www.classic-trader.com/uk/cars/listing/", index) != -1) {
            index = HTML.indexOf("https://www.classic-trader.com/uk/cars/listing/", index);
            int end = HTML.indexOf("&quot", index);
            int altEnd = HTML.indexOf("\"", index);
            if (altEnd < end) {
                end = altEnd;
            }
            String link = HTML.substring(index, end);
            if (map.containsKey(link)) {
                int[] array = map.get(link);
                if (array.length == 2) {
                    break;
                }
                int[] newArray = new int[array.length + 1];
                for (int i = 0; i < array.length; i++) {
                    newArray[i] = array[i];
                }
                newArray[array.length] = index;
                map.put(link, newArray);
            } else {
                map.put(link, new int[]{index});
            }
            index += 1;
        }

        //Get the links from the map
        String[] links = new String[map.size()];
        int i = 0;
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            links[i] = entry.getKey();
            i++;
        }
        return links;
    }

    //Returns, in order, Make, Model, Year, HP, Mileage, Price, and Description
    private Object[] grabCarData() {
        Object[] carArray = new Object[6];
        return carArray;
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
