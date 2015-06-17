package flatlc.realdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * User: endresma
 * Date: 26.05.14
 * Time: 11:15
 */
public class GetMaximumValues {

    private static ArrayList<int[]> rawData = new ArrayList<>();





    private static void printObject(int[] tuple) {
        for (int i = 0; i < tuple.length; i++) {
            System.out.print(tuple[i] + " ");
        }
        System.out.println();
    }


    /**
     * set min and max values for the data set
     * This is equal to set the maxLevels
     */
    public static void setMinMax(int col) {

        int[] tuple;

//        double[] min = new double[col];
        // note that the first element is the ID
        int[] max = new int[col];

        for (int i = 0; i < rawData.size(); i++) {
            tuple = rawData.get(i);
//            setMin(min, tuple);
            setMax(max, tuple);
        }

        printObject(max);
    }

    private static void setMin(double[] min, double[] tuple) {
        if (min.length != tuple.length)
            throw new RuntimeException("Wrong size of arrays");
        for (int i = 0; i < min.length; i++) {
            min[i] = min[i] < tuple[i] ? min[i] : tuple[i];
        }
    }


    // note that the first component is the ID
    private static void setMax(int[] max, int[] tuple) {
        if (max.length != tuple.length)
            throw new RuntimeException("Wrong size of arrays");
        for (int i = 0; i < max.length; i++) {
            max[i] = max[i] > tuple[i] ? max[i] : tuple[i];
        }


    }


    public static void readRawData(Scanner sc, int rows, int col) {

        // read raw data into ArrayList
        for (int i = 0; i < rows; ++i) {
            int[] tuple = new int[col];
//            System.out.println("Row = " + i);
            for (int j = 0; j < col; ++j) {
                double val = sc.nextDouble();

                tuple[j] = (int) val;
            }
            rawData.add(tuple);
            sc.nextLine();
        }

//        find min/max for each column
        setMinMax(col);

    }


    public static String getFilePath(String filename) {
        String canonicalPath = null;
        try {
            canonicalPath = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String prefix = canonicalPath + "/FlatLCDataGenerator/data/";


        return (prefix + filename);
    }


    /**
     * Open file and initiate scanner /**
     *
     * @return sc
     */
    public static Scanner getScanner(String filename) {
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to read file: " + filename);
        }
        sc.useLocale(Locale.US);
        return sc;
    }


    public static void main(String[] args) {

        String file = "zillow_mod_Scalagon.txt";
//        String file = "test.txt";
        int rows = 2245109;
        //int rows = 1500000;
        int col = 7;


        String filePath = getFilePath(file);
        Scanner sc = getScanner(filePath);

        readRawData(sc, rows, col);


    }


}
