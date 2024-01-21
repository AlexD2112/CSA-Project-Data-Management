import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


/*
*Main class for working with CSV files using OpenCSV
*/

public class CSVWorker {
    public String[][] CSVToArray(Path csvName) throws IOException, CsvException {
        try (Reader reader = Files.newBufferedReader(csvName)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                //Makes a list of string arrays from the csv file
                List<String[]> linesList = csvReader.readAll();

                // Convert the list to a two-dimensional array
                String[][] linesArray = new String[linesList.size()][];
                linesList.toArray(linesArray);

                return linesArray;
            }
        }
    }

}
