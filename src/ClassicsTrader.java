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
        this(100);
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
        links[0] = "https://www.classic-trader.com/uk/cars/listing/ferrari/testarossa/testarossa/1985/332971";
        //Set first element to be https://www.classic-trader.com/uk/cars/listing/alfa-romeo/giulietta/giulietta-sz/1960/332964
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
            System.out.println((j+1) + "/" + numCars + " " + link);
            doubleArray[j] = grabCarData(link);
            j++;
        }

        //Log double array
        for (Object[] array : doubleArray) {
            System.out.println(Arrays.toString(array));
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

    //Returns, in order, Make, Model, Year, HP, Mileage, Price, and whether or not its "matching serial numbers" (i.e. no parts have been replaced)
    private Object[] grabCarData(String link) {
        Object[] carArray = new Object[7];

        String HTML;
        try {
            HTML = Jsoup.connect(link).get().html();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //System.out.println(HTML);

        //Get Make and Model- String value of the class right after the class with value "Make" and "Model". In between there is a </dt> tag, and than the <dd class etc > header for the value
        String make = getAttrValue(HTML, "Make");
        String model = getAttrValue(HTML, "Model");
        if (make.equals("Notprovided")) {
            carArray[0] = "N/A";
        } else {
            carArray[0] = make;
        }
        if (model.equals("Notprovided")) {
            carArray[1] = "N/A";
        } else {
            carArray[1] = model;
        }

        //Get Year
        String year = getAttrValue(HTML, "Year of manufacture");
        if (year.equals("Notprovided")) {
            carArray[2] = "N/A";
        } else {
            carArray[2] = Integer.parseInt(year);
        }

        //Get HP- after found, needs to be split at / and than the second number is the HP
        String hp = getAttrValue(HTML, "Power (kW/hp)");
        if (hp.equals("Notprovided")) {
            carArray[3] = "N/A";
        } else {
            if (hp.contains("Factory:")) {
                hp = hp.split("Factory:")[1];
                //Replace spaces and parentheses
                hp = hp.replaceAll("\\(", "").replaceAll("\\)", "");
            }
            carArray[3] = Integer.parseInt(hp.split("/")[1]);
        }

        //Get Mileage
        String mileageStr = getAttrValue(HTML, "Mileage (read)");
        if (mileageStr.equals("Notprovided")) {
            carArray[4] = "N/A";
        } else {
            //If contains mi, remove mi and convert to int converted to km
            //Otherwise just remove km
            int mileage = getMileage(mileageStr);
            carArray[4] = mileage;
        }

        //Get Price
        if (HTML.contains("Price on request")) {
            carArray[5] = "N/A";
        } else {
            //Search for pound symbol or euro symbol, grab value after
            boolean inEuro = false;
            int priceIndex = HTML.indexOf("£");
            if (priceIndex == -1) {
                priceIndex = HTML.indexOf("€");
                inEuro = true;
            }
            int price = Integer.parseInt(HTML.substring(priceIndex + 1, HTML.indexOf("<", priceIndex)).replaceAll(",", ""));
            if (inEuro) {
                price = (int) (price * 0.86);
            }
            carArray[5] = price;
        }

        //Get whether or not its matching serial numbers
        String matchingStatement = getAttrValue(HTML, "Matching numbers");
        if (matchingStatement.equals("Notprovided")) {
            carArray[6] = "N/A";
        } else {
            boolean matching = matchingStatement.equals("Yes");
            //If matching is null, print out matching statement
            carArray[6] = matching;
        }


        return carArray;
    }

    private static int getMileage(String mileageStr) {
        int mileage;
        if (mileageStr.contains("mls")) {
            mileage = (int) (Integer.parseInt(mileageStr.replaceAll("mls", "").replaceAll(",", "")) * 1.60934);
        } else {
            mileage = Integer.parseInt(mileageStr.replaceAll("km", "").replaceAll(",", ""));
        }
        return mileage;
    }

    private String getAttrValue(String HTML, String attr) {
        int attrIndex = HTML.indexOf(attr);
        int dtCloseIndex = HTML.indexOf("</dt>", attrIndex);
        int i = 0;
        while (dtCloseIndex - attrIndex > 100) {
            attrIndex = HTML.indexOf(attr, attrIndex + 1);
            dtCloseIndex = HTML.indexOf("</dt>", attrIndex);
            i++;
            if (i > 25) {
                System.out.println(attrIndex + " " + dtCloseIndex);
                System.out.println(HTML.substring(attrIndex, dtCloseIndex+5));
                throw new RuntimeException("Could not find attribute " + attr);
            }
        }
        int dtEndIndex = HTML.indexOf(">", attrIndex) + 1;
        int ddEndIndex = HTML.indexOf(">", dtEndIndex+1)+1;
        int ddCloseIndex = HTML.indexOf("</dd>", ddEndIndex);
        return HTML.substring(ddEndIndex, ddCloseIndex).replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");
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
