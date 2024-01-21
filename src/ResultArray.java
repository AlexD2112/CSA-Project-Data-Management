import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultArray {
    //order of the headers in the csv file
    //Make,Model,Year,HP,Mileage,Price,Matching Serial Numbers

    public int xVar = 2;//sets the x variable to the second column of the header, Year
    int[] xVarArray = new int[10];
    public int yVar = 3;//sets the y variable to the third column of the header, HP
    int[] yVarArray = new int[10];

    public void Xaxis (String[][] data){
        Arrays.sort(data, new Sorter(xVar));//sorts the array by whatever numerical data you want it to
        int rows = data.length;
        for (int i = 9; i <= rows; i+=rows/10) {//creates a 1D array of 10 equal groups RHB, i=0 for LHB
            xVarArray[i/10] = Integer.parseInt(data[i][xVar]);
        }
    }

    //same as Xaxis but for the Y axis
    public void Yaxis (String[][] data){
        Arrays.sort(data, new Sorter(yVar));
        int rows = data.length;
        for (int i = 9; i <= rows; i+=rows/10) {//creates a 1D array of 10 equal groups RHB, i=0 for LHB
            yVarArray[i/10] = Integer.parseInt(data[i][yVar]);
        }
    }

    //Finds all cars that are within the bounds of the 10 groups on the X and Y axis and compares them to each other with the header defined
    //in this case by the Z variable

    public int zVar = 5;//sets the z variable to the fifth column of the header, Price
    public double zMean = 0;
    public int zMedian = 0;
    public double zDiff = 0;
    Double[][] resultsArray = new Double[10][10];

    public Double[][] results(String[][] data){
        Xaxis(data);
        Yaxis(data);
        for(int i=0; i<10; i++){//iterates through the 10 groups on the X axis
            for(int j=0; j<10; j++) {//iterates through the 10 groups on the Y axis
                int num =0;
                List<Integer> kCarList = new ArrayList<>();//creates a list of the cars that are within the bounds of both groups
                for(int k = 0; k<data.length; k++) {
                    if(!data[k][zVar].equals("N/A")){
                        if (Integer.parseInt(data[k][xVar]) <= xVarArray[i] && Integer.parseInt(data[k][yVar]) <= yVarArray[j]) {
                            num++;
                            zMean += Double.parseDouble(data[k][zVar])/num;
                            kCarList.add(k);
                        }
                    }
                }
                zMedian = kCarList.get(kCarList.size()/2);
                zDiff = Math.abs(zMedian - zMean);//finds the difference between the mean and median of the cars within the bounds of the groups
                resultsArray[i][j] = zDiff/zMedian;//divides the difference by the median to get a percentage difference
            }
        }
        return resultsArray;
    }
}
