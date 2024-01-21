import java.util.Comparator;

public class Sorter implements Comparator<String[]> {

    private int index;

    public Sorter(int index) {
        this.index = index;
    }

    @Override
    public int compare(String[] row1, String[] row2) {

        if(row1[0].equals("Make")){
            return 0;
        }
        // Assuming the values in the specified column are integers
        int value1 = Integer.parseInt(row1[index]);
        int value2 = Integer.parseInt(row2[index]);

        return Integer.compare(value1, value2);
    }
}
